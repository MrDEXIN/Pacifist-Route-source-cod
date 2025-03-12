package name.modid.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item IRONFIGNA = new Item(new FabricItemSettings());
    public static final Item IRONFIGNA_BAGR = new Item(new FabricItemSettings());
    public static final Item DOUGH = new Item(new FabricItemSettings());
    public static final Item DIAMOND_IGNT = new Item(new FabricItemSettings());
    public static final Item HUMAN_SKIN = new Item(new FabricItemSettings());

    public static void registerItems() {
        Registry.register(Registries.ITEM, new Identifier("pacifist_route", "ironfigna"), IRONFIGNA);
        Registry.register(Registries.ITEM, new Identifier("pacifist_route", "ironfigna_bagr"), IRONFIGNA_BAGR);
        Registry.register(Registries.ITEM, new Identifier("pacifist_route", "dough"), DOUGH);
        Registry.register(Registries.ITEM, new Identifier("pacifist_route", "diamond_ignt"), DIAMOND_IGNT);
        Registry.register(Registries.ITEM, new Identifier("pacifist_route", "human_skin"), HUMAN_SKIN);
    }
}