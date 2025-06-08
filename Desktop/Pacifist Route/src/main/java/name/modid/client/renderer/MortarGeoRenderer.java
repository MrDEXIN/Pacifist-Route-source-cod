package name.modid.client.renderer;

import name.modid.block.entity.MortarBlockEntity;
import name.modid.client.model.MortarBlockEntityModel;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MortarGeoRenderer extends GeoBlockRenderer<MortarBlockEntity> {
    public MortarGeoRenderer(BlockEntityRendererFactory.Context ctx) {
        super(new MortarBlockEntityModel());
    }
}