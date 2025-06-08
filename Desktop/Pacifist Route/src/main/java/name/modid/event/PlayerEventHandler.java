package name.modid.event;

import name.modid.effect.SulfurSmokeTracker;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class PlayerEventHandler {

    public static void registerPlayerEvents() {
        ServerPlayConnectionEvents.DISCONNECT.register(PlayerEventHandler::onPlayerDisconnect);
    }

    private static void onPlayerDisconnect(ServerPlayNetworkHandler handler, net.minecraft.server.MinecraftServer server) {
        SulfurSmokeTracker.clearPlayerData(handler.getPlayer().getUuid());
    }
}