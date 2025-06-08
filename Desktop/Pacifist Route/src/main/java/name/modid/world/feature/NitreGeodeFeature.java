package name.modid.world.feature;

import com.mojang.serialization.Codec;
import name.modid.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class NitreGeodeFeature extends Feature<NitreGeodeFeatureConfig> {

    public NitreGeodeFeature(Codec<NitreGeodeFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<NitreGeodeFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos pos = context.getOrigin();
        Random random = context.getRandom();
        NitreGeodeFeatureConfig config = context.getConfig();

        if (!isValidBiome(world, pos)) {
            return false;
        }

        int surfaceY = getSurfaceY(world, pos);
        if (surfaceY == -1) return false;

        int depth = 8 + random.nextInt(13);
        BlockPos geodeCenter = new BlockPos(pos.getX(), surfaceY - depth, pos.getZ());

        if (geodeCenter.getY() < 10 || geodeCenter.getY() > surfaceY - 3) return false;

        if (!isValidGenerationSite(world, geodeCenter)) {
            return false;
        }

        float geodeTypeChance = random.nextFloat();
        if (geodeTypeChance < 0.4f) {
            // 40% - одиночная жеода
            return generateSingleGeode(world, geodeCenter, random, config, surfaceY);
        } else if (geodeTypeChance < 0.8f) {
            // 40% - двойная жеода (2 соединенные сферы)
            return generateDoubleGeode(world, geodeCenter, random, config, surfaceY);
        } else {
            // 20% - тройная жеода (3 соединенные сферы)
            return generateTripleGeode(world, geodeCenter, random, config, surfaceY);
        }
    }

    private boolean isValidBiome(StructureWorldAccess world, BlockPos pos) {
        String biomeName = world.getBiome(pos).getKey().get().getValue().getPath().toLowerCase();

        if (biomeName.contains("ocean") || biomeName.contains("sea") ||
                biomeName.contains("deep") || biomeName.contains("sky") ||
                biomeName.contains("void") || biomeName.contains("river")) {
            return false;
        }

        return true;
    }

    private int getSurfaceY(StructureWorldAccess world, BlockPos pos) {
        for (int y = world.getTopY() - 1; y > 40; y--) {
            BlockPos checkPos = new BlockPos(pos.getX(), y, pos.getZ());
            BlockState currentBlock = world.getBlockState(checkPos);
            BlockState aboveBlock = world.getBlockState(checkPos.up());

            if (!currentBlock.isAir() && !currentBlock.isOf(Blocks.WATER) &&
                    !currentBlock.isOf(Blocks.LAVA)) {

                if (currentBlock.isOf(Blocks.SAND) || currentBlock.isOf(Blocks.SANDSTONE) ||
                        currentBlock.isOf(Blocks.STONE) || currentBlock.isOf(Blocks.DIRT) ||
                        currentBlock.isOf(Blocks.GRASS_BLOCK) || currentBlock.isOf(Blocks.RED_SAND)) {

                    if (aboveBlock.isAir() || aboveBlock.isOf(Blocks.GRASS) ||
                            aboveBlock.isOf(Blocks.TALL_GRASS) || aboveBlock.isOf(Blocks.DEAD_BUSH) ||
                            aboveBlock.isOf(Blocks.CACTUS)) {
                        return y;
                    }
                }
            }
        }
        return -1;
    }

    private boolean isValidGenerationSite(StructureWorldAccess world, BlockPos center) {
        int airBlocks = 0;
        int totalBlocks = 0;
        int solidBlocks = 0;
        int sandBlocks = 0;

        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos checkPos = center.add(x, y, z);

                    if (checkPos.getY() < world.getBottomY() || checkPos.getY() >= world.getTopY()) {
                        continue;
                    }

                    BlockState state = world.getBlockState(checkPos);
                    totalBlocks++;

                    if (state.isAir() || state.isOf(Blocks.WATER) || state.isOf(Blocks.LAVA) ||
                            state.isOf(Blocks.CAVE_AIR)) {
                        airBlocks++;
                    } else if (state.isOf(Blocks.SAND) || state.isOf(Blocks.SANDSTONE) || state.isOf(Blocks.RED_SAND)) {
                        sandBlocks++;
                        solidBlocks++;
                    } else if (!state.isReplaceable()) {
                        solidBlocks++;
                    }
                }
            }
        }

        float airPercentage = (airBlocks / (float) totalBlocks);
        float solidPercentage = (solidBlocks / (float) totalBlocks);
        float sandPercentage = (sandBlocks / (float) totalBlocks);

        return (airPercentage < 0.15f && solidPercentage > 0.7f) || sandPercentage > 0.5f;
    }


    private boolean generateSingleGeode(StructureWorldAccess world, BlockPos center, Random random, NitreGeodeFeatureConfig config, int surfaceY) {
        int radiusX = 6 + random.nextInt(5);
        int radiusY = 5 + random.nextInt(4);
        int radiusZ = 6 + random.nextInt(5);

        generateEllipsoidLayers(world, center, radiusX, radiusY, radiusZ, random, surfaceY);

        if (random.nextFloat() < 0.3f) {
            generateDestructiveCavity(world, center, radiusX, radiusY, radiusZ, random, surfaceY);
        }

        if (random.nextFloat() < 0.42f) {
            generateAdditionalCavities(world, center, radiusX, radiusY, radiusZ, random, surfaceY);
        }

        aggressiveSurfaceTrimming(world, center, Math.max(radiusX, radiusZ) + 6, surfaceY);

        placeBuddingBlocksFixed(world, center, radiusX, radiusY, radiusZ, random, surfaceY, 4, 12); // было 3-9, стало 4-12
        placeClusters(world, center, radiusX - 2, radiusY - 2, radiusZ - 2, random, surfaceY);

        if (random.nextFloat() < 0.75f) {
            createTunnelWithCavities(world, center, random, surfaceY);
        }

        return true;
    }

    private boolean generateDoubleGeode(StructureWorldAccess world, BlockPos center, Random random, NitreGeodeFeatureConfig config, int surfaceY) {
        int radius1X = 5 + random.nextInt(4);
        int radius1Y = 4 + random.nextInt(3);
        int radius1Z = 5 + random.nextInt(4);

        int offsetX = 6 + random.nextInt(4);
        int offsetY = random.nextInt(4) - 2;
        int offsetZ = random.nextInt(4) - 2;

        BlockPos center2 = center.add(offsetX, offsetY, offsetZ);

        int radius2X = 5 + random.nextInt(4);
        int radius2Y = 4 + random.nextInt(3);
        int radius2Z = 5 + random.nextInt(4);

        generateEllipsoidLayers(world, center, radius1X, radius1Y, radius1Z, random, surfaceY);
        generateEllipsoidLayers(world, center2, radius2X, radius2Y, radius2Z, random, surfaceY);

        connectSpheres(world, center, center2, random, surfaceY);

        if (random.nextFloat() < 0.3f) {
            generateDestructiveCavity(world, center, radius1X, radius1Y, radius1Z, random, surfaceY);
        }
        if (random.nextFloat() < 0.3f) {
            generateDestructiveCavity(world, center2, radius2X, radius2Y, radius2Z, random, surfaceY);
        }

        aggressiveSurfaceTrimming(world, center, Math.max(radius1X, radius1Z) + offsetX + radius2X + 6, surfaceY);

        int totalBudding = 9 + random.nextInt(6);
        int budding1 = Math.max(3, totalBudding / 2);
        int budding2 = totalBudding - budding1;

        placeBuddingBlocksFixed(world, center, radius1X, radius1Y, radius1Z, random, surfaceY, budding1, budding1);
        placeBuddingBlocksFixed(world, center2, radius2X, radius2Y, radius2Z, random, surfaceY, budding2, budding2);

        placeClusters(world, center, radius1X - 2, radius1Y - 2, radius1Z - 2, random, surfaceY);
        placeClusters(world, center2, radius2X - 2, radius2Y - 2, radius2Z - 2, random, surfaceY);

        if (random.nextFloat() < 0.75f) {
            BlockPos tunnelTarget = random.nextBoolean() ? center : center2;
            createTunnelWithCavities(world, tunnelTarget, random, surfaceY);
        }

        return true;
    }

    private boolean generateTripleGeode(StructureWorldAccess world, BlockPos center, Random random, NitreGeodeFeatureConfig config, int surfaceY) {
        int radius1X = 4 + random.nextInt(4);
        int radius1Y = 4 + random.nextInt(3);
        int radius1Z = 4 + random.nextInt(4);

        int offset2X = 5 + random.nextInt(4);
        int offset2Y = random.nextInt(4) - 2;
        int offset2Z = random.nextInt(4) - 2;
        BlockPos center2 = center.add(offset2X, offset2Y, offset2Z);

        int offset3X = random.nextInt(4) - 2;
        int offset3Y = random.nextInt(4) - 2;
        int offset3Z = 5 + random.nextInt(4);
        BlockPos center3 = center.add(offset3X, offset3Y, offset3Z);

        int radius2X = 4 + random.nextInt(3);
        int radius2Y = 4 + random.nextInt(3);
        int radius2Z = 4 + random.nextInt(3);

        int radius3X = 4 + random.nextInt(3);
        int radius3Y = 4 + random.nextInt(3);
        int radius3Z = 4 + random.nextInt(3);

        generateEllipsoidLayers(world, center, radius1X, radius1Y, radius1Z, random, surfaceY);
        generateEllipsoidLayers(world, center2, radius2X, radius2Y, radius2Z, random, surfaceY);
        generateEllipsoidLayers(world, center3, radius3X, radius3Y, radius3Z, random, surfaceY);

        connectSpheres(world, center, center2, random, surfaceY);
        connectSpheres(world, center, center3, random, surfaceY);
        connectSpheres(world, center2, center3, random, surfaceY);

        if (random.nextFloat() < 0.3f) {
            generateDestructiveCavity(world, center, radius1X, radius1Y, radius1Z, random, surfaceY);
        }
        if (random.nextFloat() < 0.3f) {
            generateDestructiveCavity(world, center2, radius2X, radius2Y, radius2Z, random, surfaceY);
        }
        if (random.nextFloat() < 0.3f) {
            generateDestructiveCavity(world, center3, radius3X, radius3Y, radius3Z, random, surfaceY);
        }

        int maxRadius = Math.max(Math.max(radius1X, radius2X), radius3X) +
                Math.max(Math.max(offset2X, offset3X), 0) + 6;
        aggressiveSurfaceTrimming(world, center, maxRadius, surfaceY);

        int totalBudding = 12 + random.nextInt(8);
        int budding1 = Math.max(3, totalBudding / 3);
        int budding2 = Math.max(3, totalBudding / 3);
        int budding3 = totalBudding - budding1 - budding2;

        placeBuddingBlocksFixed(world, center, radius1X, radius1Y, radius1Z, random, surfaceY, budding1, budding1);
        placeBuddingBlocksFixed(world, center2, radius2X, radius2Y, radius2Z, random, surfaceY, budding2, budding2);
        placeBuddingBlocksFixed(world, center3, radius3X, radius3Y, radius3Z, random, surfaceY, budding3, budding3);

        placeClusters(world, center, radius1X - 2, radius1Y - 2, radius1Z - 2, random, surfaceY);
        placeClusters(world, center2, radius2X - 2, radius2Y - 2, radius2Z - 2, random, surfaceY);
        placeClusters(world, center3, radius3X - 2, radius3Y - 2, radius3Z - 2, random, surfaceY);

        if (random.nextFloat() < 0.75f) {
            float sphereChoice = random.nextFloat();
            BlockPos tunnelTarget;

            if (sphereChoice < 0.33f) {
                tunnelTarget = center;
            } else if (sphereChoice < 0.66f) {
                tunnelTarget = center2;
            } else {
                tunnelTarget = center3;
            }

            createTunnelWithCavities(world, tunnelTarget, random, surfaceY);
        }

        return true;
    }

    private void connectSpheres(StructureWorldAccess world, BlockPos center1, BlockPos center2, Random random, int surfaceY) {
        BlockPos current = center1;
        BlockPos target = center2;

        while (!current.equals(target)) {
            int deltaX = Integer.compare(target.getX(), current.getX());
            int deltaY = Integer.compare(target.getY(), current.getY());
            int deltaZ = Integer.compare(target.getZ(), current.getZ());

            current = current.add(deltaX, deltaY, deltaZ);

            if (current.getY() >= surfaceY - 3) continue;

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) <= 2) {
                            BlockPos tunnelPos = current.add(dx, dy, dz);
                            if (tunnelPos.getY() >= world.getBottomY() && tunnelPos.getY() < world.getTopY()) {
                                world.setBlockState(tunnelPos, Blocks.AIR.getDefaultState(), 3);
                            }
                        }
                    }
                }
            }
        }
    }

    private void generateDestructiveCavity(StructureWorldAccess world, BlockPos center, int radiusX, int radiusY, int radiusZ, Random random, int surfaceY) {
        float destructionPercent = 0.2f + random.nextFloat() * 0.27f;

        int destroyRadiusX = (int) (radiusX * destructionPercent);
        int destroyRadiusY = (int) (radiusY * destructionPercent);
        int destroyRadiusZ = (int) (radiusZ * destructionPercent);

        int offsetX = random.nextInt(radiusX / 2) - radiusX / 4;
        int offsetY = random.nextInt(radiusY / 2) - radiusY / 4;
        int offsetZ = random.nextInt(radiusZ / 2) - radiusZ / 4;

        BlockPos cavityCenter = center.add(offsetX, offsetY, offsetZ);

        for (int x = -destroyRadiusX; x <= destroyRadiusX; x++) {
            for (int y = -destroyRadiusY; y <= destroyRadiusY; y++) {
                for (int z = -destroyRadiusZ; z <= destroyRadiusZ; z++) {
                    BlockPos pos = cavityCenter.add(x, y, z);

                    if (pos.getY() >= surfaceY - 3) continue;

                    double distance = Math.sqrt(
                            (double) (x * x) / (destroyRadiusX * destroyRadiusX) +
                                    (double) (y * y) / (destroyRadiusY * destroyRadiusY) +
                                    (double) (z * z) / (destroyRadiusZ * destroyRadiusZ)
                    );

                    if (distance <= 1.0) {
                        if (pos.getY() >= world.getBottomY() && pos.getY() < world.getTopY()) {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                        }
                    }
                }
            }
        }
    }

    private void createTunnelWithCavities(StructureWorldAccess world, BlockPos center, Random random, int surfaceY) {
        BlockPos currentPos = center;
        int stepsWithoutY = 0;
        int maxSteps = 120;
        int steps = 0;

        while (currentPos.getY() < surfaceY && steps < maxSteps) {
            steps++;

            int offsetX = random.nextInt(3) - 1;
            int offsetZ = random.nextInt(3) - 1;
            int offsetY = 0;

            if (stepsWithoutY >= 2 && currentPos.getY() < surfaceY - 3) {
                offsetY = 1;
                stepsWithoutY = 0;
            } else {
                stepsWithoutY++;
            }

            if (offsetX == 0 && offsetZ == 0) {
                offsetX = random.nextBoolean() ? 1 : -1;
            }

            currentPos = currentPos.add(offsetX, offsetY, offsetZ);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = 0; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (Math.abs(dx) + Math.abs(dz) <= 1) {
                            BlockPos tunnelPos = currentPos.add(dx, dy, dz);

                            if (tunnelPos.getY() >= world.getBottomY() && tunnelPos.getY() < world.getTopY()) {
                                BlockState currentState = world.getBlockState(tunnelPos);

                                if (!currentState.isAir()) {
                                    world.setBlockState(tunnelPos, Blocks.AIR.getDefaultState(), 3);
                                }
                            }
                        }
                    }
                }
            }

            if (random.nextFloat() < 0.15f) {
                generateSmallCavity(world, currentPos, 1 + random.nextInt(2), 1, 1 + random.nextInt(2), surfaceY);
            }

            double horizontalDistance = Math.sqrt(
                    Math.pow(currentPos.getX() - center.getX(), 2) +
                            Math.pow(currentPos.getZ() - center.getZ(), 2)
            );

            if (horizontalDistance > 20) {
                currentPos = currentPos.up(2);
                stepsWithoutY = 0;
            }
        }

        while (currentPos.getY() <= surfaceY + 2) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 2; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (Math.abs(dx) + Math.abs(dz) <= 1) {
                            BlockPos exitPos = currentPos.add(dx, dy, dz);

                            if (exitPos.getY() >= world.getBottomY() && exitPos.getY() < world.getTopY()) {
                                BlockState state = world.getBlockState(exitPos);

                                if (!state.isAir()) {
                                    world.setBlockState(exitPos, Blocks.AIR.getDefaultState(), 3);
                                }
                            }
                        }
                    }
                }
            }

            currentPos = currentPos.up();

            if (currentPos.getY() > surfaceY + 5) {
                break;
            }
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (Math.abs(dx) + Math.abs(dz) <= 1) {
                    BlockPos surfacePos = new BlockPos(currentPos.getX() + dx, surfaceY + 1, currentPos.getZ() + dz);

                    if (surfacePos.getY() >= world.getBottomY() && surfacePos.getY() < world.getTopY()) {
                        BlockState state = world.getBlockState(surfacePos);

                        if (state.isOf(Blocks.SAND) || state.isOf(Blocks.SANDSTONE) ||
                                state.isOf(Blocks.CLAY) || state.isOf(Blocks.MUD) ||
                                state.isOf(Blocks.RED_SAND)) {
                            world.setBlockState(surfacePos, Blocks.AIR.getDefaultState(), 3);
                        }
                    }
                }
            }
        }
    }

    private void generateEllipsoidLayers(StructureWorldAccess world, BlockPos center, int radiusX, int radiusY, int radiusZ, Random random, int surfaceY) {
        for (int x = -radiusX - 3; x <= radiusX + 3; x++) {
            for (int y = -radiusY - 3; y <= radiusY + 3; y++) {
                for (int z = -radiusZ - 3; z <= radiusZ + 3; z++) {
                    BlockPos pos = center.add(x, y, z);

                    if (pos.getY() >= surfaceY - 3) {
                        continue;
                    }

                    double distanceX = (double) x / radiusX;
                    double distanceY = (double) y / radiusY;
                    double distanceZ = (double) z / radiusZ;
                    double ellipsoidDistance = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);

                    if (pos.getY() >= world.getBottomY() && pos.getY() < world.getTopY()) {
                        BlockState blockToPlace = determineBlockType(ellipsoidDistance, random);

                        if (blockToPlace != null) {
                            world.setBlockState(pos, blockToPlace, 3);
                        }
                    }
                }
            }
        }
    }

    private BlockState determineBlockType(double distance, Random random) {
        if (distance <= 0.6) {
            return Blocks.AIR.getDefaultState();
        } else if (distance <= 0.8) {
            return ModBlocks.NITRE_CRYSTAL_BLOCK.getDefaultState();
        } else if (distance <= 1.0) {
            return random.nextFloat() < 0.7f ?
                    Blocks.SAND.getDefaultState() :
                    Blocks.MUD.getDefaultState();
        } else if (distance <= 1.3) {
            return Blocks.CLAY.getDefaultState();
        }

        return null;
    }

    private void generateAdditionalCavities(StructureWorldAccess world, BlockPos center, int radiusX, int radiusY, int radiusZ, Random random, int surfaceY) {
        int cavityCount = 3 + random.nextInt(4);

        for (int i = 0; i < cavityCount; i++) {
            int offsetX = random.nextInt(radiusX) - radiusX / 2;
            int offsetY = random.nextInt(radiusY) - radiusY / 2;
            int offsetZ = random.nextInt(radiusZ) - radiusZ / 2;

            BlockPos cavityCenter = center.add(offsetX, offsetY, offsetZ);

            int cavityRadiusX = 2 + random.nextInt(4);
            int cavityRadiusY = 1 + random.nextInt(3);
            int cavityRadiusZ = 2 + random.nextInt(4);

            generateSmallCavity(world, cavityCenter, cavityRadiusX, cavityRadiusY, cavityRadiusZ, surfaceY);
            addClustersToSmallCavity(world, cavityCenter, cavityRadiusX, cavityRadiusY, cavityRadiusZ, random, surfaceY);
        }
    }

    private void generateSmallCavity(StructureWorldAccess world, BlockPos center, int radiusX, int radiusY, int radiusZ, int surfaceY) {
        for (int x = -radiusX; x <= radiusX; x++) {
            for (int y = -radiusY; y <= radiusY; y++) {
                for (int z = -radiusZ; z <= radiusZ; z++) {
                    BlockPos pos = center.add(x, y, z);

                    if (pos.getY() >= surfaceY - 3) {
                        continue;
                    }

                    double distanceX = (double) x / radiusX;
                    double distanceY = (double) y / radiusY;
                    double distanceZ = (double) z / radiusZ;
                    double ellipsoidDistance = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);

                    if (ellipsoidDistance <= 1.0) {
                        if (pos.getY() >= world.getBottomY() && pos.getY() < world.getTopY()) {
                            BlockState currentState = world.getBlockState(pos);
                            if (!currentState.isAir()) {
                                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                            }
                        }
                    }
                }
            }
        }
    }

    private void addClustersToSmallCavity(StructureWorldAccess world, BlockPos center, int radiusX, int radiusY, int radiusZ, Random random, int surfaceY) {
        int clusterCount = 4 + random.nextInt(5);

        for (int i = 0; i < clusterCount; i++) {
            double angle1 = random.nextDouble() * Math.PI * 2;
            double angle2 = random.nextDouble() * Math.PI;

            int wallX = (int) (Math.sin(angle2) * Math.cos(angle1) * (radiusX - 0.2));
            int wallY = (int) (Math.cos(angle2) * (radiusY - 0.2));
            int wallZ = (int) (Math.sin(angle2) * Math.sin(angle1) * (radiusZ - 0.2));

            BlockPos clusterPos = center.add(wallX, wallY, wallZ);

            if (clusterPos.getY() >= surfaceY - 3) continue;

            if (world.getBlockState(clusterPos).isAir()) {
                for (Direction direction : Direction.values()) {
                    BlockPos adjacentPos = clusterPos.offset(direction);
                    BlockState adjacentState = world.getBlockState(adjacentPos);

                    if (adjacentState.isOf(ModBlocks.NITRE_CRYSTAL_BLOCK) ||
                            adjacentState.isOf(Blocks.SAND) ||
                            adjacentState.isOf(Blocks.MUD) ||
                            adjacentState.isOf(ModBlocks.BUDDING_NITRE)) {

                        BlockState clusterState = getSmallCluster(random);
                        world.setBlockState(clusterPos, clusterState, 3);
                        break;
                    }
                }
            }
        }
    }

    private BlockState getSmallCluster(Random random) {
        float chance = random.nextFloat();

        if (chance < 0.6f) {
            return ModBlocks.SMALL_NITRE_BUD.getDefaultState();
        } else if (chance < 0.9f) {
            return ModBlocks.MEDIUM_NITRE_BUD.getDefaultState();
        } else {
            return ModBlocks.LARGE_NITRE_BUD.getDefaultState();
        }
    }

    private void aggressiveSurfaceTrimming(StructureWorldAccess world, BlockPos center, int checkRadius, int surfaceY) {
        for (int x = -checkRadius; x <= checkRadius; x++) {
            for (int z = -checkRadius; z <= checkRadius; z++) {
                for (int y = surfaceY - 3; y <= surfaceY + 5; y++) {
                    BlockPos pos = new BlockPos(center.getX() + x, y, center.getZ() + z);

                    if (pos.getY() >= world.getBottomY() && pos.getY() < world.getTopY()) {
                        BlockState state = world.getBlockState(pos);

                        if (pos.getY() >= surfaceY - 2 && (
                                state.isOf(Blocks.CLAY) ||
                                        state.isOf(Blocks.MUD) ||
                                        state.isOf(ModBlocks.NITRE_CRYSTAL_BLOCK) ||
                                        state.isOf(ModBlocks.BUDDING_NITRE))) {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                        }
                    }
                }
            }
        }
    }
    private void placeBuddingBlocksFixed(StructureWorldAccess world, BlockPos center, int radiusX, int radiusY, int radiusZ, Random random, int surfaceY, int minBudding, int maxBudding) {
        int buddingCount = minBudding + random.nextInt(maxBudding - minBudding + 1);
        int placed = 0;
        int attempts = 0;
        int maxAttempts = buddingCount * 10;

        while (placed < buddingCount && attempts < maxAttempts) {
            attempts++;

            double angle1 = random.nextDouble() * Math.PI * 2;
            double angle2 = random.nextDouble() * Math.PI;

            int wallX = (int) (Math.sin(angle2) * Math.cos(angle1) * (radiusX * 0.8));
            int wallY = (int) (Math.cos(angle2) * (radiusY * 0.8));
            int wallZ = (int) (Math.sin(angle2) * Math.sin(angle1) * (radiusZ * 0.8));

            BlockPos buddingPos = center.add(wallX, wallY, wallZ);

            if (buddingPos.getY() >= surfaceY - 3) continue;

            if (world.getBlockState(buddingPos).isOf(ModBlocks.NITRE_CRYSTAL_BLOCK)) {
                world.setBlockState(buddingPos, ModBlocks.BUDDING_NITRE.getDefaultState(), 3);
                placed++;
            }
        }
    }

    private void placeClusters(StructureWorldAccess world, BlockPos center, int radiusX, int radiusY, int radiusZ, Random random, int surfaceY) {
        int clusterCount = 15 + random.nextInt(20);

        for (int i = 0; i < clusterCount; i++) {
            double angle1 = random.nextDouble() * Math.PI * 2;
            double angle2 = random.nextDouble() * Math.PI;

            int wallX = (int) (Math.sin(angle2) * Math.cos(angle1) * radiusX);
            int wallY = (int) (Math.cos(angle2) * radiusY);
            int wallZ = (int) (Math.sin(angle2) * Math.sin(angle1) * radiusZ);

            BlockPos clusterPos = center.add(wallX, wallY, wallZ);

            if (clusterPos.getY() >= surfaceY - 3) continue;

            if (world.getBlockState(clusterPos).isAir()) {
                for (Direction direction : Direction.values()) {
                    BlockPos adjacentPos = clusterPos.offset(direction);
                    BlockState adjacentState = world.getBlockState(adjacentPos);

                    if (adjacentState.isOf(ModBlocks.NITRE_CRYSTAL_BLOCK) ||
                            adjacentState.isOf(Blocks.SAND) ||
                            adjacentState.isOf(Blocks.MUD) ||
                            adjacentState.isOf(ModBlocks.BUDDING_NITRE)) {

                        BlockState clusterState = getClusterState(random);
                        world.setBlockState(clusterPos, clusterState, 3);
                        break;
                    }
                }
            }
        }
    }

    private BlockState getClusterState(Random random) {
        float chance = random.nextFloat();

        if (chance < 0.3f) {
            return ModBlocks.TINY_NITRE_BUD.getDefaultState();
        } else if (chance < 0.5f) {
            return ModBlocks.SMALL_NITRE_BUD.getDefaultState();
        } else if (chance < 0.7f) {
            return ModBlocks.MEDIUM_NITRE_BUD.getDefaultState();
        } else if (chance < 0.9f) {
            return ModBlocks.LARGE_NITRE_BUD.getDefaultState();
        } else {
            return ModBlocks.NITRE_CLUSTER.getDefaultState();
        }
    }
}