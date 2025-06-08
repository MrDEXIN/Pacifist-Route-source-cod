package name.modid.block;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class BuddingNitreBlock extends AmethystBlock implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final Direction[] DIRECTIONS = Direction.values();

    public BuddingNitreBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false));
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(5) == 0) {
            Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            Block block = null;
            if (canGrowIn(blockState)) {
                block = ModBlocks.TINY_NITRE_BUD;
            } else if (blockState.isOf(ModBlocks.TINY_NITRE_BUD) && blockState.get(NitreBudBlock.FACING) == direction) {
                block = ModBlocks.SMALL_NITRE_BUD;
            } else if (blockState.isOf(ModBlocks.SMALL_NITRE_BUD) && blockState.get(NitreBudBlock.FACING) == direction) {
                block = ModBlocks.MEDIUM_NITRE_BUD;
            } else if (blockState.isOf(ModBlocks.MEDIUM_NITRE_BUD) && blockState.get(NitreBudBlock.FACING) == direction) {
                block = ModBlocks.LARGE_NITRE_BUD;
            } else if (blockState.isOf(ModBlocks.LARGE_NITRE_BUD) && blockState.get(NitreBudBlock.FACING) == direction) {
                block = ModBlocks.NITRE_CLUSTER;
            }

            if (block != null) {
                BlockState blockState2 = block.getDefaultState()
                        .with(NitreBudBlock.FACING, direction)
                        .with(NitreBudBlock.WATERLOGGED, blockState.getFluidState().getFluid() == Fluids.WATER);
                world.setBlockState(blockPos, blockState2);
            }
        }
    }

    public static boolean canGrowIn(BlockState state) {
        return state.isAir() || (state.isOf(Blocks.WATER) && state.getFluidState().getLevel() == 8);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
}