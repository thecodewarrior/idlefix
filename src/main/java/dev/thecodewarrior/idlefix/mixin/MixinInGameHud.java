package dev.thecodewarrior.idlefix.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.thecodewarrior.idlefix.IdlefixMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud extends DrawableHelper {
    private static Identifier HUD_TEXTURE = new Identifier("idlefix:textures/hud.png");

    @Shadow @Final private MinecraftClient client;
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    @Inject(method = "renderCrosshair", at = @At("HEAD"))
    private void renderIdlefixHud(CallbackInfo ci) {
        if(IdlefixMod.getEnabledKeyBinds().isEmpty())
            return;

        client.getTextureManager().bindTexture(HUD_TEXTURE);

        boolean[] layers = new boolean[] {
                false, //IdlefixMod.getTiming().isPressed(),
                IdlefixMod.getTiming().isPressed(),
                true,
                IdlefixMod.getTiming().wasPressed()
        };

        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        for (int i = 0; i < layers.length; i++) {
            if(layers[i])
                blit((this.scaledWidth - 21) / 2, (this.scaledHeight - 15) / 2 - 8, blitOffset, 0, 8*i, 21, 8, 32, 32);
        }
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//        blit(this.left + 83 + 99, this.top + 35, this.blitOffset, 311.0F, 0.0F, 28, 21, 256, 512);
//        if (this.client.options.attackIndicator == AttackIndicator.CROSSHAIR) {
//            float float_1 = this.client.player.getAttackCooldownProgress(0.0F);
//            boolean boolean_1 = false;
//            if (this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity && float_1 >= 1.0F) {
//                boolean_1 = this.client.player.getAttackCooldownProgressPerTick() > 5.0F;
//                boolean_1 &= this.client.targetedEntity.isAlive();
//            }
//
//            int int_2 = this.scaledHeight / 2 - 7 + 16;
//            int int_3 = this.scaledWidth / 2 - 8;
//            if (boolean_1) {
//                this.blit(int_3, int_2, 68, 94, 16, 16);
//            } else if (float_1 < 1.0F) {
//                int int_4 = (int)(float_1 * 17.0F);
//                this.blit(int_3, int_2, 36, 94, 16, 4);
//                this.blit(int_3, int_2, 52, 94, int_4, 4);
//            }
//        }

        client.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
    }
}
