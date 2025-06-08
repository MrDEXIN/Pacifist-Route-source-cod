package name.modid.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public class NitreGeodeFeatureConfig implements FeatureConfig {
    public static final Codec<NitreGeodeFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf("min_outer_wall_distance").forGetter(config -> config.minOuterWallDistance),
                    Codec.INT.fieldOf("max_outer_wall_distance").forGetter(config -> config.maxOuterWallDistance),
                    Codec.INT.fieldOf("min_distribution_points").forGetter(config -> config.minDistributionPoints),
                    Codec.INT.fieldOf("max_distribution_points").forGetter(config -> config.maxDistributionPoints)
            ).apply(instance, NitreGeodeFeatureConfig::new)
    );

    public final int minOuterWallDistance;
    public final int maxOuterWallDistance;
    public final int minDistributionPoints;
    public final int maxDistributionPoints;

    public NitreGeodeFeatureConfig(int minOuterWallDistance, int maxOuterWallDistance,
                                   int minDistributionPoints, int maxDistributionPoints) {
        this.minOuterWallDistance = minOuterWallDistance;
        this.maxOuterWallDistance = maxOuterWallDistance;
        this.minDistributionPoints = minDistributionPoints;
        this.maxDistributionPoints = maxDistributionPoints;
    }
}