package name.modid.client.model;

import name.modid.block.entity.MortarBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class MortarBlockEntityModel extends GeoModel<MortarBlockEntity> {
    @Override
    public Identifier getModelResource(MortarBlockEntity animatable) {
        return new Identifier("pacifist_route", "geo/mortal.geo.json");
    }

    @Override
    public Identifier getTextureResource(MortarBlockEntity animatable) {
        return new Identifier("pacifist_route", "textures/block/mortal.png");
    }

    @Override
    public Identifier getAnimationResource(MortarBlockEntity animatable) {
        return new Identifier("pacifist_route", "animations/model.animation.json");
    }
}