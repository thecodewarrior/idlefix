package dev.thecodewarrior.idlefix.timing

import net.minecraft.client.options.KeyBinding

abstract class Timing {
    open fun isPressed(id: String, binding: KeyBinding): Boolean = isPressed()
    open fun wasPressed(id: String, binding: KeyBinding): Boolean = wasPressed()

    abstract fun isPressed(): Boolean
    abstract fun wasPressed(): Boolean

    companion object {
        @JvmStatic
        var time: Int = 0
    }
}
