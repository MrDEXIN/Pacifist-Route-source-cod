package name.modid.event;

import name.modid.item.ModItems;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class PlayerHeadInteractionHandler implements UseBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack heldItem = player.getStackInHand(hand);

        // Проверяем оба типа головы игрока
        if ((state.getBlock() == Blocks.PLAYER_HEAD || state.getBlock() == Blocks.PLAYER_WALL_HEAD) && isAxe(heldItem.getItem())) {
            if (!world.isClient) {
                heldItem.damage(1, player, (user) -> user.sendToolBreakStatus(hand));

                BlockState skeletonSkullState;
                if (state.getBlock() == Blocks.PLAYER_HEAD) {
                    int rotation = state.get(Properties.ROTATION);
                    skeletonSkullState = Blocks.SKELETON_SKULL.getDefaultState().with(Properties.ROTATION, rotation);
                } else {
                    var facing = state.get(Properties.HORIZONTAL_FACING);
                    skeletonSkullState = Blocks.SKELETON_WALL_SKULL.getDefaultState().with(Properties.HORIZONTAL_FACING, facing);
                }

                world.setBlockState(pos, skeletonSkullState);

                world.playSound(null, pos, SoundEvents.ENTITY_SLIME_ATTACK, SoundCategory.BLOCKS, 1.0F, 1.0F);

                Vector3f redColor = new Vector3f(1.0F, 0.0F, 0.0F);
                DustParticleEffect dustParticleEffect = new DustParticleEffect(redColor, 1.0F);

                ((ServerWorld) world).spawnParticles(
                        dustParticleEffect,
                        pos.getX() + 0.5,
                        pos.getY() + 0.3,
                        pos.getZ() + 0.5,
                        400,
                        0.2,
                        0.2,
                        0.2,
                        0.1
                );

                int skinAmount = getRandomSkinAmount(world.getRandom());
                ItemStack humanSkinStack = new ItemStack(ModItems.HUMAN_SKIN, skinAmount);
                ItemEntity humanSkinEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, humanSkinStack);
                world.spawnEntity(humanSkinEntity);
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private int getRandomSkinAmount(Random random) {
        double chance = random.nextDouble();
        if (chance < 0.25) return 1;
        else if (chance < 0.55) return 2;
        else if (chance < 0.85) return 3;
        else return 4;
    }

    private boolean isAxe(Item item) {
        return item == Items.WOODEN_AXE ||
                item == Items.STONE_AXE ||
                item == Items.IRON_AXE ||
                item == Items.GOLDEN_AXE ||
                item == Items.DIAMOND_AXE ||
                item == Items.NETHERITE_AXE;
    }
}