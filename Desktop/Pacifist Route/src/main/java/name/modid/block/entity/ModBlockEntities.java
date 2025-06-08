package name.modid.block.entity;

import name.modid.PacifistRoute;
import name.modid.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<MortarBlockEntity> MORTAR_BLOCK_ENTITY =
            BlockEntityType.Builder.create(MortarBlockEntity::new, ModBlocks.MORTAR_BLOCK).build(null);

    public static void registerBlockEntities() {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(PacifistRoute.MOD_ID, "mortar_block_entity"), MORTAR_BLOCK_ENTITY);
    }
}