package name.modid.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import name.modid.effect.BleedingEffect;
import java.util.List;

public class RitualKnifeItem extends SwordItem {
    private static final float HUMAN_SKIN_DROP_CHANCE = 0.42F;

    public RitualKnifeItem() {
        super(
                ToolMaterials.IRON,
                1,
                -1.8F,
                new Settings().maxDamage(160)
        );
    }

    // Новый метод для применения эффекта
    private void applyBleeding(LivingEntity target, World world) {
        StatusEffectInstance currentEffect = target.getStatusEffect(ModItems.BLEEDING);
        int duration = world.getRandom().nextBetween(100, 400);
        int amplifier = 0;

        if (currentEffect != null) {
            amplifier = Math.min(currentEffect.getAmplifier() + 1, 2); // Максимум amplifier=2 (III уровень)
            duration = (amplifier == 2) ? 1200 : duration; // Бесконечная длительность для III уровня
        }

        target.addStatusEffect(new StatusEffectInstance(
                ModItems.BLEEDING,
                duration,
                amplifier,
                false,
                true
        ));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.isSneaking()) {
            if (!world.isClient) {
                stack.damage(1, user, (p) -> p.sendToolBreakStatus(hand));
                user.damage(user.getDamageSources().generic(), 0.5F);

                if (world.getRandom().nextFloat() < HUMAN_SKIN_DROP_CHANCE) {
                    user.dropItem(new ItemStack(ModItems.HUMAN_SKIN), false);
                }

                if (world.getRandom().nextFloat() < 0.1F) {
                    applyBleeding(user, world);
                }
            }
            return TypedActionResult.success(stack);
        }
        return super.use(world, user, hand);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, (e) -> e.sendToolBreakStatus(attacker.getActiveHand()));

        // Проверяем, является ли цель игроком
        if (target instanceof PlayerEntity && !target.getWorld().isClient()) {
            PlayerEntity playerTarget = (PlayerEntity) target;

            // Наносим уменьшенный урон (0.5 / 1.5 = ~0.33)
            playerTarget.damage(playerTarget.getDamageSources().mobAttack(attacker), 0.33F);

            // Шанс выпадения HumanSkinItem
            if (playerTarget.getWorld().getRandom().nextFloat() < HUMAN_SKIN_DROP_CHANCE) {
                playerTarget.dropStack(new ItemStack(ModItems.HUMAN_SKIN));
            }

            // Шанс наложения кровотечения
            if (playerTarget.getWorld().getRandom().nextFloat() < 0.1F) {
                applyBleeding(playerTarget, playerTarget.getWorld());
            }
        }

        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        // Добавляем стандартные атрибуты (урон и скорость атаки)
        super.appendTooltip(stack, world, tooltip, context);

        // Находим индекс строки с уроном (обычно это первая строка после названия предмета)
        // В Minecraft 1.20.1 строка урона добавляется автоматически через атрибуты
        // Мы вставляем нашу надпись сразу после первой строки с атрибутами (урон)
        int damageLineIndex = -1;
        for (int i = 0; i < tooltip.size(); i++) {
            String line = tooltip.get(i).getString();
            if (line.contains("Attack Damage") || line.matches(".*\\+\\d+.*")) { // Ищем строку с уроном
                damageLineIndex = i;
                break;
            }
        }
    }

    public boolean isAcceptableEnchantment(Enchantment enchantment) {
        return enchantment == Enchantments.UNBREAKING || enchantment == Enchantments.MENDING;
    }

    @Override
    public int getEnchantability() {
        return 15;
    }
}