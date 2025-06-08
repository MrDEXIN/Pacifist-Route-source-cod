package name.modid;

import name.modid.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import name.modid.item.ModItems; // Импортируем предметы

public class ModItemGroups {
    public static final ItemGroup PEACEFUL_SURVIVAL_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.IRONFIGNA_BAGR)) // Иконка группы (используем первый предмет)
            .displayName(Text.translatable("itemGroup.pacifist_route.group")) // Название группы
            .entries((context, entries) -> {
                entries.add(ModItems.IRONFIGNA);
                entries.add(ModItems.IRONFIGNA_BAGR);
                entries.add(ModItems.DIAMOND_IGNT);
                entries.add(ModItems.DOUGH);
                entries.add(ModItems.SULFUR);
                entries.add(ModItems.SALTPETER);
                entries.add(new ItemStack(ModBlocks.MORTAR_BLOCK));
            })
            .build();

    public static final ItemGroup MYSTICAL_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.RITUAL_KNIFE))
            .displayName(Text.translatable("itemGroup.pacifist_route.group_ritual"))
            .entries((context, entries) -> {
                entries.add(ModItems.RITUAL_KNIFE);
                entries.add(ModItems.HUMAN_SKIN);
                entries.add(ModItems.LIGHTSTONE_NERVE);
                entries.add(ModItems.SOUL_PRISON);
                entries.add(ModItems.WITHER_SOUL);
            })
            .build();
    public static final ItemGroup ORES = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.SULFUR_IN_NETHERRACK))
            .displayName(Text.translatable("itemGroup.pacifist_route.ore_group")) // Название группы
            .entries((context, entries) -> {
                entries.add(ModItems.NITRE_SHARD);
                entries.add(new ItemStack(ModBlocks.NITRE_CRYSTAL_BLOCK));
                entries.add(new ItemStack(ModBlocks.BUDDING_NITRE));
                entries.add(new ItemStack(ModBlocks.TINY_NITRE_BUD));
                entries.add(new ItemStack(ModBlocks.SMALL_NITRE_BUD));
                entries.add(new ItemStack(ModBlocks.MEDIUM_NITRE_BUD));
                entries.add(new ItemStack(ModBlocks.LARGE_NITRE_BUD));
                entries.add(new ItemStack(ModBlocks.NITRE_CLUSTER));

                entries.add(ModItems.STONE_CRYSTAL_SULFUR);
                entries.add(ModItems.DEEP_CRYSTAL_SULFUR);
                entries.add(ModItems.NETHER_CRYSTAL_SULFUR);
                entries.add(new ItemStack(ModBlocks.SULFUR_IN_STONE));
                entries.add(new ItemStack(ModBlocks.SULFUR_IN_DEEP));
                entries.add(new ItemStack(ModBlocks.SULFUR_IN_NETHERRACK));
            })
            .build();

    public static void registerItemGroups() {
        Registry.register(Registries.ITEM_GROUP, new Identifier("pacifist_route", "peaceful"), PEACEFUL_SURVIVAL_GROUP);
        Registry.register(Registries.ITEM_GROUP, new Identifier("pacifist_route", "group_ritual"), MYSTICAL_GROUP);
        Registry.register(Registries.ITEM_GROUP, new Identifier("pacifist_route", "ore_group"), ORES);
    }
}