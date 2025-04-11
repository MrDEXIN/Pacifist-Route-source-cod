package name.modid.event;

import name.modid.item.HumanSkinItem;
import name.modid.item.ModItems;
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
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripwireInteractionHandler implements UseBlockCallback {

    private static final Map<BlockPos, List<ItemStack>> tripwireStacks = new HashMap<>();

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack heldItem = player.getStackInHand(hand);

        if (state.getBlock() == Blocks.TRIPWIRE && heldItem.getItem() == ModItems.HUMAN_SKIN) {
            if (!world.isClient) {
                List<ItemStack> stacks = tripwireStacks.getOrDefault(pos, new ArrayList<>());
                if (stacks.size() >= 2) {
                    return ActionResult.PASS;
                }
                ItemStack skinCopy = heldItem.copy();
                skinCopy.setCount(1);
                stacks.add(skinCopy);
                tripwireStacks.put(pos, stacks);
                heldItem.decrement(1);
                world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return ActionResult.SUCCESS;
        }

        if (state.getBlock() == Blocks.TRIPWIRE && tripwireStacks.containsKey(pos)) {
            if (!world.isClient) {
                List<ItemStack> stacks = tripwireStacks.get(pos);
                int rottenFleshCount = 0;
                List<ItemStack> toRemove = new ArrayList<>();
                for (ItemStack stack : stacks) {
                    ItemStack transformed = ((HumanSkinItem) stack.getItem()).checkAndTransform(stack, world);
                    if (transformed.getItem() == Items.ROTTEN_FLESH) {
                        rottenFleshCount += transformed.getCount();
                        toRemove.add(stack);
                    }
                }
                stacks.removeAll(toRemove);
                if (rottenFleshCount > 0) {
                    ItemStack rottenFleshStack = new ItemStack(Items.ROTTEN_FLESH, rottenFleshCount);
                    ItemEntity rottenFleshEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, rottenFleshStack);
                    world.spawnEntity(rottenFleshEntity);
                    world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                if (stacks.isEmpty()) {
                    tripwireStacks.remove(pos);
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static void tick(World world) {
        for (Map.Entry<BlockPos, List<ItemStack>> entry : tripwireStacks.entrySet()) {
            BlockPos pos = entry.getKey();
            List<ItemStack> stacks = entry.getValue();
            for (int i = 0; i < stacks.size(); i++) {
                ItemStack stack = stacks.get(i);
                if (stack.getItem() instanceof HumanSkinItem) {
                    HumanSkinItem item = (HumanSkinItem) stack.getItem();
                    ItemStack transformed = item.checkAndTransform(stack, world);
                    String state = item.getState(stack, world);
                    Vector3f color = switch (state) {
                        case "fresh" -> new Vector3f(0.0F, 1.0F, 0.0F); // Зеленый
                        case "stale" -> new Vector3f(0.0F, 0.5F, 0.0F); // Темно-зеленый
                        case "spoiling" -> new Vector3f(1.0F, 0.5F, 0.0F); // Оранжевый
                        default -> new Vector3f(1.0F, 0.0F, 0.0F); // Красный
                    };
                    if (world instanceof ServerWorld) {
                        ((ServerWorld) world).spawnParticles(
                                new DustParticleEffect(color, 1.0F),
                                pos.getX() + 0.5,
                                pos.getY() + 0.5 + (i * 0.2),
                                pos.getZ() + 0.5,
                                1,
                                0.0,
                                0.0,
                                0.0,
                                0.0
                        );
                    }
                    if (transformed.getItem() == Items.ROTTEN_FLESH) {
                        stacks.set(i, transformed);
                    }
                }
            }
        }
    }
}