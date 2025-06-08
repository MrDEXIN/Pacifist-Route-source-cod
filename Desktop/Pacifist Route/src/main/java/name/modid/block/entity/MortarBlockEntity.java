package name.modid.block.entity;

import name.modid.item.ModItems;
import name.modid.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import name.modid.PacifistRoute;

public class MortarBlockEntity extends BlockEntity implements GeoBlockEntity, Inventory {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private float grindProgress = 0.0f;
    private long lastGrindTime = 0;
    private boolean lastGrindSuccess = false;

    // ЗВУКОВАЯ СИСТЕМА
    private long lastSoundTime = 0;
    private static final long SOUND_INTERVAL = 20;
    private static final SoundEvent[] GRIND_SOUNDS = {
            ModSounds.MORTAR_GRIND_1,
            ModSounds.MORTAR_GRIND_2,
            ModSounds.MORTAR_GRIND_3
    };

    private static final RawAnimation GRIND_ANIMATION = RawAnimation.begin().thenLoop("animation.model.new");

    private static final Item NITRE_SHARD = ModItems.NITRE_SHARD;
    private static final Item SALTPETER = ModItems.SALTPETER;
    private static final Item[] SULFUR_CRYSTALS = {
            ModItems.NETHER_CRYSTAL_SULFUR,
            ModItems.DEEP_CRYSTAL_SULFUR,
            ModItems.STONE_CRYSTAL_SULFUR
    };
    private static final Item SULFUR = ModItems.SULFUR;

