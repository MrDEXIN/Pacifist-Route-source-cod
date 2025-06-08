package name.modid.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import name.modid.block.ModBlocks;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.literal;

public class GeodeCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(GeodeCommands::registerCommands);
    }


    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher,
                                         CommandRegistryAccess registryAccess,
                                         CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(literal("geode")
                .then(literal("find")
                        .executes(GeodeCommands::findGeodes))
                .then(literal("spawn")
                        .executes(GeodeCommands::spawnGeode))
                .then(literal("check")
                        .executes(GeodeCommands::checkArea))
                .then(literal("bigfind")
                        .executes(GeodeCommands::bigFindGeodes))
        );
    }

    private static int findGeodes(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        BlockPos playerPos = BlockPos.ofFloored(source.getPosition());

        source.sendFeedback(() -> Text.literal("§6Поиск жеод в радиусе 200 блоков..."), false);

        int found = 0;
        for (int x = -200; x <= 200; x += 8) {
            for (int z = -200; z <= 200; z += 8) {
                for (int y = 5; y <= 60; y += 5) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (world.getBlockState(checkPos).isOf(ModBlocks.NITRE_CRYSTAL_BLOCK)) {
                        found++;
                        int distance = (int) Math.sqrt(x*x + z*z);
                        source.sendFeedback(() -> Text.literal(
                                "§aЖеода на " + checkPos.getX() + ", " +
                                        checkPos.getY() + ", " + checkPos.getZ() + " (дистанция: " + distance + ")"), false);
                    }
                }
            }
        }

        final int finalFound = found;
        source.sendFeedback(() -> Text.literal("§eВсего найдено жеод: " + finalFound), false);
        return found;
    }

    private static int bigFindGeodes(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        BlockPos playerPos = BlockPos.ofFloored(source.getPosition());

        source.sendFeedback(() -> Text.literal("§6Поиск жеод в радиусе 1000 блоков... (может занять время)"), false);

        int found = 0;
        for (int x = -1000; x <= 1000; x += 16) {
            for (int z = -1000; z <= 1000; z += 16) {
                for (int y = 5; y <= 60; y += 8) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (world.getBlockState(checkPos).isOf(ModBlocks.NITRE_CRYSTAL_BLOCK)) {
                        found++;
                        int distance = (int) Math.sqrt(x*x + z*z);
                        source.sendFeedback(() -> Text.literal(
                                "§aЖеода на " + checkPos.getX() + ", " +
                                        checkPos.getY() + ", " + checkPos.getZ() + " (дистанция: " + distance + ")"), false);
                    }
                }
            }
        }

        final int finalFound = found;
        source.sendFeedback(() -> Text.literal("§eВсего найдено жеод: " + finalFound), false);
        return found;
    }

    private static int spawnGeode(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        BlockPos playerPos = BlockPos.ofFloored(source.getPosition().add(0, -3, 0));

        source.sendFeedback(() -> Text.literal("§6Создание жеоды вручную..."), false);

        try {
            generateManualGeode(world, playerPos);

            source.sendFeedback(() -> Text.literal(
                    "§aЖеода создана на " + playerPos.getX() + ", " +
                            playerPos.getY() + ", " + playerPos.getZ()), false);
            return 1;

        } catch (Exception e) {
            source.sendFeedback(() -> Text.literal("§cОшибка создания жеоды: " + e.getMessage()), false);
            e.printStackTrace();
            return 0;
        }
    }

    private static void generateManualGeode(ServerWorld world, BlockPos center) {
        int radius = 5;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos currentPos = center.add(x, y, z);

                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance <= radius - 2) {
                        world.setBlockState(currentPos, Blocks.AIR.getDefaultState(), 3);
                    } else if (distance <= radius - 1.5) {
                        world.setBlockState(currentPos, ModBlocks.NITRE_CRYSTAL_BLOCK.getDefaultState(), 3);
                    } else if (distance <= radius - 0.5) {
                        BlockState innerLayer = world.random.nextFloat() < 0.75f ?
                                Blocks.SAND.getDefaultState() :
                                Blocks.DIRT.getDefaultState();
                        world.setBlockState(currentPos, innerLayer, 3);
                    } else if (distance <= radius) {
                        world.setBlockState(currentPos, Blocks.CLAY.getDefaultState(), 3);
                    }
                }
            }
        }
    }

    private static int checkArea(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        BlockPos playerPos = BlockPos.ofFloored(source.getPosition());

        source.sendFeedback(() -> Text.literal("§6Проверка области вокруг игрока..."), false);
        source.sendFeedback(() -> Text.literal("§eBiome: " + world.getBiome(playerPos).getKey().get().getValue()), false);
        source.sendFeedback(() -> Text.literal("§eY-level: " + playerPos.getY()), false);
        source.sendFeedback(() -> Text.literal("§eПозиция: " + playerPos.getX() + ", " + playerPos.getY() + ", " + playerPos.getZ()), false);

        // Проверяем блок под ногами
        BlockPos underPos = playerPos.down(5);
        source.sendFeedback(() -> Text.literal("§eБлок на глубине 5: " + world.getBlockState(underPos).getBlock().getName().getString()), false);

        return 1;
    }
}