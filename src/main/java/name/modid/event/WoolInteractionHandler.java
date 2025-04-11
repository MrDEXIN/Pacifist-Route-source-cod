package name.modid.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class WoolInteractionHandler implements UseBlockCallback {
    private static final Map<BlockPos, Integer> shearLevelMap = new HashMap<>();

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack heldItem = player.getStackInHand(hand);

        if (isWoolBlock(state.getBlock()) && heldItem.getItem() == Items.SHEARS) {
            if (!world.isClient) {
                heldItem.damage(1, player, (p) -> p.sendToolBreakStatus(hand));
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(Items.STRING, 1));
                world.spawnEntity(itemEntity);

                int currentShearLevel = shearLevelMap.getOrDefault(pos, 0);
                int newShearLevel = currentShearLevel + 1;

                if (newShearLevel >= 4) {
                    world.playSound(null, pos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    ((ServerWorld) world).spawnParticles(
                            new DustParticleEffect(getWoolColor(state.getBlock()), 1.0F),
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            20, 0.5, 0.5, 0.5, 0.1
                    );
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    shearLevelMap.remove(pos);
                } else {
                    shearLevelMap.put(pos, newShearLevel);
                    world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    ((ServerWorld) world).spawnParticles(
                            new DustParticleEffect(getWoolColor(state.getBlock()), 1.0F),
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            10, 0.5, 0.5, 0.5, 0.1
                    );
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static void registerBreakHandler() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (isWoolBlock(state.getBlock()) && shearLevelMap.containsKey(pos)) {
                if (!world.isClient) {
                    // Удаляем блок и предотвращаем дроп
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    shearLevelMap.remove(pos);
                    // Отменяем стандартное разрушение
                    return false;
                }
            }
            return true;
        });
    }

    public static boolean isWoolBlock(Block block) {
        return block == Blocks.WHITE_WOOL ||
                block == Blocks.ORANGE_WOOL ||
                block == Blocks.MAGENTA_WOOL ||
                block == Blocks.LIGHT_BLUE_WOOL ||
                block == Blocks.YELLOW_WOOL ||
                block == Blocks.LIME_WOOL ||
                block == Blocks.PINK_WOOL ||
                block == Blocks.GRAY_WOOL ||
                block == Blocks.LIGHT_GRAY_WOOL ||
                block == Blocks.CYAN_WOOL ||
                block == Blocks.PURPLE_WOOL ||
                block == Blocks.BLUE_WOOL ||
                block == Blocks.BROWN_WOOL ||
                block == Blocks.GREEN_WOOL ||
                block == Blocks.RED_WOOL ||
                block == Blocks.BLACK_WOOL;
    }

    public static Map<BlockPos, Integer> getShearLevelMap() {
        return shearLevelMap;
    }

    private Vector3f getWoolColor(Block block) {
        if (block == Blocks.WHITE_WOOL) return new Vector3f(1.0F, 1.0F, 1.0F);
        if (block == Blocks.ORANGE_WOOL) return new Vector3f(0.98F, 0.55F, 0.0F);
        if (block == Blocks.MAGENTA_WOOL) return new Vector3f(0.98F, 0.2F, 0.98F);
        if (block == Blocks.LIGHT_BLUE_WOOL) return new Vector3f(0.4F, 0.8F, 1.0F);
        if (block == Blocks.YELLOW_WOOL) return new Vector3f(1.0F, 1.0F, 0.0F);
        if (block == Blocks.LIME_WOOL) return new Vector3f(0.5F, 0.0F, 0.0F);
        if (block == Blocks.PINK_WOOL) return new Vector3f(1.0F, 0.6F, 0.7F);
        if (block == Blocks.GRAY_WOOL) return new Vector3f(0.3F, 0.3F, 0.3F);
        if (block == Blocks.LIGHT_GRAY_WOOL) return new Vector3f(0.7F, 0.7F, 0.7F);
        if (block == Blocks.CYAN_WOOL) return new Vector3f(0.0F, 0.8F, 0.8F);
        if (block == Blocks.PURPLE_WOOL) return new Vector3f(0.6F, 0.2F, 0.98F);
        if (block == Blocks.BLUE_WOOL) return new Vector3f(0.0F, 0.0F, 1.0F);
        if (block == Blocks.BROWN_WOOL) return new Vector3f(0.6F, 0.4F, 0.2F);
        if (block == Blocks.GREEN_WOOL) return new Vector3f(0.0F, 0.5F, 0.0F);
        if (block == Blocks.RED_WOOL) return new Vector3f(1.0F, 0.0F, 0.0F);
        if (block == Blocks.BLACK_WOOL) return new Vector3f(0.1F, 0.1F, 0.1F);
        return new Vector3f(1.0F, 1.0F, 1.0F);
    }
}