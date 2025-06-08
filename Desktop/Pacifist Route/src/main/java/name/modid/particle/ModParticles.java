package name.modid.particle;

import name.modid.PacifistRoute;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final DefaultParticleType YELLOW_SMOKE = FabricParticleTypes.simple();

    public static void registerParticles() {
        PacifistRoute.LOGGER.info("Registering particles for " + PacifistRoute.MOD_ID);

        Registry.register(Registries.PARTICLE_TYPE, new Identifier(PacifistRoute.MOD_ID, "yellow_smoke"), YELLOW_SMOKE);
    }
}