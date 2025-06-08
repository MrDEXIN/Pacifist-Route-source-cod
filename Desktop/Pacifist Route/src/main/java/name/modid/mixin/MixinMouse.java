package name.modid.mixin;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mouse.class)
public interface MixinMouse {
    @Accessor("eventDeltaWheel")
    double getEventDeltaWheel();
}