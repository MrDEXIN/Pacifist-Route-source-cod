package name.modid.recipe;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class SpiderEyeRecipe extends SpecialCraftingRecipe {
    public SpiderEyeRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        boolean hasString = false;
        boolean hasBerry = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() == Items.STRING) {
                hasString = true;
            } else if (stack.getItem() == Items.SWEET_BERRIES) {
                hasBerry = true;
            } else if (!stack.isEmpty()) {
                return false; // Другие предметы не допускаются
            }
        }

        return hasString && hasBerry;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        return new ItemStack(Items.SPIDER_EYE, 1);
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2; // Минимум 2 слота
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SPIDER_EYE_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
        return DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
    }

    public static final RecipeSerializer<SpiderEyeRecipe> SPIDER_EYE_RECIPE_SERIALIZER = new SpecialRecipeSerializer<>(SpiderEyeRecipe::new);

    public static void registerRecipes() {
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("pacifist_route", "spider_eye_recipe"), SPIDER_EYE_RECIPE_SERIALIZER);
    }
}