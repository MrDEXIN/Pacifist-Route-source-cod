package name.modid.event;

import name.modid.PacifistRoute;
import name.modid.block.entity.MortarBlockEntity;
import name.modid.mixin.MixinMouse;
import name.modid.network.MortarGrindPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class MouseScrollHandler {
    private static double lastScrollValue = 0;
    private static final float MAX_SCROLL_DELTA = 0.05f;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null || client.currentScreen != null) return;
            if (client.crosshairTarget instanceof BlockHitResult hitResult) {
                BlockPos pos = hitResult.getBlockPos();
                if (client.world.getBlockEntity(pos) instanceof MortarBlockEntity mortar) {
                    handleMouseScroll(client, mortar, pos);
                }
            }
        });
    }

    private static void handleMouseScroll(MinecraftClient client, MortarBlockEntity mortar, BlockPos pos) {
        double currentScroll = ((MixinMouse) (Object) client.mouse).getEventDeltaWheel();
        double delta = currentScroll - lastScrollValue;
        lastScrollValue = currentScroll;
        if (delta != 0) {
            float scrollAmount = Math.min(Math.abs((float) delta) * 0.05f, MAX_SCROLL_DELTA);
            boolean forward = delta > 0;
            MortarGrindPacket.send(pos, scrollAmount, forward);
            PacifistRoute.LOGGER.info("Client scroll: delta={}, scrollAmount={}, forward={}", delta, scrollAmount, forward);
        }
    }
}