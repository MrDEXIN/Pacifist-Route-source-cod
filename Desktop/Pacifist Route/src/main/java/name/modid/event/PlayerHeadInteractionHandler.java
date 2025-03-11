package name.modid.event;

import name.modid.item.ModItems;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class PlayerHeadInteractionHandler implements UseBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack heldItem = player.getStackInHand(hand);

        // Проверяем, что игрок кликает по голове игрока и держит топор
        if (state.getBlock() == Blocks.PLAYER_HEAD && isAxe(heldItem.getItem())) {
            if (!world.isClient) { // Проверяем, что код выполняется на сервере
                // Уменьшаем прочность топора на 1 единицу
                heldItem.damage(1, player, (user) -> user.sendToolBreakStatus(hand));

                // Получаем текущую ориентацию головы игрока
                int rotation = state.get(Properties.ROTATION); // Свойство ROTATION определяет направление

                // Создаем новое состояние для черепа скелета с той же ориентацией
                BlockState skeletonSkullState = Blocks.SKELETON_SKULL.getDefaultState().with(Properties.ROTATION, rotation);

                // Заменяем голову игрока на череп скелета с сохранением ориентации
                world.setBlockState(pos, skeletonSkullState);

                // Воспроизводим звук удара слизня
                world.playSound(
                        null,                       // Игрок (null для всех игроков)
                        pos,                        // Позиция звука
                        SoundEvents.ENTITY_SLIME_ATTACK, // Звук удара слизня
                        SoundCategory.BLOCKS,       // Категория звука
                        1.0F,                       // Громкость
                        1.0F                        // Высота тона
                );

                // Создаем красные частицы (капли)
                Vector3f redColor = new Vector3f(1.0F, 0.0F, 0.0F); // Красный цвет (RGB)
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

                // Генерируем случайное количество кожи человека (1-4) с заданными вероятностями
                int skinAmount = getRandomSkinAmount(world.getRandom());

                // Создаем и выбрасываем предмет кожи человека
                ItemStack humanSkinStack = new ItemStack(ModItems.HUMAN_SKIN, skinAmount); // Используем кастомную кожу человека
                ItemEntity humanSkinEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, humanSkinStack);
                world.spawnEntity(humanSkinEntity); // Выбрасываем кожу человека в мир
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    // Метод для генерации случайного количества кожи человека с заданными вероятностями
    private int getRandomSkinAmount(Random random) {
        double chance = random.nextDouble(); // Генерируем случайное число от 0.0 до 1.0

        if (chance < 0.25) { // 25% вероятность
            return 1;
        } else if (chance < 0.55) { // 30% вероятность (0.25 + 0.30)
            return 2;
        } else if (chance < 0.85) { // 30% вероятность (0.55 + 0.30)
            return 3;
        } else { // 15% вероятность (остаток)
            return 4;
        }
    }

    // Проверка, является ли предмет топором
    private boolean isAxe(Item item) {
        return item == Items.WOODEN_AXE ||
                item == Items.STONE_AXE ||
                item == Items.IRON_AXE ||
                item == Items.GOLDEN_AXE ||
                item == Items.DIAMOND_AXE ||
                item == Items.NETHERITE_AXE;
    }
}