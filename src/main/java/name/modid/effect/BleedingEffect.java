package name.modid.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.random.Random;

public class BleedingEffect extends StatusEffect {
    public BleedingEffect() {
        super(StatusEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient()) {
            Random rand = entity.getWorld().getRandom();
            float damage = 0.5F + (amplifier + 0.5F); // Урон: 0.5 (I), 1.0 (II), 1.5 (III)
            if (rand.nextInt(100) < 33) {
                entity.damage(entity.getDamageSources().magic(), damage);
            }
        }
    }
}