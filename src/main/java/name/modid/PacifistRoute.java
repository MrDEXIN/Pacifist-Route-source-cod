package name.modid;

import net.fabricmc.api.ModInitializer;
import name.modid.event.WoolInteractionHandler;
import name.modid.item.ModItems;
import name.modid.recipe.SlimeRecipe;
import name.modid.recipe.SpiderEyeRecipe;
import name.modid.recipe.WoolToStringRecipe;
import name.modid.recipe.EnderPearlRecipe;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

public class PacifistRoute implements ModInitializer {
    @Override
    public void onInitialize() {
        // Регистрируем предметы
        ModItems.registerItems();

        // Регистрируем новые рецепты
        SpiderEyeRecipe.registerRecipes();
        SlimeRecipe.registerRecipes();
        WoolToStringRecipe.registerRecipes();
        EnderPearlRecipe.registerRecipes();

        // Регистрируем группу предметов
        ModItemGroups.registerItemGroups();

        // Регистрируем обработчик взаимодействия с шерстью
        UseBlockCallback.EVENT.register(new WoolInteractionHandler());

        // Регистрируем удаление стандартного рецепта хлеба

        System.out.println("Мод Pacifist Route успешно загружен!");
    }
}