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

public class ZombieHeadToCreeperHandler implements UseBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack heldItem = player.getStackInHand(hand);

        // Проверяем оба типа головы зомби
        if ((state.getBlock() == Blocks.ZOMBIE_HEAD || state.getBlock() == Blocks.ZOMBIE_WALL_HEAD) && heldItem.getItem() == Items.GUNPOWDER) {
            if (!world.isClient) {
                heldItem.decrement(1);

                BlockState creeperHeadState;
                if (state.getBlock() == Blocks.ZOMBIE_HEAD) {
                    int rotation = state.get(Properties.ROTATION);
                    creeperHeadState = Blocks.CREEPER_HEAD.getDefaultState().with(Properties.ROTATION, rotation);
                } else {
                    var facing = state.get(Properties.HORIZONTAL_FACING);
                    creeperHeadState = Blocks.CREEPER_WALL_HEAD.getDefaultState().with(Properties.HORIZONTAL_FACING, facing);
                }

                world.setBlockState(pos, creeperHeadState);

                world.playSound(null, pos, SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);

                spawnColoredParticles((ServerWorld) world, pos, new Vector3f(0.5F, 1.0F, 0.0F));
                spawnColoredParticles((ServerWorld) world, pos, new Vector3f(0.0F, 1.0F, 0.0F));
                spawnColoredParticles((ServerWorld) world, pos, new Vector3f(0.0F, 0.5F, 0.0F));
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private void spawnColoredParticles(ServerWorld world, BlockPos pos, Vector3f color) {
        DustParticleEffect particleEffect = new DustParticleEffect(color, 1.0F);
        world.spawnParticles(
                particleEffect,
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
}