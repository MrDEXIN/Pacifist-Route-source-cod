package name.modid.world.feature;

import com.mojang.serialization.Codec;
import name.modid.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.particle.WaterSuspendParticle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class NetherSulfurFeature extends Feature<DefaultFeatureConfig> {

    public NetherSulfurFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos pos = context.getOrigin();
        Random random = context.getRandom();

        if (random.nextInt(3) != 0) {
            return false;
        }

        return generateNetherSulfur(world, pos, random);
    }

    private boolean generateNetherSulfur(StructureWorldAccess world, BlockPos startPos, Random random) {
        int clusterSize = random.nextInt(15) + 7;
        boolean generated = false;

        for (int i = 0; i < clusterSize; i++) {
            for (int attempts = 0; attempts < 10; attempts++) {
                int offsetX = random.nextInt(8) - 4;
                int offsetY = random.nextInt(6) - 3;
                int offsetZ = random.nextInt(8) - 4;

                BlockPos orePos = startPos.add(offsetX, offsetY, offsetZ);

                if (canPlaceNetherOre(world, orePos, random)) {
                    world.setBlockState(orePos, ModBlocks.SULFUR_IN_NETHERRACK.getDefaultState(), 3);
                    generated = true;
                    break;
                }
            }
        }

        return generated;
    }

    private boolean canPlaceNetherOre(StructureWorldAccess world, BlockPos pos, Random random) {
        BlockState currentState = world.getBlockState(pos);
        BlockState aboveState = world.getBlockState(pos.up());

        // Можем заменить
        if (currentState.getBlock() != Blocks.NETHERRACK &&
                currentState.getBlock() != Blocks.WARPED_NYLIUM &&
                currentState.getBlock() != Blocks.CRIMSON_NYLIUM) {
            return false;
        }

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

        // Предпочитает воздух сверху, но не обязательно
        if (aboveState.isAir() && random.nextFloat() < 0.7f) {
            return true;
        }

        return random.nextFloat() < 0.3f; // Небольшой шанс без воздуха сверху
    }
}