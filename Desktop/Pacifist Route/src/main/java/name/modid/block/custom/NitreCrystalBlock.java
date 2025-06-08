package name.modid.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class NitreCrystalBlock extends Block {

    public NitreCrystalBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {

        if (stateFrom.getBlock() == this) {
            return true;
        }

        if (hasFullHitbox(stateFrom)) {
            return true;
        }

        return super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    private boolean hasFullHitbox(BlockState state) {
        VoxelShape shape = state.getCollisionShape(null, null);
        VoxelShape fullShape = VoxelShapes.fullCube();

        return VoxelShapes.matchesAnywhere(shape, fullShape, (a, b) -> a && b);
    }
}
