package name.modid.recipe;

import name.modid.item.ModItems; // Убедитесь, что этот импорт есть
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

public class SlimeRecipe extends SpecialCraftingRecipe {
    public SlimeRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        boolean hasDough = false;
        boolean hasDye = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() == ModItems.DOUGH) { // Убедитесь, что ModItems.DOUGH существует
                hasDough = true;
            } else if (stack.getItem() == Items.LIME_DYE) {
                hasDye = true;
            } else if (!stack.isEmpty()) {
                return false; // Другие предметы не допускаются
            }
        }

        return hasDough && hasDye;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        return new ItemStack(Items.SLIME_BALL, 1);
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2; // Минимум 2 слота
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SLIME_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
        return DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
    }

    public static final RecipeSerializer<SlimeRecipe> SLIME_RECIPE_SERIALIZER = new SpecialRecipeSerializer<>(SlimeRecipe::new);

    public static void registerRecipes() {
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("pacifist_route", "slime_recipe"), SLIME_RECIPE_SERIALIZER);
    }
}