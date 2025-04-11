package name.modid.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;

public class HumanSkinItem extends Item {
    public static final FoodComponent HUMAN_SKIN_FOOD = (new FoodComponent.Builder())
            .hunger(1) // Пол сытости
            .saturationModifier(0.5F) // Насыщение
            .build();

    public HumanSkinItem(Settings settings) {
        super(settings.maxCount(64).food(HUMAN_SKIN_FOOD));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.canConsume(false)) { // Проверяем, может ли игрок есть
            user.setCurrentHand(hand); // Запускаем процесс еды
            return TypedActionResult.consume(stack);
        }
        return TypedActionResult.pass(stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient && user instanceof PlayerEntity player) {
            String state = getState(stack, world);
            switch (state) {
                case "fresh":
                    if (world.random.nextFloat() < 0.5F) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 140, 0)); // 7 сек, 50%
                    }
                    player.heal(1.0F); // Пол сердца
                    break;
                case "stale":
                    if (world.random.nextFloat() < 0.6F) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 240, 0)); // 12 сек, 60%
                    }
                    player.damage(player.getDamageSources().generic(), 1.0F); // Пол сердца урона
                    break;
                case "spoiling":
                    if (world.random.nextFloat() < 0.8F) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 400, 0)); // 20 сек, 80%
                    }
                    break;
                case "rotten":
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 600, 0)); // 30 сек, 100%
                    if (world.random.nextFloat() < 0.4F) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 0)); // 10 сек, 40%
                    }
                    break;
            }
        }
        return super.finishUsing(stack, world, user);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, java.util.List<Text> tooltip, net.minecraft.client.item.TooltipContext context) {
        if (world != null) {
            String state = getState(stack, world);
            Formatting color = switch (state) {
                case "fresh" -> Formatting.GREEN;
                case "stale" -> Formatting.DARK_GREEN;
                case "spoiling" -> Formatting.GOLD;
                default -> Formatting.RED; // Для "rotten"
            };
            tooltip.add(Text.translatable("item.pacifist_route.human_skin.quality." + state)
                    .setStyle(Style.EMPTY.withColor(color)));
        }
    }

    public String getState(ItemStack stack, World world) {
        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains("CreationTime")) {
            nbt.putLong("CreationTime", world.getTime());
        }
        long creationTime = nbt.getLong("CreationTime");
        long currentTime = world.getTime();
        long age = currentTime - creationTime;

        if (age < 2400) { // 5 секунд
            return "fresh";
        } else if (age < 4800) { // 10 секунд
            return "stale";
        } else if (age < 7200) { // 15 секунд
            return "spoiling";
        } else {
            return "rotten";
        }
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putLong("CreationTime", world.getTime());
    }

    public ItemStack checkAndTransform(ItemStack stack, World world) {
        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains("CreationTime")) {
            nbt.putLong("CreationTime", world.getTime());
        }
        long creationTime = nbt.getLong("CreationTime");
        long currentTime = world.getTime();
        long age = currentTime - creationTime;

        if (age >= 12000) { // 20 секунд
            int count = stack.getCount();
            return new ItemStack(Items.ROTTEN_FLESH, count); // Заменяем на гнилую плоть
        }
        return stack; // Возвращаем оригинальный предмет
    }
}