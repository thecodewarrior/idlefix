package dev.thecodewarrior.idlefix

import dev.thecodewarrior.idlefix.mixin.IKeyBinding
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import java.lang.reflect.Modifier

object IdlefixMod: ClientModInitializer {
    @JvmStatic
    lateinit var key: FabricKeyBinding
        private set

    @JvmStatic
    var enabled: Boolean = false
    @JvmStatic
    val keysDown: IntSet = IntOpenHashSet()
    @JvmStatic
    val knownKeys: IntSet = IntOpenHashSet()

    val keyBinds: Collection<KeyBinding>

    init {
        keyBinds = IKeyBinding.getKeysById().values
        knownKeys.addAll(
            GLFW::class.java.declaredFields
                .filter {
                    it.name.startsWith("GLFW_KEY_") && Modifier.isStatic(it.modifiers)
                }
                .map {
                    it.getInt(null)
                }
        )
    }

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
            if(enabled) {
                keysDown.clear()
                enabled = false
                keyBinds.forEach {
                    if(it == key)
                        return@forEach
                    (it as KeyBindingExtension).resetState()
                }
            } else {
                keysDown.clear()
                keysDown.addAll(
                    knownKeys.filter { keyCode ->
                        InputUtil.isKeyPressed(MinecraftClient.getInstance().window.handle, keyCode)
                    }
                )
                enabled = true
                keyBinds.forEach { bind ->
                    if(bind == key)
                        return@forEach
                    bind as KeyBindingExtension
                    if(bind.isPressed) {
                        bind.saveState()
                    } else {
                        bind.resetState()
                    }
                }
            }
        }
        keyBinds.forEach {
            it as KeyBindingExtension
            it.loadPressed()
        }
    }
}
