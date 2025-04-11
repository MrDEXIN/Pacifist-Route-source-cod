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

public class SkeletonSkullInteractionHandler implements UseBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack heldItem = player.getStackInHand(hand);

        // Проверяем оба типа черепа скелета
        if ((state.getBlock() == Blocks.SKELETON_SKULL || state.getBlock() == Blocks.SKELETON_WALL_SKULL) && heldItem.getItem() == Items.ROTTEN_FLESH) {
            if (!world.isClient) {
                heldItem.decrement(1);

                BlockState zombieHeadState;
                if (state.getBlock() == Blocks.SKELETON_SKULL) {
                    int rotation = state.get(Properties.ROTATION);
                    zombieHeadState = Blocks.ZOMBIE_HEAD.getDefaultState().with(Properties.ROTATION, rotation);
                } else {
                    var facing = state.get(Properties.HORIZONTAL_FACING);
                    zombieHeadState = Blocks.ZOMBIE_WALL_HEAD.getDefaultState().with(Properties.HORIZONTAL_FACING, facing);
                }

                world.setBlockState(pos, zombieHeadState);

                world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.BLOCKS, 1.0F, 1.0F);

                Vector3f redColor = new Vector3f(139.0F, 0.0F, 0.0F);
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