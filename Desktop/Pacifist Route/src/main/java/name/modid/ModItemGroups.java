package name.modid;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import name.modid.item.ModItems; // Импортируем предметы

public class ModItemGroups {
    // Создаём группу предметов
    public static final ItemGroup PEACEFUL_SURVIVAL_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.IRONFIGNA)) // Иконка группы (используем первый предмет)
            .displayName(Text.translatable("itemGroup.pacifist_route.group")) // Название группы
            .entries((context, entries) -> {
                // Добавляем предметы в группу
                entries.add(ModItems.IRONFIGNA);
                entries.add(ModItems.IRONFIGNA_BAGR);
                entries.add(ModItems.DIAMOND_IGNT);
                entries.add(ModItems.DOUGH);
                entries.add(ModItems.HUMAN_SKIN);
            })
            .build();

    // Метод для регистрации группы
    public static void registerItemGroups() {
        Registry.register(Registries.ITEM_GROUP, new Identifier("pacifist_route", "group"), PEACEFUL_SURVIVAL_GROUP);
    }
}