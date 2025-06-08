package name.modid.block.custom;

import name.modid.effect.SulfurSmokeTracker;
import name.modid.particle.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SulfurBlock extends Block {

    private static final UniformIntProvider EXPERIENCE_PROVIDER = UniformIntProvider.create(2, 5);

    public SulfurBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (stateFrom.getBlock() == this) {
            return true;
        }
        return super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockPos abovePos = pos.up();
        if (!world.getBlockState(abovePos).isAir()) {
            return;
        }

        if (random.nextInt(2) == 0) {
            spawnVeryDenseSmoke(world, pos, random);
        }

        if (random.nextInt(3) == 0) {
            spawnExtraSmoke(world, pos, random);
        }

        if (random.nextInt(4) == 0) {
            spawnTinySmoke(world, pos, random);
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos abovePos = pos.up();
        if (world.getBlockState(abovePos).isAir()) {
            SulfurSmokeTracker.checkPlayersNearSulfurBlock(world, pos);
        }
    }

    @Override
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
        super.onStacksDropped(state, world, pos, tool, dropExperience);

        if (dropExperience && !EnchantmentHelper.get(tool).containsKey(Enchantments.SILK_TOUCH)) {
            this.dropExperience(world, pos, EXPERIENCE_PROVIDER.get(world.random));
        }
    }

    private void spawnVeryDenseSmoke(World world, BlockPos pos, Random random) {
        int smokeCount = 3 + random.nextInt(4);

        for (int i = 0; i < smokeCount; i++) {
            double x = (double) pos.getX() + 0.5 + random.nextGaussian() * 0.08;
            double y = (double) pos.getY() + 1.0;
            double z = (double) pos.getZ() + 0.5 + random.nextGaussian() * 0.08;

            double velocityX = random.nextGaussian() * 0.002;
            double velocityY = 0.025;
            double velocityZ = random.nextGaussian() * 0.002;

            world.addParticle(ModParticles.YELLOW_SMOKE, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    private void spawnExtraSmoke(World world, BlockPos pos, Random random) {
        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + 0.48 + random.nextDouble() * 0.04;
            double y = pos.getY() + 1.0 + random.nextDouble() * 0.02;
            double z = pos.getZ() + 0.48 + random.nextDouble() * 0.04;

            double velocityX = (random.nextDouble() - 0.5) * 0.001;
            double velocityY = 0.025;
            double velocityZ = (random.nextDouble() - 0.5) * 0.001;

            world.addParticle(ModParticles.YELLOW_SMOKE, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    private void spawnTinySmoke(World world, BlockPos pos, Random random) {
        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + 0.49 + random.nextDouble() * 0.02;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.49 + random.nextDouble() * 0.02;

            double velocityX = (random.nextDouble() - 0.5) * 0.0005;
            double velocityY = 0.025;
            double velocityZ = (random.nextDouble() - 0.5) * 0.0005;

            world.addParticle(ModParticles.YELLOW_SMOKE, x, y, z, velocityX, velocityY, velocityZ);
        }
    }
}