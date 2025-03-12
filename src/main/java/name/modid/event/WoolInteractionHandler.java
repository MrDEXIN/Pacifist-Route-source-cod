package name.modid.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class WoolInteractionHandler implements UseBlockCallback {
    public static final BooleanProperty SHEARED = BooleanProperty.of("sheared");

    // Хранение количества взаимодействий для каждого блока
    private final Map<BlockPos, Integer> interactionCountMap = new HashMap<>();

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack heldItem = player.getStackInHand(hand);

        // Проверяем, что игрок кликает по блоку шерсти и держит ножницы
        if (isWoolBlock(state.getBlock()) && heldItem.getItem() == Items.SHEARS) {
            if (!world.isClient) { // Проверяем, что код выполняется на сервере
                // Уменьшаем прочность ножниц
                heldItem.damage(1, player, (p) -> p.sendToolBreakStatus(hand));

                // Создаём предмет (нитку) на месте блока
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(Items.STRING, 1));
                world.spawnEntity(itemEntity);

                // Получаем текущее количество взаимодействий
                int interactions = interactionCountMap.getOrDefault(pos, 0) + 1;

                // Если взаимодействий достигло 4, удаляем блок
                if (interactions >= 4) {
                    // Воспроизводим звук разрушения блока шерсти
                    world.playSound(
                            null,                       // Игрок (null для всех игроков)
                            pos,                        // Позиция звука
                            SoundEvents.BLOCK_WOOL_BREAK, // Звук разрушения шерсти
                            SoundCategory.BLOCKS,       // Категория звука
                            1.0F,                       // Громкость
                            1.0F                        // Высота тона
                    );

                    // Создаём частицы разрушения блока
                    ((ServerWorld) world).spawnParticles(
                            new DustParticleEffect(getWoolColor(state.getBlock()), 1.0F), // Цветные частицы
                            pos.getX() + 0.5,                   // X координата центра блока
                            pos.getY() + 0.5,                   // Y координата центра блока
                            pos.getZ() + 0.5,                   // Z координата центра блока
                            20,                                 // Количество частиц
                            0.5,                                // Разброс по X
                            0.5,                                // Разброс по Y
                            0.5,                                // Разброс по Z
                            0.1                                 // Скорость частиц
                    );

                    // Удаляем блок
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    interactionCountMap.remove(pos); // Удаляем блок из карты
                } else {
                    // Сохраняем количество взаимодействий
                    interactionCountMap.put(pos, interactions);

                    // Воспроизводим звук стрижки шерсти
                    world.playSound(
                            null,                       // Игрок (null для всех игроков)
                            pos,                        // Позиция звука
                            SoundEvents.ENTITY_SHEEP_SHEAR, // Звук стрижки овцы
                            SoundCategory.BLOCKS,       // Категория звука
                            1.0F,                       // Громкость
                            1.0F                        // Высота тона
                    );

                    // Создаём цветные частицы (как при убийстве моба)
                    ((ServerWorld) world).spawnParticles(
                            new DustParticleEffect(getWoolColor(state.getBlock()), 1.0F), // Цветные частицы
                            pos.getX() + 0.5,                   // X координата центра блока
                            pos.getY() + 0.5,                   // Y координата центра блока
                            pos.getZ() + 0.5,                   // Z координата центра блока
                            10,                                 // Количество частиц
                            0.5,                                // Разброс по X
                            0.5,                                // Разброс по Y
                            0.5,                                // Разброс по Z
                            0.1                                 // Скорость частиц
                    );
                }
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    // Проверка, является ли блок шерстью
    private boolean isWoolBlock(Block block) {
        return block == Blocks.WHITE_WOOL ||
                block == Blocks.ORANGE_WOOL ||
                block == Blocks.MAGENTA_WOOL ||
                block == Blocks.LIGHT_BLUE_WOOL ||
                block == Blocks.YELLOW_WOOL ||
                block == Blocks.LIME_WOOL ||
                block == Blocks.PINK_WOOL ||
                block == Blocks.GRAY_WOOL ||
                block == Blocks.LIGHT_GRAY_WOOL ||
                block == Blocks.CYAN_WOOL ||
                block == Blocks.PURPLE_WOOL ||
                block == Blocks.BLUE_WOOL ||
                block == Blocks.BROWN_WOOL ||
                block == Blocks.GREEN_WOOL ||
                block == Blocks.RED_WOOL ||
                block == Blocks.BLACK_WOOL;
    }

    // Получение цвета блока шерсти
    private Vector3f getWoolColor(Block block) {
        if (block == Blocks.WHITE_WOOL) return new Vector3f(1.0F, 1.0F, 1.0F);       // Белый
        if (block == Blocks.ORANGE_WOOL) return new Vector3f(0.98F, 0.55F, 0.0F);    // Оранжевый
        if (block == Blocks.MAGENTA_WOOL) return new Vector3f(0.98F, 0.2F, 0.98F);  // Пурпурный
        if (block == Blocks.LIGHT_BLUE_WOOL) return new Vector3f(0.4F, 0.8F, 1.0F);  // Светло-синий
        if (block == Blocks.YELLOW_WOOL) return new Vector3f(1.0F, 1.0F, 0.0F);      // Жёлтый
        if (block == Blocks.LIME_WOOL) return new Vector3f(0.5F, 1.0F, 0.0F);        // Лаймовый
        if (block == Blocks.PINK_WOOL) return new Vector3f(1.0F, 0.6F, 0.7F);        // Розовый
        if (block == Blocks.GRAY_WOOL) return new Vector3f(0.3F, 0.3F, 0.3F);        // Серый
        if (block == Blocks.LIGHT_GRAY_WOOL) return new Vector3f(0.7F, 0.7F, 0.7F);  // Светло-серый
        if (block == Blocks.CYAN_WOOL) return new Vector3f(0.0F, 0.8F, 0.8F);        // Бирюзовый
        if (block == Blocks.PURPLE_WOOL) return new Vector3f(0.6F, 0.2F, 0.98F);     // Фиолетовый
        if (block == Blocks.BLUE_WOOL) return new Vector3f(0.0F, 0.0F, 1.0F);        // Синий
        if (block == Blocks.BROWN_WOOL) return new Vector3f(0.6F, 0.4F, 0.2F);       // Коричневый
        if (block == Blocks.GREEN_WOOL) return new Vector3f(0.0F, 0.5F, 0.0F);       // Зелёный
        if (block == Blocks.RED_WOOL) return new Vector3f(1.0F, 0.0F, 0.0F);         // Красный
        if (block == Blocks.BLACK_WOOL) return new Vector3f(0.1F, 0.1F, 0.1F);       // Чёрный
        return new Vector3f(1.0F, 1.0F, 1.0F); // По умолчанию белый
    }
}