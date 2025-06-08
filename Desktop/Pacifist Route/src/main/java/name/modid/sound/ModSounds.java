package name.modid.sound;

import name.modid.PacifistRoute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent MORTAR_GRIND_1 = registerSoundEvent("mortar_grind_1");
    public static final SoundEvent MORTAR_GRIND_2 = registerSoundEvent("mortar_grind_2");
    public static final SoundEvent MORTAR_GRIND_3 = registerSoundEvent("mortar_grind_3");

    public static final SoundEvent MORTAR_COMPLETE = registerSoundEvent("mortar_complete");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(PacifistRoute.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        PacifistRoute.LOGGER.info("Registering sounds for " + PacifistRoute.MOD_ID);
    }
}