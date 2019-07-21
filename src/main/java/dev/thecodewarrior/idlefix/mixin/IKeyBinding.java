package dev.thecodewarrior.idlefix.mixin;

import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(KeyBinding.class)
public interface IKeyBinding {
    @Accessor("keysById")
    static Map<String, KeyBinding> getKeysById() {
        throw new UnsupportedOperationException("Mixin failed");
    }

    @Accessor("pressed")
    boolean isPressed();

    @Accessor("pressed")
    void setPressed(boolean pressed);

    @Accessor("timesPressed")
    int getTimesPressed();

    @Accessor("timesPressed")
    void setTimesPressed(int timesPressed);
}
