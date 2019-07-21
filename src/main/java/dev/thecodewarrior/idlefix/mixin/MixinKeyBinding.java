package dev.thecodewarrior.idlefix.mixin;

import dev.thecodewarrior.idlefix.KeyBindingExtension;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyBinding.class)
public class MixinKeyBinding implements KeyBindingExtension {
    @Shadow private boolean pressed;
    @Shadow private int timesPressed;

    private boolean savedPressed = false;
    private int savedTimesPressed = 0;

    @Override
    public void saveState() {
        this.savedPressed = this.pressed;
        this.savedTimesPressed = this.timesPressed;
    }

    @Override
    public void clearSaved() {
        this.savedPressed = false;
        this.savedTimesPressed = 0;
    }

    @Override
    public void resetState() {
        this.pressed = false;
        this.timesPressed = 0;
    }

    @Override
    public void loadPressed() {
        this.pressed = this.pressed || this.savedPressed;
    }

    @Override
    public void loadTimesPressed() {
        this.timesPressed = Math.max(this.timesPressed, this.savedTimesPressed);
    }
}
