package name.modid.network;

import name.modid.PacifistRoute;
import name.modid.block.entity.MortarBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class MortarGrindPacket {
    public static final Identifier ID = new Identifier(PacifistRoute.MOD_ID, "mortar_grind");

    public static void send(BlockPos pos, float delta, boolean forward) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeFloat(delta);
        buf.writeBoolean(forward);
        PacifistRoute.LOGGER.info("Sending grind packet: pos={}, delta={}, forward={}", pos, delta, forward);
        ClientPlayNetworking.send(ID, buf);
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            float delta = buf.readFloat();
            boolean forward = buf.readBoolean();
            PacifistRoute.LOGGER.info("Server received grind packet: pos={}, delta={}, forward={}", pos, delta, forward);
            server.execute(() -> {
                if (player.getWorld().getBlockEntity(pos) instanceof MortarBlockEntity mortar) {
                    PacifistRoute.LOGGER.info("Processing grind packet for mortar at {}", pos);
                    mortar.addGrindProgress(delta, forward);
                } else {
                    PacifistRoute.LOGGER.warn("No MortarBlockEntity found at {}", pos);
                }
            });
        });
    }
}