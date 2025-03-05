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

public class StringRecipe extends SpecialCraftingRecipe {
    public StringRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        boolean hasWool = false;
        boolean hasShears = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() == Items.WHITE_WOOL || stack.getItem() == Items.BLACK_WOOL || stack.getItem() == Items.BLUE_WOOL || stack.getItem() == Items.BROWN_WOOL || stack.getItem() == Items.CYAN_WOOL || stack.getItem() == Items.GRAY_WOOL || stack.getItem() == Items.GREEN_WOOL || stack.getItem() == Items.LIGHT_BLUE_WOOL || stack.getItem() == Items.LIGHT_GRAY_WOOL || stack.getItem() == Items.LIME_WOOL || stack.getItem() == Items.MAGENTA_WOOL || stack.getItem() == Items.ORANGE_WOOL || stack.getItem() == Items.PINK_WOOL || stack.getItem() == Items.PURPLE_WOOL || stack.getItem() == Items.RED_WOOL || stack.getItem() == Items.YELLOW_WOOL) {
                hasWool = true;
            } else if (stack.getItem() == Items.SHEARS) {
                hasShears = true;
            } else if (!stack.isEmpty()) {
                return false; // Другие предметы не допускаются
            }
        }

        return hasWool && hasShears;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        return new ItemStack(Items.STRING, 4);
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2; // Минимум 2 слота
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return STRING_RECIPE_SERIALIZER;
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
            if (stack.getItem() == Items.SHEARS) {
                ItemStack shears = stack.copy();
                shears.setDamage(shears.getDamage() + 1); // Уменьшаем прочность ножниц на 1
                if (shears.getDamage() < shears.getMaxDamage()) {
                    remainders.set(i, shears);
                }
            }
        }

        return remainders;
    }

    public static final RecipeSerializer<StringRecipe> STRING_RECIPE_SERIALIZER = new SpecialRecipeSerializer<>(StringRecipe::new);

    public static void registerRecipes() {
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("pacifist_route", "string_recipe"), STRING_RECIPE_SERIALIZER);
    }
}