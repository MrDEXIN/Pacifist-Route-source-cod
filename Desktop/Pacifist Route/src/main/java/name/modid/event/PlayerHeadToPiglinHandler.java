package name.modid.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.world.World;
import org.joml.Vector3f;

public class PlayerHeadToPiglinHandler implements UseBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack heldItem = player.getStackInHand(hand);

        // Проверяем оба типа головы игрока
        if ((state.getBlock() == Blocks.PLAYER_HEAD || state.getBlock() == Blocks.PLAYER_WALL_HEAD) && heldItem.getItem() == Items.PORKCHOP) {
            if (!world.isClient) {
                heldItem.decrement(1);

                BlockState piglinHeadState;
                if (state.getBlock() == Blocks.PLAYER_HEAD) {
                    int rotation = state.get(Properties.ROTATION);
                    piglinHeadState = Blocks.PIGLIN_HEAD.getDefaultState().with(Properties.ROTATION, rotation);
                } else {
                    var facing = state.get(Properties.HORIZONTAL_FACING);
                    piglinHeadState = Blocks.PIGLIN_WALL_HEAD.getDefaultState().with(Properties.HORIZONTAL_FACING, facing);
                }

                world.setBlockState(pos, piglinHeadState);

                world.playSound(null, pos, SoundEvents.ENTITY_PIGLIN_AMBIENT, SoundCategory.BLOCKS, 1.0F, 1.0F);

                Vector3f redColor = new Vector3f(255.0F, 160.0F, 122.0F);
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
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}