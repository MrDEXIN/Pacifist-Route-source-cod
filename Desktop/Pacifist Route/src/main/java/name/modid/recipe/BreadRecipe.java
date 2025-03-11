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

public class BreadRecipe extends SpecialCraftingRecipe {
    public BreadRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        boolean hasWheat = false;
        boolean hasWaterBucket = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() == Items.WHEAT) {
                hasWheat = true;
            } else if (stack.getItem() == Items.WATER_BUCKET) {
                hasWaterBucket = true;
            } else if (!stack.isEmpty()) {
                return false; // Другие предметы не допускаются
            }
        }

        return hasWheat && hasWaterBucket;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        return new ItemStack(Items.BREAD, 1);
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2; // Минимум 2 слота
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BREAD_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
        DefaultedList<ItemStack> remainders = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() == Items.WATER_BUCKET) {
                remainders.set(i, new ItemStack(Items.BUCKET)); // Возвращаем пустое ведро
            }
        }

        return remainders;
    }

    public static final RecipeSerializer<BreadRecipe> BREAD_RECIPE_SERIALIZER = new SpecialRecipeSerializer<>(BreadRecipe::new);

    public static void registerRecipes() {
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("pacifist_route", "bread_recipe"), BREAD_RECIPE_SERIALIZER);
    }
}