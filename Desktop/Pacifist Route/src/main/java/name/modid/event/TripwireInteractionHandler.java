package name.modid.event;

import name.modid.item.ModItems;
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
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import org.joml.Vector3f;

import java.util.*;

public class TripwireInteractionHandler implements UseBlockCallback {

    private static final Map<BlockPos, List<Integer>> tripwireStates = new HashMap<>();

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack heldItem = player.getStackInHand(hand);

        if (state.getBlock() == Blocks.TRIPWIRE && heldItem.getItem() == ModItems.HUMAN_SKIN) {
            if (!world.isClient) {
                List<Integer> timers = tripwireStates.getOrDefault(pos, new ArrayList<>());
                if (timers.size() >= 2) {
                    return ActionResult.PASS;
                }
                heldItem.decrement(1);
                timers.add(0);
                tripwireStates.put(pos, timers);
                world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return ActionResult.SUCCESS;
        }

        if (state.getBlock() == Blocks.TRIPWIRE && tripwireStates.containsKey(pos)) {
            if (!world.isClient) {
                List<Integer> timers = tripwireStates.get(pos);
                int rottenFleshCount = 0;
                for (int i = 0; i < timers.size(); i++) {
                    if (timers.get(i) >= 12000) { // Изменено на 12000
                        rottenFleshCount++;
                    }
                }
                if (rottenFleshCount > 0) {
                    timers.removeIf(timer -> timer >= 12000); // Изменено на 12000
                    ItemStack rottenFleshStack = new ItemStack(Items.ROTTEN_FLESH, rottenFleshCount);
                    ItemEntity rottenFleshEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, rottenFleshStack);
                    world.spawnEntity(rottenFleshEntity);
                    world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    if (timers.isEmpty()) {
                        tripwireStates.remove(pos);
                    }
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static void tick(World world) {
        for (Map.Entry<BlockPos, List<Integer>> entry : tripwireStates.entrySet()) {
            BlockPos pos = entry.getKey();
            List<Integer> timers = entry.getValue();
            for (int i = 0; i < timers.size(); i++) {
                int timer = timers.get(i);
                if (timer < 12000) { // Изменено на 12000
                    timers.set(i, timer + 1);
                    if (world instanceof ServerWorld) {
                        ((ServerWorld) world).spawnParticles(
                                new DustParticleEffect(new Vector3f(0.0F, 1.0F, 0.0F), 1.0F),
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
                } else if (timer == 12000) { // Изменено на 12000
                    if (world instanceof ServerWorld) {
                        ((ServerWorld) world).spawnParticles(
                                new DustParticleEffect(new Vector3f(1.0F, 0.0F, 0.0F), 1.0F),
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
                }
            }
        }
    }
}