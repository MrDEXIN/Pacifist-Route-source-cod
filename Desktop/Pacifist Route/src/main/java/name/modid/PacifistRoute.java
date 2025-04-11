package name.modid;

import name.modid.item.HumanSkinItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import name.modid.event.*;
import net.fabricmc.api.ModInitializer;
import name.modid.item.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacifistRoute implements ModInitializer {
    public static final DefaultParticleType CROVE = Registry.register(Registries.PARTICLE_TYPE, new Identifier("pacifist_route", "crowe"), FabricParticleTypes.simple());
    public static final String MOD_ID = "pacifist_route";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // Регистрируем предметы
        ModItems.registerItems();
        ModItems.registerEffects();
        PlayerRespawnHandler.register();
        WoolInteractionHandler.registerBreakHandler();


        // Регистрируем группу предметов
        ModItemGroups.registerItemGroups();

        // Регистрируем обработчик взаимодействия с шерстью
        UseBlockCallback.EVENT.register(new WoolInteractionHandler());

        // Регистрируем обработчик взаимодействия с черепом скелета
        UseBlockCallback.EVENT.register(new SkeletonSkullInteractionHandler());

        // Регистрируем обработчик взаимодействия с головой игрока (превращение в пиглина)
        UseBlockCallback.EVENT.register(new PlayerHeadToPiglinHandler());

        // Регистрируем обработчик взаимодействия с головой зомби (превращение в крипера)
        UseBlockCallback.EVENT.register(new ZombieHeadToCreeperHandler());

        UseBlockCallback.EVENT.register(new PlayerHeadInteractionHandler());

        ServerTickEvents.END_WORLD_TICK.register(TripwireInteractionHandler::tick);
        // Обработка сундуков и инвентаря
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                ServerWorld world = player.getServerWorld();

                // Инвентарь игрока
                PlayerInventory inventory = player.getInventory();
                for (int i = 0; i < inventory.size(); i++) {
                    ItemStack stack = inventory.getStack(i);
                    if (stack.getItem() instanceof HumanSkinItem humanSkin) {
                        ItemStack transformed = humanSkin.checkAndTransform(stack, world);
                        if (transformed != stack) {
                            inventory.setStack(i, transformed);
                        }
                    }
                }

                // Сундуки вокруг игрока
                BlockPos playerPos = player.getBlockPos();
                int radius = 16;
                Box area = new Box(playerPos).expand(radius);
                for (BlockPos pos : BlockPos.iterate(
                        (int) area.minX, (int) area.minY, (int) area.minZ,
                        (int) area.maxX, (int) area.maxY, (int) area.maxZ)) {
                    if (world.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
                        for (int i = 0; i < chest.size(); i++) {
                            ItemStack stack = chest.getStack(i);
                            if (stack.getItem() instanceof HumanSkinItem humanSkin) {
                                ItemStack transformed = humanSkin.checkAndTransform(stack, world);
                                if (transformed != stack) {
                                    chest.setStack(i, transformed);
                                }
                            }
                        }
                    }
                }
            }
        });


        System.out.println("Мод Pacifist Route успешно загружен!");
    }
}