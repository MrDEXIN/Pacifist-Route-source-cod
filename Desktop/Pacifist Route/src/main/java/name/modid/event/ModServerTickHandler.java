// Файл: ModServerTickHandler.java
package name.modid.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ModServerTickHandler {
    public static void register() {
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            TripwireInteractionHandler.tick(world);
        });
    }
}