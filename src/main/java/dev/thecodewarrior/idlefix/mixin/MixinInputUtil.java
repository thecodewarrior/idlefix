package dev.thecodewarrior.idlefix.mixin;

import dev.thecodewarrior.idlefix.IdlefixMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InputUtil.class)
public class MixinInputUtil {
	@Inject(at = @At("RETURN"), method = "isKeyPressed", cancellable = true)
	private static void keyPressedMixin(long window, int keyCode, CallbackInfoReturnable<Boolean> cir) {
		IdlefixMod.getKnownKeys().add(keyCode);
	    if(IdlefixMod.getEnabled() && IdlefixMod.getKeysDown().contains(keyCode)) {
	    	cir.setReturnValue(true);
		}
	}
}
