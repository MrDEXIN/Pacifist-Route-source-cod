package name.modid.world.feature;

import name.modid.PacifistRoute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;

public class ModFeatures {

    public static final Feature<NitreGeodeFeatureConfig> NITRE_GEODE_FEATURE =
            Registry.register(Registries.FEATURE,
                    new Identifier(PacifistRoute.MOD_ID, "nitre_geode"),
                    new NitreGeodeFeature(NitreGeodeFeatureConfig.CODEC));

    public static void registerFeatures() {
        PacifistRoute.LOGGER.info("Registering features for " + PacifistRoute.MOD_ID);
    }
}