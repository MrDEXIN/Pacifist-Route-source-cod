package name.modid.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class RitualKnifeItem extends SwordItem {
    private static final float HUMAN_SKIN_DROP_CHANCE = 0.42F;

    public RitualKnifeItem() {
        super(
                ToolMaterials.IRON,
                1, // У
                -1.8F,
                new Settings().maxDamage(160)
        );
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.isSneaking()) {
            if (!world.isClient) {
                stack.damage(1, user, (p) -> p.sendToolBreakStatus(hand));
                user.damage(user.getDamageSources().generic(), 0.5F); // Урон игроку 0.5 ❤️

                if (world.getRandom().nextFloat() < HUMAN_SKIN_DROP_CHANCE) {
                    user.dropItem(new ItemStack(ModItems.HUMAN_SKIN), false);
                }

                if (world.getRandom().nextFloat() < 0.1F) {
                    user.addStatusEffect(new StatusEffectInstance(
                            ModItems.BLEEDING,
                            world.getRandom().nextBetween(100, 400),
                            0
                    ));
                }
            }
            return TypedActionResult.success(stack);
        }
        return super.use(world, user, hand);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, (e) -> e.sendToolBreakStatus(attacker.getActiveHand()));

        if (!target.getWorld().isClient()) {
            target.damage(target.getDamageSources().mobAttack(attacker), 0.5F); // Урон цели 0.5 ❤️

            if (target.getWorld().getRandom().nextFloat() < HUMAN_SKIN_DROP_CHANCE) {
                target.dropStack(new ItemStack(ModItems.HUMAN_SKIN));
            }

            if (target.getWorld().getRandom().nextFloat() < 0.1F) {
                target.addStatusEffect(new StatusEffectInstance(
                        ModItems.BLEEDING,
                        target.getWorld().getRandom().nextBetween(100, 400),
                        0
                ));
            }
        }
        return true;
    }

    public boolean isAcceptableEnchantment(Enchantment enchantment) {
        return enchantment == Enchantments.UNBREAKING ||
                enchantment == Enchantments.MENDING;
    }

    @Override
    public int getEnchantability() {
        return 15;
    }
}