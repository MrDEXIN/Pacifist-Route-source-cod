package name.modid;

import name.modid.event.*;
import net.fabricmc.api.ModInitializer;
import name.modid.item.ModItems;
import name.modid.recipe.BreadRecipe;
import name.modid.recipe.SlimeRecipe;
import name.modid.recipe.SpiderEyeRecipe;
import name.modid.recipe.EnderPearlRecipe;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

public class PacifistRoute implements ModInitializer {

    @Override
    public void onInitialize() {
        // Регистрируем предметы
        ModItems.registerItems();
        PlayerRespawnHandler.register();

        // Регистрируем новые рецепты
        SpiderEyeRecipe.registerRecipes();
        SlimeRecipe.registerRecipes();
        BreadRecipe.registerRecipes();
        EnderPearlRecipe.registerRecipes();

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

        UseBlockCallback.EVENT.register(new TripwireInteractionHandler());

        ServerTickEvents.END_WORLD_TICK.register(TripwireInteractionHandler::tick);

        System.out.println("Мод Pacifist Route успешно загружен!");
    }
}
