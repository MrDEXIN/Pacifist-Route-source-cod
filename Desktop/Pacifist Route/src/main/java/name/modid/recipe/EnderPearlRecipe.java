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
import name.modid.item.ModItems; // Импортируем ваш предмет diamond_ignt

public class EnderPearlRecipe extends SpecialCraftingRecipe {

    public EnderPearlRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        // Проверяем, что ингредиенты расположены правильно
        return inventory.getStack(0).getItem() == ModItems.DIAMOND_IGNT && // Левый верхний угол
                inventory.getStack(1).getItem() == Items.WARPED_FUNGUS && // Верхний центр
                inventory.getStack(2).getItem() == ModItems.DIAMOND_IGNT && // Правый верхний угол
                inventory.getStack(3).getItem() == Items.TWISTING_VINES && // Левый центр
                inventory.getStack(4).getItem() == Items.SPIDER_EYE && // Центр
                inventory.getStack(5).getItem() == Items.TWISTING_VINES && // Правый центр
                inventory.getStack(6).getItem() == ModItems.DIAMOND_IGNT && // Левый нижний угол
                inventory.getStack(7).getItem() == Items.CYAN_DYE && // Нижний центр
                inventory.getStack(8).getItem() == ModItems.DIAMOND_IGNT; // Правый нижний угол
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        // Возвращаем жемчуг Эндера
        return new ItemStack(Items.ENDER_PEARL);
    }

    @Override
    public boolean fits(int width, int height) {
        // Рецепт работает в сетке 3x3
        return width * height >= 9;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ENDER_PEARL_RECIPE_SERIALIZER;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
        // Возвращаем пустой список, так как остатков нет
        return DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
    }

    // Сериализатор для рецепта
    public static final RecipeSerializer<EnderPearlRecipe> ENDER_PEARL_RECIPE_SERIALIZER = new RecipeSerializer<>() {
        @Override
        public EnderPearlRecipe read(Identifier id, JsonObject json) {
            return new EnderPearlRecipe(id, CraftingRecipeCategory.MISC);
        }

        @Override
        public EnderPearlRecipe read(Identifier id, PacketByteBuf buf) {
            return new EnderPearlRecipe(id, CraftingRecipeCategory.MISC);
        }

        @Override
        public void write(PacketByteBuf buf, EnderPearlRecipe recipe) {
            // Ничего не нужно записывать
        }
    };

    // Метод для регистрации рецепта
    public static void registerRecipes() {
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("pacifist_route", "ender_pearl_recipe"), ENDER_PEARL_RECIPE_SERIALIZER);
    }
}