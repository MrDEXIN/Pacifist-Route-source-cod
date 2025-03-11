package name.modid.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class ZombieHeadToCreeperHandler implements UseBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack heldItem = player.getStackInHand(hand);

        // Проверяем, что игрок кликает по голове зомби и держит порох
        if (state.getBlock() == Blocks.ZOMBIE_HEAD && heldItem.getItem() == Items.GUNPOWDER) {
            if (!world.isClient) { // Проверяем, что код выполняется на сервере
                // Убираем порох из руки игрока
                heldItem.decrement(1);

                // Получаем текущую ориентацию головы зомби
                int rotation = state.get(Properties.ROTATION); // Свойство ROTATION определяет направление

                // Создаем новое состояние для головы крипера с той же ориентацией
                BlockState creeperHeadState = Blocks.CREEPER_HEAD.getDefaultState().with(Properties.ROTATION, rotation);

                // Заменяем голову зомби на голову крипера с сохранением ориентации
                world.setBlockState(pos, creeperHeadState);

                // Воспроизводим звук взрыва (или другой звук, подходящий для крипера)
                world.playSound(
                        null,                       // Игрок (null для всех игроков)
                        pos,                        // Позиция звука
                        SoundEvents.ENTITY_CREEPER_PRIMED, // Звук активации крипера
                        SoundCategory.BLOCKS,       // Категория звука
                        1.0F,                       // Громкость
                        1.0F                        // Высота тона
                );

                // Создаем лаймовые, зеленые и темно-зеленые частицы
                spawnColoredParticles((ServerWorld) world, pos, new Vector3f(0.5F, 1.0F, 0.0F)); // Лаймовый
                spawnColoredParticles((ServerWorld) world, pos, new Vector3f(0.0F, 1.0F, 0.0F)); // Зеленый
                spawnColoredParticles((ServerWorld) world, pos, new Vector3f(0.0F, 0.5F, 0.0F)); // Темно-зеленый
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    // Метод для создания цветных частиц
    private void spawnColoredParticles(ServerWorld world, BlockPos pos, Vector3f color) {
        DustParticleEffect particleEffect = new DustParticleEffect(color, 1.0F); // Цвет и масштаб частиц
        world.spawnParticles(
                particleEffect,                     // Тип частиц (цветные пылевые частицы)
                pos.getX() + 0.5,                  // X координата центра блока
                pos.getY() + 0.3,                  // Y координата центра блока
                pos.getZ() + 0.5,                  // Z координата центра блока
                400,                                // Количество частиц
                0.2,                               // Разброс по X
                0.2,                               // Разброс по Y
                0.2,                               // Разброс по Z
                0.1                                // Скорость частиц
        );
    }
}