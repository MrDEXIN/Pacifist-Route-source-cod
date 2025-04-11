package name.modid.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import name.modid.effect.BleedingEffect;

public class ModItems {
    public static final Item IRONFIGNA = new Item(new FabricItemSettings());
    public static final Item IRONFIGNA_BAGR = new Item(new FabricItemSettings());
    public static final Item DOUGH = new Item(new FabricItemSettings());
    public static final Item DIAMOND_IGNT = new Item(new FabricItemSettings());
    public static final Item HUMAN_SKIN = new HumanSkinItem(new FabricItemSettings());
    public static final StatusEffect BLEEDING = new BleedingEffect();
    public static final Item RITUAL_KNIFE = new RitualKnifeItem();
    public static final Item SOUL_PRISON = new SoulPrisonItem(new FabricItemSettings().maxCount(1));
    public static final Item WITHER_SOUL = new WitherSoulItem(new FabricItemSettings().maxCount(1));
    public static final Item LIGHTSTONE_NERVE = new Item(new FabricItemSettings());

    public static void registerItems() {
        Registry.register(Registries.ITEM, new Identifier("pacifist_route", "ironfigna"), IRONFIGNA);
        Registry.register(Registries.ITEM, new Identifier("pacifist_route", "ironfigna_bagr"), IRONFIGNA_BAGR);
        Registry.register(Registries.ITEM, new Identifier("pacifist_route", "dough"), DOUGH);
        Registry.register(Registries.ITEM, new Identifier("pacifist_route", "diamond_ignt"), DIAMOND_IGNT);
        Registry.register(Registries.ITEM, new Identifier("pacifist_route", "human_skin"), HUMAN_SKIN);
        Registry.register(Registries.ITEM, new Identifier("pacifist_route", "ritual_knife"), RITUAL_KNIFE);
        Registry.register(Registries.ITEM, new Identifier("pacifist_route", "soul_prison"), SOUL_PRISON);
        Registry.register(Registries.ITEM, new Identifier("pacifist_route", "wither_soul"), WITHER_SOUL);
        Registry.register(Registries.ITEM, new Identifier("pacifist_route", "lightstone_nerve"), LIGHTSTONE_NERVE);
    }

    public static void registerEffects() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier("pacifist_route", "bleeding"), BLEEDING);
    }
}