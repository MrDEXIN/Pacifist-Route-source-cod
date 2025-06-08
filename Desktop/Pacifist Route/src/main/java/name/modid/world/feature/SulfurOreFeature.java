package name.modid.world.feature;

import com.mojang.serialization.Codec;
import name.modid.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class SulfurOreFeature extends Feature<DefaultFeatureConfig> {

    public SulfurOreFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos pos = context.getOrigin();
        Random random = context.getRandom();

        return generateSulfurOre(world, pos, random);
    }

    private boolean generateSulfurOre(StructureWorldAccess world, BlockPos startPos, Random random) {
        boolean generated = false;

        // Поиск лавовых озер в радиусе
        for (int x = -16; x <= 16; x++) {
            for (int y = -8; y <= 8; y++) {
                for (int z = -16; z <= 16; z++) {
                    BlockPos checkPos = startPos.add(x, y, z);

                    if (world.getBlockState(checkPos).getBlock() == Blocks.LAVA) {
                        // Найдено лавовое озеро, генерируем серную руду рядом
                        if (tryGenerateNearLava(world, checkPos, random)) {
                            generated = true;
                        }
                    }
                }
            }
        }

        return generated;
    }

    private boolean tryGenerateNearLava(StructureWorldAccess world, BlockPos lavaPos, Random random) {
        boolean generated = false;
        int y = lavaPos.getY();

        BlockState oreType;
        int clusterMin, clusterMax;
        float chance;

        if (y < 0) {
            // Deep sulfur (ниже 0)
            oreType = ModBlocks.SULFUR_IN_DEEP.getDefaultState();
            clusterMin = 2;
            clusterMax = 6;
            chance = 0.32f;
        } else {
            // Stone sulfur (поверхность)
            oreType = ModBlocks.SULFUR_IN_STONE.getDefaultState();
            clusterMin = 1;
            clusterMax = 5;
            chance = 0.60f;
        }

        if (random.nextFloat() > chance) {
            return false;
        }

        // Генерируем кластер руды
        int clusterSize = random.nextInt(clusterMax - clusterMin + 1) + clusterMin;

        for (int i = 0; i < clusterSize; i++) {
            // Ищем подходящие позиции рядом с лавой
            for (int attempts = 0; attempts < 20; attempts++) {
                int offsetX = random.nextInt(6) - 3;
                int offsetY = random.nextInt(4) - 2;
                int offsetZ = random.nextInt(6) - 3;

                BlockPos orePos = lavaPos.add(offsetX, offsetY, offsetZ);

                if (canPlaceOre(world, orePos, oreType)) {
                    world.setBlockState(orePos, oreType, 3);
                    generated = true;
                    break;
                }
            }
        }

        return generated;
    }

    private boolean canPlaceOre(StructureWorldAccess world, BlockPos pos, BlockState oreState) {
        BlockState currentState = world.getBlockState(pos);
        BlockState aboveState = world.getBlockState(pos.up());

        // Проверяем, что можем заменить блок
        boolean canReplace = false;
        if (oreState == ModBlocks.SULFUR_IN_DEEP.getDefaultState()) {
            canReplace = currentState.getBlock() == Blocks.DEEPSLATE;
        } else if (oreState == ModBlocks.SULFUR_IN_STONE.getDefaultState()) {
            canReplace = currentState.getBlock() == Blocks.STONE;
        }

        if (!canReplace) return false;

        // Не может касаться лавы
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (world.getBlockState(pos.add(x, y, z)).getBlock() == Blocks.LAVA) {
                        return false;
                    }
                }
            }
        }

        // Верхняя часть должна быть над воздухом (для surface/deep)
        return aboveState.isAir();
    }
}