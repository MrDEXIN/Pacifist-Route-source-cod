// FallingAnvilMixin.java
package name.modid.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import name.modid.item.ModItems;

@Mixin(FallingBlockEntity.class)
public abstract class FallingAnvilMixin {

    // Список всех вариаций наковальни
    private static final Block[] ANVILS = {
            Blocks.ANVIL,
            Blocks.CHIPPED_ANVIL,
            Blocks.DAMAGED_ANVIL
    };

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/FallingBlockEntity;discard()V",
                    shift = At.Shift.AFTER
            )
    )
    private void onAnvilLand(CallbackInfo ci) {
        FallingBlockEntity self = (FallingBlockEntity) (Object) this;
        World world = self.getWorld();
        Block currentBlock = self.getBlockState().getBlock();

        // Проверяем все типы наковален
        for (Block anvil : ANVILS) {
            if (currentBlock == anvil) {
                handleAnvilEffect(world, self.getBlockPos());
                break;
            }
        }
    }

    private void handleAnvilEffect(World world, BlockPos anvilPos) {
        world.getEntitiesByClass(
                ItemEntity.class,
                new Box(anvilPos),
                entity -> entity.getBlockPos().equals(anvilPos)
                        && entity.getStack().getItem() == ModItems.WITHER_SOUL
        ).forEach(item -> {
            // Замена предмета
            ItemEntity starEntity = new ItemEntity(
                    world,
                    item.getX(),
                    item.getY(),
                    item.getZ(),
                    new ItemStack(Items.NETHER_STAR, item.getStack().getCount())
            );
            world.spawnEntity(starEntity);
            item.discard();

            if (!world.isClient) {
                ServerWorld serverWorld = (ServerWorld) world;

                // Звук смерти Визера
                serverWorld.playSound(
                        null,
                        anvilPos,
                        SoundEvents.ENTITY_WITHER_DEATH,
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );

                // Генерация частиц
                Vec3d center = new Vec3d(
                        anvilPos.getX() + 0.5,
                        anvilPos.getY() + 0.5,
                        anvilPos.getZ() + 0.5
                );
                spawnStarParticles(serverWorld, center);
            }
        });
    }

    private void spawnStarParticles(ServerWorld world, Vec3d center) {
        // Плоская 4-угольная звезда
        double radius = 3.0; // Радиус звезды
        int points = 4;      // Количество лучей

        // Углы для 4 точек (0°, 90°, 180°, 270°)
        for (int i = 0; i < 360; i += 90) {
            double angle = Math.toRadians(i);

            // Координаты для частиц вдоль лучей
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            world.spawnParticles(
                    ParticleTypes.FIREWORK,
                    center.x + x,
                    center.y + 0.1,  // Небольшое смещение вверх для видимости
                    center.z + z,
                    20,             // Количество частиц на луч
                    0.3,            // Разброс по X
                    0.0,            // Разброс по Y (0 для плоской формы)
                    0.3,            // Разброс по Z
                    0.15            // Скорость
            );
        }

        // Диагонали для формирования звезды
        for (int i = 45; i < 360; i += 90) {
            double angle = Math.toRadians(i);

            double x = Math.cos(angle) * radius * 0.6;
            double z = Math.sin(angle) * radius * 0.6;

            world.spawnParticles(
                    ParticleTypes.FIREWORK,
                    center.x + x,
                    center.y + 0.1,
                    center.z + z,
                    15,
                    0.2,
                    0.0,
                    0.2,
                    0.1
            );
        }
    }
}