package name.modid.recipe;

import net.minecraft.network.PacketByteBuf;
import com.google.gson.JsonObject;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class WoolToStringRecipe extends SpecialCraftingRecipe {

    public WoolToStringRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        // Проверяем, что в сетке крафта есть 1 блок шерсти
        boolean hasWool = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() == Items.WHITE_WOOL ||
                    stack.getItem() == Items.BLACK_WOOL ||
                    stack.getItem() == Items.BLUE_WOOL ||
                    stack.getItem() == Items.BROWN_WOOL ||
                    stack.getItem() == Items.CYAN_WOOL ||
                    stack.getItem() == Items.GRAY_WOOL ||
                    stack.getItem() == Items.GREEN_WOOL ||
                    stack.getItem() == Items.LIGHT_BLUE_WOOL ||
                    stack.getItem() == Items.LIGHT_GRAY_WOOL ||
                    stack.getItem() == Items.LIME_WOOL ||
                    stack.getItem() == Items.MAGENTA_WOOL ||
                    stack.getItem() == Items.ORANGE_WOOL ||
                    stack.getItem() == Items.PINK_WOOL ||
                    stack.getItem() == Items.PURPLE_WOOL ||
                    stack.getItem() == Items.RED_WOOL ||
                    stack.getItem() == Items.YELLOW_WOOL) {
                hasWool = true;
            } else if (!stack.isEmpty()) {
                return false; // Другие предметы не допускаются
            }
        }

        return hasWool;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        // Возвращаем 4 нитки
        return new ItemStack(Items.STRING, 4);
    }

    @Override
    public boolean fits(int width, int height) {
        // Рецепт работает в сетке 2x2 или больше
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return WOOL_TO_STRING_RECIPE_SERIALIZER;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
        // Возвращаем пустой список, так как остатков нет
        return DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
    }

    // Сериализатор для рецепта
    public static final RecipeSerializer<WoolToStringRecipe> WOOL_TO_STRING_RECIPE_SERIALIZER = new RecipeSerializer<>() {
        @Override
        public WoolToStringRecipe read(Identifier id, JsonObject json) {
            return new WoolToStringRecipe(id, CraftingRecipeCategory.MISC);
        }

        @Override
        public WoolToStringRecipe read(Identifier id, PacketByteBuf buf) {
            return new WoolToStringRecipe(id, CraftingRecipeCategory.MISC);
        }

        @Override
        public void write(PacketByteBuf buf, WoolToStringRecipe recipe) {
            // Ничего не нужно записывать
        }
    };

    // Метод для регистрации рецепта
    public static void registerRecipes() {
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("pacifist_route", "wool_to_string"), WOOL_TO_STRING_RECIPE_SERIALIZER);
    }
}