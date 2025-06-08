package name.modid.block;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class NitreBudBlock extends Block implements Waterloggable {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected final VoxelShape[] shapes;

    public NitreBudBlock(Settings settings, VoxelShape[] shapes) {
        super(settings);
        this.shapes = shapes;
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.UP)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return shapes[state.get(FACING).getId()];
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        BlockState state = this.getDefaultState()
                .with(FACING, direction)
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);

        if (!canPlaceAt(state, ctx.getWorld(), ctx.getBlockPos())) {
            return null;
        }

        return state;
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

        Direction facing = state.get(FACING);
        if (direction == facing.getOpposite() || !canPlaceAt(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public boolean canPlaceAt(BlockState state, WorldAccess world, BlockPos pos) {
        Direction facing = state.get(FACING);
        BlockPos attachedPos = pos.offset(facing.getOpposite());
        BlockState attachedState = world.getBlockState(attachedPos);

        if (!attachedState.isSideSolidFullSquare(world, attachedPos, facing)) {
            return false;
        }

        if (this == ModBlocks.NITRE_CLUSTER) {
            if (facing == Direction.WEST || facing == Direction.EAST) {
                BlockPos abovePos = pos.offset(Direction.UP);
                if (!world.getBlockState(abovePos).isAir()) {
                    return false;
                }
            } else if (facing == Direction.DOWN || facing == Direction.UP) {
                BlockPos forwardPos = pos.offset(facing);
                if (!world.getBlockState(forwardPos).isAir()) {
                    return false;
                }
            }
        }

        return true;
    }
}