package name.modid.block;

import name.modid.PacifistRoute;
import name.modid.block.custom.SulfurBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class ModBlocks {
    private static final VoxelShape TINY_SHAPE_WEST = VoxelShapes.cuboid(0.375, 0.875, 0.375, 0.625, 1.0, 0.625);
    private static final VoxelShape TINY_SHAPE_EAST = VoxelShapes.cuboid(0.375, 0.0, 0.375, 0.625, 0.125, 0.625);
    private static final VoxelShape TINY_SHAPE_DOWN = VoxelShapes.cuboid(0.375, 0.375, 0.875, 0.625, 0.625, 1.0);
    private static final VoxelShape TINY_SHAPE_UP = VoxelShapes.cuboid(0.375, 0.375, 0.0, 0.625, 0.625, 0.125);
    private static final VoxelShape TINY_SHAPE_NORTH = VoxelShapes.cuboid(0.875, 0.375, 0.375, 1.0, 0.625, 0.625);
    private static final VoxelShape TINY_SHAPE_SOUTH = VoxelShapes.cuboid(0.0, 0.375, 0.375, 0.125, 0.625, 0.625);

    private static final VoxelShape SMALL_SHAPE_WEST = VoxelShapes.cuboid(0.40625, 0.75, 0.40625, 0.59375, 1.0, 0.59375);
    private static final VoxelShape SMALL_SHAPE_EAST = VoxelShapes.cuboid(0.40625, 0.0, 0.40625, 0.59375, 0.25, 0.59375);
    private static final VoxelShape SMALL_SHAPE_DOWN = VoxelShapes.cuboid(0.40625, 0.40625, 0.75, 0.59375, 0.59375, 1.0);
    private static final VoxelShape SMALL_SHAPE_UP = VoxelShapes.cuboid(0.40625, 0.40625, 0.0, 0.59375, 0.59375, 0.25);
    private static final VoxelShape SMALL_SHAPE_NORTH = VoxelShapes.cuboid(0.75, 0.40625, 0.40625, 1.0, 0.59375, 0.59375);
    private static final VoxelShape SMALL_SHAPE_SOUTH = VoxelShapes.cuboid(0.0, 0.40625, 0.40625, 0.25, 0.59375, 0.59375);

    private static final VoxelShape MEDIUM_SHAPE_WEST = VoxelShapes.cuboid(0.40625, 0.3125, 0.40625, 0.59375, 1.0, 0.59375);
    private static final VoxelShape MEDIUM_SHAPE_EAST = VoxelShapes.cuboid(0.40625, 0.0, 0.40625, 0.59375, 0.6875, 0.59375);
    private static final VoxelShape MEDIUM_SHAPE_DOWN = VoxelShapes.cuboid(0.40625, 0.40625, 0.3125, 0.59375, 0.59375, 1.0);
    private static final VoxelShape MEDIUM_SHAPE_UP = VoxelShapes.cuboid(0.40625, 0.40625, 0.0, 0.59375, 0.59375, 0.6875);
    private static final VoxelShape MEDIUM_SHAPE_NORTH = VoxelShapes.cuboid(0.3125, 0.40625, 0.40625, 1.0, 0.59375, 0.59375);
    private static final VoxelShape MEDIUM_SHAPE_SOUTH = VoxelShapes.cuboid(0.0, 0.40625, 0.40625, 0.6875, 0.59375, 0.59375);

    private static final VoxelShape LARGE_SHAPE_WEST = VoxelShapes.cuboid(0.375, 0.0625, 0.375, 0.625, 1.0, 0.625);
    private static final VoxelShape LARGE_SHAPE_EAST = VoxelShapes.cuboid(0.375, 0.0, 0.375, 0.625, 0.9375, 0.625);
    private static final VoxelShape LARGE_SHAPE_DOWN = VoxelShapes.cuboid(0.375, 0.375, 0.0625, 0.625, 0.625, 1.0);
    private static final VoxelShape LARGE_SHAPE_UP = VoxelShapes.cuboid(0.375, 0.375, 0.0, 0.625, 0.625, 0.9375);
    private static final VoxelShape LARGE_SHAPE_NORTH = VoxelShapes.cuboid(0.0625, 0.375, 0.375, 1.0, 0.625, 0.625);
    private static final VoxelShape LARGE_SHAPE_SOUTH = VoxelShapes.cuboid(0.0, 0.375, 0.375, 0.9375, 0.625, 0.625);

    private static final VoxelShape CLUSTER_SHAPE_WEST = VoxelShapes.cuboid(0.34375, 0.0, 0.34375, 0.65625, 1.27, 0.65625);
    private static final VoxelShape CLUSTER_SHAPE_EAST = VoxelShapes.cuboid(0.34375, 0.0, 0.34375, 0.65625, 1.27, 0.65625);
    private static final VoxelShape CLUSTER_SHAPE_DOWN = VoxelShapes.cuboid(0.34375, 0.34375, 0.0, 0.65625, 0.65625, 1.27);
    private static final VoxelShape CLUSTER_SHAPE_UP = VoxelShapes.cuboid(0.34375, 0.34375, 0.0, 0.65625, 0.65625, 1.27);
    private static final VoxelShape CLUSTER_SHAPE_NORTH = VoxelShapes.cuboid(0.0, 0.34375, 0.34375, 1.27, 0.65625, 0.65625);
    private static final VoxelShape CLUSTER_SHAPE_SOUTH = VoxelShapes.cuboid(0.0, 0.34375, 0.34375, 1.27, 0.65625, 0.65625);



    // Простые настройки блоков
    public static final Block NITRE_CRYSTAL_BLOCK = new name.modid.block.custom.NitreCrystalBlock(
            FabricBlockSettings.create()
                    .strength(1.5F)
                    .requiresTool()
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                    .nonOpaque().requiresTool()
    );

    public static final Block BUDDING_NITRE = new BuddingNitreBlock(
            FabricBlockSettings.create()
                    .ticksRandomly()
                    .strength(1.5F)
                    .requiresTool()
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                    .nonOpaque().requiresTool()
    );

    public static final Block TINY_NITRE_BUD = new NitreBudBlock(
            FabricBlockSettings.create()
                    .strength(1.5F)
                    .requiresTool()
                    .sounds(BlockSoundGroup.AMETHYST_CLUSTER)
                    .nonOpaque().requiresTool(),
            new VoxelShape[]{TINY_SHAPE_WEST, TINY_SHAPE_EAST, TINY_SHAPE_DOWN, TINY_SHAPE_UP, TINY_SHAPE_NORTH, TINY_SHAPE_SOUTH}
    );

    public static final Block SMALL_NITRE_BUD = new NitreBudBlock(
            FabricBlockSettings.create()
                    .strength(1.5F)
                    .requiresTool()
                    .sounds(BlockSoundGroup.AMETHYST_CLUSTER)
                    .nonOpaque().requiresTool(),
            new VoxelShape[]{SMALL_SHAPE_WEST, SMALL_SHAPE_EAST, SMALL_SHAPE_DOWN, SMALL_SHAPE_UP, SMALL_SHAPE_NORTH, SMALL_SHAPE_SOUTH}
    );

    public static final Block MEDIUM_NITRE_BUD = new NitreBudBlock(
            FabricBlockSettings.create()
                    .strength(1.5F)
                    .requiresTool()
                    .sounds(BlockSoundGroup.AMETHYST_CLUSTER)
                    .nonOpaque().requiresTool(),
            new VoxelShape[]{MEDIUM_SHAPE_WEST, MEDIUM_SHAPE_EAST, MEDIUM_SHAPE_DOWN, MEDIUM_SHAPE_UP, MEDIUM_SHAPE_NORTH, MEDIUM_SHAPE_SOUTH}
    );

    public static final Block LARGE_NITRE_BUD = new NitreBudBlock(
            FabricBlockSettings.create()
                    .strength(1.5F)
                    .requiresTool()
                    .sounds(BlockSoundGroup.AMETHYST_CLUSTER)
                    .nonOpaque().requiresTool().luminance(4),
            new VoxelShape[]{LARGE_SHAPE_WEST, LARGE_SHAPE_EAST, LARGE_SHAPE_DOWN, LARGE_SHAPE_UP, LARGE_SHAPE_NORTH, LARGE_SHAPE_SOUTH}
    );

    public static final Block NITRE_CLUSTER = new NitreBudBlock(
            FabricBlockSettings.create()
                    .strength(1.5F)
                    .requiresTool()
                    .sounds(BlockSoundGroup.AMETHYST_CLUSTER)
                    .nonOpaque().requiresTool().luminance(6),
            new VoxelShape[]{CLUSTER_SHAPE_WEST, CLUSTER_SHAPE_EAST, CLUSTER_SHAPE_DOWN, CLUSTER_SHAPE_UP, CLUSTER_SHAPE_NORTH, CLUSTER_SHAPE_SOUTH}
    );
    public static final Block SULFUR_IN_NETHERRACK = new SulfurBlock(
            FabricBlockSettings.create()
                    .strength(3F)
                    .requiresTool().nonOpaque()
                    .sounds(BlockSoundGroup.NETHERRACK).allowsSpawning(Blocks::never)
                    .solidBlock(Blocks::never)
                    .suffocates(Blocks::never)
                    .blockVision(Blocks::never));
    public static final Block SULFUR_IN_STONE = new SulfurBlock(
            FabricBlockSettings.create()
                    .strength(3.5F)
                    .requiresTool().nonOpaque()
                    .sounds(BlockSoundGroup.STONE).allowsSpawning(Blocks::never)
                    .solidBlock(Blocks::never)
                    .suffocates(Blocks::never)
                    .blockVision(Blocks::never));
    public static final Block SULFUR_IN_DEEP = new SulfurBlock(
            FabricBlockSettings.create()
                    .strength(5F)
                    .requiresTool().nonOpaque()
                    .sounds(BlockSoundGroup.DEEPSLATE).allowsSpawning(Blocks::never)
                    .solidBlock(Blocks::never)
                    .suffocates(Blocks::never)
                    .blockVision(Blocks::never));
    public static final Block MORTAR_BLOCK = new MortarBlock(
            FabricBlockSettings.create()
                    .strength(2.0f, 3.0f)
                    .sounds(BlockSoundGroup.WOOD)
                    .burnable());

    public static void registerBlocks() {
        Registry.register(Registries.BLOCK, new Identifier(PacifistRoute.MOD_ID, "mortar"), MORTAR_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier(PacifistRoute.MOD_ID, "nitre_crystal_block"), NITRE_CRYSTAL_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier(PacifistRoute.MOD_ID, "budding_nitre"), BUDDING_NITRE);
        Registry.register(Registries.BLOCK, new Identifier(PacifistRoute.MOD_ID, "tiny_nitre_bud"), TINY_NITRE_BUD);
        Registry.register(Registries.BLOCK, new Identifier(PacifistRoute.MOD_ID, "small_nitre_bud"), SMALL_NITRE_BUD);
        Registry.register(Registries.BLOCK, new Identifier(PacifistRoute.MOD_ID, "medium_nitre_bud"), MEDIUM_NITRE_BUD);
        Registry.register(Registries.BLOCK, new Identifier(PacifistRoute.MOD_ID, "large_nitre_bud"), LARGE_NITRE_BUD);
        Registry.register(Registries.BLOCK, new Identifier(PacifistRoute.MOD_ID, "nitre_cluster"), NITRE_CLUSTER);
        Registry.register(Registries.BLOCK, new Identifier(PacifistRoute.MOD_ID, "sulfur_in_netherrack"), SULFUR_IN_NETHERRACK);
        Registry.register(Registries.BLOCK, new Identifier(PacifistRoute.MOD_ID, "sulfur_in_stone"), SULFUR_IN_STONE);
        Registry.register(Registries.BLOCK, new Identifier(PacifistRoute.MOD_ID, "sulfur_in_deep"), SULFUR_IN_DEEP);

        Registry.register(Registries.ITEM, new Identifier(PacifistRoute.MOD_ID, "nitre_crystal_block"), new BlockItem(NITRE_CRYSTAL_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(PacifistRoute.MOD_ID, "budding_nitre"), new BlockItem(BUDDING_NITRE, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(PacifistRoute.MOD_ID, "tiny_nitre_bud"), new BlockItem(TINY_NITRE_BUD, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(PacifistRoute.MOD_ID, "small_nitre_bud"), new BlockItem(SMALL_NITRE_BUD, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(PacifistRoute.MOD_ID, "medium_nitre_bud"), new BlockItem(MEDIUM_NITRE_BUD, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(PacifistRoute.MOD_ID, "large_nitre_bud"), new BlockItem(LARGE_NITRE_BUD, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(PacifistRoute.MOD_ID, "nitre_cluster"), new BlockItem(NITRE_CLUSTER, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(PacifistRoute.MOD_ID, "sulfur_in_netherrack"), new BlockItem(SULFUR_IN_NETHERRACK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(PacifistRoute.MOD_ID, "sulfur_in_stone"), new BlockItem(SULFUR_IN_STONE, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(PacifistRoute.MOD_ID, "sulfur_in_deep"), new BlockItem(SULFUR_IN_DEEP, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(PacifistRoute.MOD_ID, "mortar"), new BlockItem(MORTAR_BLOCK, new FabricItemSettings()));
    }
}