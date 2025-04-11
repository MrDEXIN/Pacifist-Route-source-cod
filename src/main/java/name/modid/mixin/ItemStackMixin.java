package name.modid.mixin;

import name.modid.item.HumanSkinItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract int getCount();

    @Inject(
            method = "canCombine(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void onCanCombine(ItemStack stack1, ItemStack stack2, CallbackInfoReturnable<Boolean> cir) {
        if (stack1.getItem() instanceof HumanSkinItem && stack2.getItem() instanceof HumanSkinItem) {
            NbtCompound nbt1 = stack1.getOrCreateNbt();
            NbtCompound nbt2 = stack2.getOrCreateNbt();

            long time1 = nbt1.contains("CreationTime") ? nbt1.getLong("CreationTime") : Long.MAX_VALUE;
            long time2 = nbt2.contains("CreationTime") ? nbt2.getLong("CreationTime") : Long.MAX_VALUE;

            // Сохраняем меньший CreationTime в обоих стаках
            long olderTime = Math.min(time1, time2);
            nbt1.putLong("CreationTime", olderTime);
            nbt2.putLong("CreationTime", olderTime);

            cir.setReturnValue(true); // Разрешаем объединение
        }
    }

    @Inject(
            method = "increment(I)V",
            at = @At("HEAD")
    )
    private void onIncrement(int amount, CallbackInfo ci) {
        ItemStack thisStack = (ItemStack) (Object) this;

        if (thisStack.getItem() instanceof HumanSkinItem && amount > 0) {
            NbtCompound nbt = thisStack.getOrCreateNbt();
            long currentTime = nbt.contains("CreationTime") ? nbt.getLong("CreationTime") : Long.MAX_VALUE;

            // Если это первый предмет в стачке (пустой слот), сохраняем его время
            if (thisStack.getCount() == amount) {
                return; // Таймер остается как у подбираемого предмета
            }

            // В этот момент canCombine уже должен был установить меньший CreationTime
        }
    }

    @Inject(
            method = "split(I)Lnet/minecraft/item/ItemStack;",
            at = @At("RETURN")
    )
    private void onSplit(int amount, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack thisStack = (ItemStack) (Object) this;
        ItemStack splitStack = cir.getReturnValue();

        if (thisStack.getItem() instanceof HumanSkinItem && splitStack.getItem() instanceof HumanSkinItem) {
            NbtCompound nbtOriginal = thisStack.getOrCreateNbt();
            NbtCompound nbtSplit = splitStack.getOrCreateNbt();

            long originalTime = nbtOriginal.contains("CreationTime") ? nbtOriginal.getLong("CreationTime") : Long.MAX_VALUE;
            nbtSplit.putLong("CreationTime", originalTime); // Отделяемый предмет сохраняет таймер стака
            // Таймер исходного стака не меняется
        }
    }
}