    public MortarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MORTAR_BLOCK_ENTITY, pos, state);
    }

    private void playGrindingSounds(World world, BlockPos pos) {
        if (!world.isClient && world.getTime() - lastSoundTime >= SOUND_INTERVAL) {
            Random random = world.getRandom();
            int soundIndex = random.nextInt(GRIND_SOUNDS.length);

            SoundEvent soundToPlay = GRIND_SOUNDS[soundIndex];

            world.playSound(
                    null,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    soundToPlay,
                    SoundCategory.BLOCKS,
                    0.8f,
                    0.9f + random.nextFloat() * 0.2f
            );

            lastSoundTime = world.getTime();

            PacifistRoute.LOGGER.info("Playing grinding sound {} at {}", soundIndex + 1, pos);
        }
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(stack -> stack == null || stack.isEmpty());
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(inventory, slot, amount);
        markDirty();
        forceSync();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = inventory.get(slot);
        inventory.set(slot, ItemStack.EMPTY);
        markDirty();
        forceSync();
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack != null ? stack : ItemStack.EMPTY);
        markDirty();
        forceSync();
    }

    @Override
    public boolean canPlayerUse(net.minecraft.entity.player.PlayerEntity player) {
        return pos.getSquaredDistance(player.getPos()) <= 64 && world.getBlockEntity(pos) == this;
    }

    @Override
    public void clear() {
        inventory.clear();
        for (int i = 0; i < inventory.size(); i++) {
            inventory.set(i, ItemStack.EMPTY);
        }
        grindProgress = 0.0f;
        markDirty();
        forceSync();
    }

    public void addGrindProgress(float delta, boolean forward) {
        if (world == null || world.isClient) return;

        if (!hasItemsToGrind()) {
            PacifistRoute.LOGGER.info("No items to grind");
            grindProgress = 0.0f;
            markDirty();
            forceSync();
            return;
        }

        float cappedDelta = Math.min(delta, 0.03f);
        grindProgress = Math.max(0.0f, Math.min(grindProgress + cappedDelta, 1.0f));

        PacifistRoute.LOGGER.info("Grind progress: {}", grindProgress);

        if (grindProgress > 0.0f && grindProgress < 1.0f) {
            playGrindingSounds(world, pos);
        }

        if (grindProgress >= 1.0f) {
            processGrind();
        }

        markDirty();
        forceSync();
    }

    public void processGrind() {
        if (world == null || world.isClient) return;

        for (int i = inventory.size() - 1; i >= 0; i--) {
            ItemStack stack = inventory.get(i);
            if (stack != null && !stack.isEmpty() && !isGrinded(stack.getItem())) {
                Item item = stack.getItem();
                Boolean success = null;
                double random = world.random.nextDouble();

                if (item == NITRE_SHARD) success = random < 0.65;
                else if (isSulfurCrystal(item)) success = random < 0.45;

                if (success != null) {
                    lastGrindTime = world.getTime();
                    lastGrindSuccess = success;

                    if (success) {
                        world.playSound(
                                null,
                                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                SoundEvents.ENTITY_PLAYER_LEVELUP,
                                SoundCategory.BLOCKS,
                                0.4f,
                                1.2f
                        );

                        ItemStack result = new ItemStack(getGrindedItem(item), 1);
                        shiftItemsUp(i);
                        inventory.set(0, result);
                        PacifistRoute.LOGGER.info("Successful grind: {} -> {}", item, result.getItem());
                    } else {
                        world.playSound(
                                null,
                                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                SoundEvents.BLOCK_ANVIL_DESTROY,
                                SoundCategory.BLOCKS,
                                0.4f,
                                0.8f );

                        inventory.set(i, ItemStack.EMPTY);
                        PacifistRoute.LOGGER.info("Failed grind: {} destroyed", item);
                    }

                    grindProgress = 0.0f;
                    markDirty();

                    forceSync();
                    if (world instanceof ServerWorld serverWorld) {
                        serverWorld.getServer().execute(() -> {
                            forceSync();
                        });
                    }
                    return;
                }
            }
        }

        grindProgress = 0.0f;
        markDirty();
        forceSync();
    }

    private void shiftItemsUp(int fromIndex) {
        for (int i = fromIndex; i > 0; i--) {
            inventory.set(i, inventory.get(i - 1));
        }
        inventory.set(0, ItemStack.EMPTY);
    }

    public boolean canAddItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        Item item = stack.getItem();
        if (item != NITRE_SHARD && !isSulfurCrystal(item)) return false;
        int count = getTotalItemCount();
        if (count >= 5) return false;
        Item firstItemType = getFirstItemType();
        return firstItemType == null ||
                (item == NITRE_SHARD && firstItemType == NITRE_SHARD) ||
                (isSulfurCrystal(item) && isSulfurCrystal(firstItemType));
    }

    public void addItem(ItemStack stack) {
        if (!canAddItem(stack)) return;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).isEmpty()) {
                inventory.set(i, new ItemStack(stack.getItem(), 1));
                stack.decrement(1);
                markDirty();
                forceSync();
                break;
            }
        }
    }

    public ItemStack extractItem() {
        for (int i = inventory.size() - 1; i >= 0; i--) {
            ItemStack stack = inventory.get(i);
            if (stack != null && !stack.isEmpty() && isGrinded(stack.getItem())) {
                ItemStack result = stack.split(1);

                if (stack.isEmpty() || stack.getCount() <= 0) {
                    inventory.set(i, ItemStack.EMPTY);
                }

                markDirty();

                forceSync();
                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.getServer().execute(() -> {
                        forceSync();
                    });
                }

                PacifistRoute.LOGGER.info("Extracted item: {}", result.getItem().toString());
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    private boolean isSulfurCrystal(Item item) {
        for (Item crystal : SULFUR_CRYSTALS) {
            if (item == crystal) return true;
        }
        return false;
    }

    public boolean isGrinded(Item item) {
        return item == SALTPETER || item == SULFUR;
    }

    public boolean hasItemsToGrind() {
        for (ItemStack stack : inventory) {
            if (stack != null && !stack.isEmpty() && !isGrinded(stack.getItem())) {
                return true;
            }
        }
        return false;
    }

    private Item getGrindedItem(Item item) {
        return item == NITRE_SHARD ? SALTPETER : SULFUR;
    }

    private int getTotalItemCount() {
        return inventory.stream().mapToInt(stack -> stack == null || stack.isEmpty() ? 0 : stack.getCount()).sum();
    }

    public Item getFirstItemType() {
        for (ItemStack stack : inventory) {
            if (stack != null && !stack.isEmpty()) return stack.getItem();
        }
        return null;
    }

    public void forceSync() {
        if (world != null && !world.isClient && world instanceof ServerWorld serverWorld) {
            BlockEntityUpdateS2CPacket packet = toUpdatePacket();
            serverWorld.getPlayers(player ->
                            player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64 * 64)
                    .forEach(player -> player.networkHandler.sendPacket(packet));

            world.markDirty(pos);
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            serverWorld.getChunkManager().markForUpdate(pos);

            PacifistRoute.LOGGER.debug("Force sync executed for mortar at {}", pos);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        inventory.clear();
        for (int i = 0; i < inventory.size(); i++) {
            inventory.set(i, ItemStack.EMPTY);
        }

        Inventories.readNbt(nbt, inventory);
        grindProgress = nbt.getFloat("GrindProgress");
        lastGrindTime = nbt.getLong("LastGrindTime");
        lastGrindSuccess = nbt.getBoolean("LastGrindSuccess");
        lastSoundTime = nbt.getLong("LastSoundTime");

        markDirty();
        if (world != null && !world.isClient) {
            forceSync();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putFloat("GrindProgress", grindProgress);
        nbt.putLong("LastGrindTime", lastGrindTime);
        nbt.putBoolean("LastGrindSuccess", lastGrindSuccess);
        nbt.putLong("LastSoundTime", lastSoundTime); // Сохраняем время последнего звука
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public void sync() {
        forceSync();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "grindController", 0, this::animationPredicate));
    }

    private PlayState animationPredicate(AnimationState<MortarBlockEntity> state) {
        if (grindProgress > 0.0f && hasItemsToGrind()) {
            PacifistRoute.LOGGER.debug("Animation playing: progress={}", grindProgress);
            return state.setAndContinue(GRIND_ANIMATION);
        } else {
            PacifistRoute.LOGGER.debug("Animation stopped: progress={}, hasItems={}", grindProgress, hasItemsToGrind());
            return PlayState.STOP;
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public float getGrindProgress() {
        return grindProgress;
    }

    public long getLastGrindTime() {
        return lastGrindTime;
    }

    public boolean getLastGrindSuccess() {
        return lastGrindSuccess;
    }

    public boolean isGrinding() {
        return grindProgress > 0.0f && hasItemsToGrind();
    }

    public static void tick(World world, BlockPos pos, BlockState state, MortarBlockEntity entity) {
    }
}