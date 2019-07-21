package dev.thecodewarrior.idlefix

import dev.thecodewarrior.idlefix.mixin.IKeyBinding
import dev.thecodewarrior.idlefix.timing.Timing
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

object IdlefixMod: ClientModInitializer {
    @JvmStatic
    lateinit var key: FabricKeyBinding
        private set

    var timing: Timing = object: Timing() {
        override fun isPressed() = true
        override fun wasPressed() = true
    }
    val keyBinds = IKeyBinding.getKeysById()
    val enabledKeyBinds = mutableMapOf<String, KeyBinding>()

    override fun onInitializeClient() {
        key = FabricKeyBinding.Builder.create(
            Identifier("idlefix:toggle"),
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F10,
            "key.categories.misc"
        ).build()

        KeyBindingRegistry.INSTANCE.register(key)
    }

    @JvmStatic
    fun handleKeybinds() {
        while(key.wasPressed()) {
            if(enabledKeyBinds.isEmpty()) {
                keyBinds.forEach { (id, binding) ->
                    if (binding == key)
                        return@forEach
                    if (binding.isPressed) {
                        enabledKeyBinds[id] = binding
                    }
                }
            } else {
                enabledKeyBinds.forEach { (_, it) ->
                    val kb = it as IKeyBinding
                    kb.isPressed = false
                    kb.timesPressed = 0
                }
                enabledKeyBinds.clear()
            }
        }
        enabledKeyBinds.forEach { (id, binding) ->
            val isPressed = timing.isPressed(id, binding)
            val wasPressed = timing.wasPressed(id, binding)

            val kb = binding as IKeyBinding
            kb.isPressed = isPressed
            if(wasPressed)
                kb.timesPressed = 1
        }
    }
}
