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

public class SkeletonSkullInteractionHandler implements UseBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack heldItem = player.getStackInHand(hand);

        // Проверяем, что игрок кликает по черепу скелета и держит гнилую плоть
        if (state.getBlock() == Blocks.SKELETON_SKULL && heldItem.getItem() == Items.ROTTEN_FLESH) {
            if (!world.isClient) { // Проверяем, что код выполняется на сервере
                // Убираем гнилую плоть из руки игрока
                heldItem.decrement(1);

                // Получаем текущую ориентацию черепа скелета
                int rotation = state.get(Properties.ROTATION); // Свойство ROTATION определяет направление

                // Создаем новое состояние для головы зомби с той же ориентацией
                BlockState zombieHeadState = Blocks.ZOMBIE_HEAD.getDefaultState().with(Properties.ROTATION, rotation);

                // Заменяем череп скелета на голову зомби с сохранением ориентации
                world.setBlockState(pos, zombieHeadState);

                // Воспроизводим звук превращения зомби-жителя в жителя
                world.playSound(
                        null,                       // Игрок (null для всех игроков)
                        pos,                        // Позиция звука
                        SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, // Звук превращения зомби-жителя в жителя
                        SoundCategory.BLOCKS,       // Категория звука
                        1.0F,                       // Громкость
                        1.0F                        // Высота тона
                );

                // Создаем красные частицы с помощью DustParticleEffect
                Vector3f redColor = new Vector3f(139.0F, 0.0F, 0.0F); // Красный цвет (RGB)
                DustParticleEffect dustParticleEffect = new DustParticleEffect(redColor, 1.0F); // Масштаб 1.0F

                // Создаем частицы
                ((ServerWorld) world).spawnParticles(
                        dustParticleEffect,         // Тип частиц (красные пылевые частицы)
                        pos.getX() + 0.5,          // X координата центра блока
                        pos.getY() + 0.3,          // Y координата центра блока
                        pos.getZ() + 0.5,          // Z координата центра блока
                        400,                        // Количество частиц
                        0.2,                       // Разброс по X
                        0.2,                       // Разброс по Y
                        0.2,                       // Разброс по Z
                        0.1                        // Скорость частиц
                );
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}