package dev.thecodewarrior.idlefix.mixin;

import dev.thecodewarrior.idlefix.IdlefixMod;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(method = "handleInputEvents", at=@At("HEAD"))
    private void handleKeybinds(CallbackInfo callbackInfo) {
        IdlefixMod.handleKeybinds();
    }
}
