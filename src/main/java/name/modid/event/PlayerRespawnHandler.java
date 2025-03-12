package name.modid.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRespawnHandler {

    // Храним информацию о том, был ли игрок убит молнией
    private static final Map<UUID, Boolean> killedByLightning = new HashMap<>();

    public static void register() {
        // Регистрируем обработчик события смерти
        ServerPlayerEvents.ALLOW_DEATH.register((player, source, amount) -> {
            // Проверяем, что смерть вызвана молнией
            if (source.isOf(DamageTypes.LIGHTNING_BOLT)) {
                // Сохраняем информацию о том, что игрок был убит молнией
                killedByLightning.put(player.getUuid(), true);
            }
            return true; // Разрешаем смерть
        });

        // Регистрируем обработчик события возрождения
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            // Проверяем, был ли игрок убит молнией
            if (killedByLightning.getOrDefault(newPlayer.getUuid(), false)) {
                // Добавляем череп скелета в инвентарь игрока
                newPlayer.getInventory().offerOrDrop(new ItemStack(Items.PLAYER_HEAD, 1));
                // Удаляем информацию о смерти от молнии
                killedByLightning.remove(newPlayer.getUuid());
            }
        });
    }
}