package name.modid.block;

import name.modid.PacifistRoute;
import name.modid.block.entity.MortarBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class MortarBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = VoxelShapes.cuboid(5.0 / 16.0, 0.0, 5.0 / 16.0, 11.0 / 16.0, 5.0 / 16.0, 11.0 / 16.0);

    public MortarBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MortarBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && world.getBlockEntity(pos) instanceof MortarBlockEntity mortar) {
            ItemStack stack = player.getStackInHand(hand);
            PacifistRoute.LOGGER.info("MortarBlock onUse: isGrinding={}, handStack={}", mortar.isGrinding(), stack.isEmpty() ? "empty" : stack.getItem());
            if (!mortar.isGrinding()) {
                if (!stack.isEmpty() && mortar.canAddItem(stack)) {
                    mortar.addItem(stack);
                    if (!player.isCreative()) stack.decrement(1);
                    return ActionResult.SUCCESS;
                } else if (stack.isEmpty()) {
                    ItemStack extracted = mortar.extractItem();
                    if (!extracted.isEmpty()) {
                        if (!player.getInventory().insertStack(extracted)) {
                            player.dropItem(extracted, false);
                        }
                        PacifistRoute.LOGGER.info("Extracted {}", extracted.getItem());
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}