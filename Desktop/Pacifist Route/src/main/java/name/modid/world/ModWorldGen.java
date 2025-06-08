package name.modid.world;

import name.modid.PacifistRoute;
import name.modid.world.feature.ModFeatures;
import name.modid.world.feature.NetherSulfurFeature;
import name.modid.world.feature.SulfurOreFeature;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;

public class ModWorldGen {

    public static final RegistryKey<PlacedFeature> SULFUR_ORE_PLACED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(PacifistRoute.MOD_ID, "sulfur_ore"));

    public static final RegistryKey<PlacedFeature> NETHER_SULFUR_PLACED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(PacifistRoute.MOD_ID, "nether_sulfur"));


    public static final Feature<DefaultFeatureConfig> SULFUR_ORE_FEATURE =
            new SulfurOreFeature(DefaultFeatureConfig.CODEC);

    public static final Feature<DefaultFeatureConfig> NETHER_SULFUR_FEATURE =
            new NetherSulfurFeature(DefaultFeatureConfig.CODEC);

    public static void registerWorldGen() {
        PacifistRoute.LOGGER.info("Registering features for " + PacifistRoute.MOD_ID);


        ModFeatures.registerFeatures();

        registerSulfurFeatures();
    }

    private static void registerSulfurFeatures() {
        PacifistRoute.LOGGER.info("Registering sulfur ore features");

        Registry.register(Registries.FEATURE, new Identifier(PacifistRoute.MOD_ID, "sulfur_ore"), SULFUR_ORE_FEATURE);
        Registry.register(Registries.FEATURE, new Identifier(PacifistRoute.MOD_ID, "nether_sulfur"), NETHER_SULFUR_FEATURE);

        PacifistRoute.LOGGER.info("Sulfur ore features registered successfully");
    }

    public static void addBiomeModifications() {
        PacifistRoute.LOGGER.info("Adding biome modifications");

        RegistryKey<PlacedFeature> nitreGeodePlaced =
                RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(PacifistRoute.MOD_ID, "nitre_geode_placed"));

        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(
                        BiomeKeys.DESERT,
                        BiomeKeys.SAVANNA,
                        BiomeKeys.SAVANNA_PLATEAU,
                        BiomeKeys.WINDSWEPT_SAVANNA,
                        BiomeKeys.BADLANDS,
                        BiomeKeys.ERODED_BADLANDS,
                        BiomeKeys.WOODED_BADLANDS
                ),
                GenerationStep.Feature.UNDERGROUND_ORES,
                nitreGeodePlaced
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                SULFUR_ORE_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInTheNether(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                NETHER_SULFUR_PLACED_KEY
        );

        PacifistRoute.LOGGER.info("Biome modifications added successfully (nitre geodes only in desert/savanna/mesa, sulfur ores everywhere)");
    }
}