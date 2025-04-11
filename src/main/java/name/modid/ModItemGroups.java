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
            .icon(() -> new ItemStack(ModItems.IRONFIGNA_BAGR)) // Иконка группы (используем первый предмет)
            .displayName(Text.translatable("itemGroup.pacifist_route.group")) // Название группы
            .entries((context, entries) -> {
                // Добавляем предметы в группу
                entries.add(ModItems.IRONFIGNA);
                entries.add(ModItems.IRONFIGNA_BAGR);
                entries.add(ModItems.DIAMOND_IGNT);
                entries.add(ModItems.DOUGH);
            })
            .build();

    public static final ItemGroup MYSTICAL_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.RITUAL_KNIFE)) // Иконка группы
            .displayName(Text.translatable("itemGroup.pacifist_route.group_ritual"))
            .entries((context, entries) -> {
                entries.add(ModItems.RITUAL_KNIFE);
                entries.add(ModItems.HUMAN_SKIN);
                entries.add(ModItems.LIGHTSTONE_NERVE);
                entries.add(ModItems.SOUL_PRISON);
                entries.add(ModItems.WITHER_SOUL);
            })
            .build();

    // Регистрация всех групп
    public static void registerItemGroups() {
        Registry.register(Registries.ITEM_GROUP, new Identifier("pacifist_route", "peaceful"), PEACEFUL_SURVIVAL_GROUP);
        Registry.register(Registries.ITEM_GROUP, new Identifier("pacifist_route", "group_ritual"), MYSTICAL_GROUP); // Добавьте эту строку
    }
}