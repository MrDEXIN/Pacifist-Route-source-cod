package name.modid.effect;

import name.modid.block.ModBlocks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

public class SulfurSmokeHandler {

    private static final Set<Block> SULFUR_BLOCKS = Set.of(
            ModBlocks.SULFUR_IN_STONE,
            ModBlocks.SULFUR_IN_DEEP,
            ModBlocks.SULFUR_IN_NETHERRACK
    );

    public static void registerSmokeHandler() {
        ServerTickEvents.END_WORLD_TICK.register(SulfurSmokeHandler::onWorldTick);
    }

    private static void onWorldTick(ServerWorld world) {

        if (world.getTime() % 20 != 0) return;

        for (PlayerEntity player : world.getPlayers()) {
            checkSulfurBlocksNearPlayer(world, player);
        }
    }

    private static void checkSulfurBlocksNearPlayer(ServerWorld world, PlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();

        // Проверяем в радиусе 8 блоков от игрока
        for (int x = -8; x <= 8; x++) {
            for (int y = -4; y <= 8; y++) {
                for (int z = -8; z <= 8; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);

                    if (SULFUR_BLOCKS.contains(world.getBlockState(checkPos).getBlock())) {
                        BlockPos smokePos = checkPos.up();

                        // Проверяем, что над блоком воздух (есть дым)
                        if (world.getBlockState(smokePos).isAir()) {
                            SulfurSmokeTracker.checkPlayersNearSulfurBlock(world, checkPos);
                        }
                    }
                }
            }
        }
    }
}