package name.modid.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
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
            if (rand.nextInt(100) < 33) { // 33% шанс урона каждую секунду
                entity.damage(entity.getDamageSources().magic(), 0.5F);
            }
        }
    }
}