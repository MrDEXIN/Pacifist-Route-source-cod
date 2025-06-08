package name.modid.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class YellowSmokeParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;

    protected YellowSmokeParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        this.spriteProvider = spriteProvider;

        this.velocityX = velocityX;
        this.velocityY = 0.025; // Медленная постоянная скорость
        this.velocityZ = velocityZ;

        this.scale = 0.25f + random.nextFloat() * 0.25f; // 0.25-0.5

        this.maxAge = 200 + random.nextInt(80); // 200-280 тиков (10-14 секунд)
        this.collidesWithWorld = false;
        this.gravityStrength = 0.0f;

        this.red = 1.0f;
        this.green = 1.0f;
        this.blue = 1.0f;
        this.alpha = 0.75f;

        this.setSprite(spriteProvider.getSprite(0, 11));
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }

        int frameChangeRate = 15;
        int currentFrame = Math.min(11, this.age / frameChangeRate);
        this.setSprite(this.spriteProvider.getSprite(currentFrame, 11));

        if (this.age > this.maxAge * 9 / 10) {
            float fadeStart = (float)(this.age - this.maxAge * 9 / 10);
            float fadeLength = (float)(this.maxAge / 10);
            float fadeProgress = fadeStart / fadeLength;
            this.alpha = 0.95f * (1.0f - fadeProgress);
        }

        this.velocityY = 0.025;

        // Почти никакого горизонтального замедления
        this.velocityX *= 0.995;
        this.velocityZ *= 0.995;

        this.velocityX += (double)((this.random.nextFloat() - 0.5f) / 30000.0f);
        this.velocityZ += (double)((this.random.nextFloat() - 0.5f) / 30000.0f);

        this.move(this.velocityX, this.velocityY, this.velocityZ);

        if (this.age < this.maxAge / 4) {
            this.scale += 0.001f; // Очень медленное увеличение
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new YellowSmokeParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.sprites);
        }
    }
}