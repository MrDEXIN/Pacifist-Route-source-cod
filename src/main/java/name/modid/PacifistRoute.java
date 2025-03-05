package name.modid;

import net.fabricmc.api.ModInitializer;
import name.modid.item.ModItems;
import name.modid.recipe.BreadRecipe;
import name.modid.recipe.SlimeRecipe;
import name.modid.recipe.SpiderEyeRecipe;
import name.modid.recipe.StringRecipe;
import name.modid.recipe.WoolToStringRecipe;
import name.modid.recipe.EnderPearlRecipe;

public class PacifistRoute implements ModInitializer {
    @Override
    public void onInitialize() {
        // Регистрируем предметы
        ModItems.registerItems();

        // Регистрируем новые рецепты
        SpiderEyeRecipe.registerRecipes();
        StringRecipe.registerRecipes();
        SlimeRecipe.registerRecipes();
        BreadRecipe.registerRecipes();
        WoolToStringRecipe.registerRecipes();
        EnderPearlRecipe.registerRecipes();

        // Регистрируем группу предметов
        ModItemGroups.registerItemGroups();

        System.out.println("Мод Pacifist Route успешно загружен!");
    }
}