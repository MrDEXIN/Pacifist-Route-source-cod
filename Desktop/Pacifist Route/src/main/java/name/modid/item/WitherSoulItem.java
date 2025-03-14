package name.modid.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class WitherSoulItem extends Item {
    public WitherSoulItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.pacifist_route.wither_soul.tooltip")
                .formatted(Formatting.ITALIC, Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }
}