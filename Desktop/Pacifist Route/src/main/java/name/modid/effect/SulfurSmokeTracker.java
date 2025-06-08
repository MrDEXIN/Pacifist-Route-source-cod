package name.modid.effect;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SulfurSmokeTracker {
    private static final Map<UUID, Long> playersInSmokeZone = new HashMap<>();
    private static final Map<UUID, Long> playersInSmokeDirect = new HashMap<>();

    private static final long NAUSEA_DELAY = 5000;
    private static final int ZONE_RADIUS = 5;

    public static void checkPlayersNearSulfurBlock(ServerWorld world, BlockPos sulfurBlockPos) {
        BlockPos smokePos = sulfurBlockPos.up();
        Box checkArea = new Box(smokePos).expand(ZONE_RADIUS + 1);

        for (PlayerEntity player : world.getEntitiesByClass(PlayerEntity.class, checkArea, p -> true)) {
            UUID playerId = player.getUuid();
            double distance = player.getPos().distanceTo(smokePos.toCenterPos());

            // Прямой контакт с дымом
            if (distance <= 1.5) {
                handleDirectSmokeContact(player);
            }

            // В зоне действия дыма
            if (distance <= ZONE_RADIUS) {
                handleSmokeZoneExposure(player);
            } else {
                // Вышел из зоны - сбрасываем состояния
                playersInSmokeZone.remove(playerId);
                playersInSmokeDirect.remove(playerId);
            }
        }
    }

    private static void handleDirectSmokeContact(PlayerEntity player) {
        UUID playerId = player.getUuid();
        long currentTime = System.currentTimeMillis();

        if (!playersInSmokeDirect.containsKey(playerId)) {
            playersInSmokeDirect.put(playerId, currentTime);
        }

        // МГНОВЕННОЕ отравление каждый раз
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.POISON,
                60, // 3 секунды
                0,
                false,
                true,
                true
        ));

        // Тошнота при долгом прямом контакте
        long timeInSmoke = currentTime - playersInSmokeDirect.get(playerId);
        if (timeInSmoke > 2000) { // 2 секунды прямого контакта
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NAUSEA,
                    100, // 5 секунд
                    0,
                    false,
                    true,
                    true
            ));
        }
    }

    private static void handleSmokeZoneExposure(PlayerEntity player) {
        UUID playerId = player.getUuid();
        long currentTime = System.currentTimeMillis();

        if (!playersInSmokeZone.containsKey(playerId)) {
            playersInSmokeZone.put(playerId, currentTime);
            return;
        }

        long timeInZone = currentTime - playersInSmokeZone.get(playerId);

        // Тошнота через 5 секунд
        if (timeInZone >= NAUSEA_DELAY) {
            // Применяем эффект тошноты каждый раз
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NAUSEA,
                    200, // 10 секунд
                    0,
                    false,
                    true,
                    true
            ));

            // Слабость через еще 10 секунд
            if (timeInZone >= NAUSEA_DELAY + 10000) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.WEAKNESS,
                        300, // 15 секунд
                        0,
                        false,
                        true,
                        true
                ));
            }
        }
    }

    public static void clearPlayerData(UUID playerId) {
        playersInSmokeZone.remove(playerId);
        playersInSmokeDirect.remove(playerId);
    }
}