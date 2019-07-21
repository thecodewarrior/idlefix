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

class TapAndHoldTiming(private val hold: Boolean, private val interval: Int): Timing() {
    init {
        if(interval < 0)
            throw IllegalArgumentException("Interval $interval must not be negative")
    }

    override fun isPressed(): Boolean = hold || wasPressed()
    override fun wasPressed(): Boolean = interval == 0 || time % interval == 0
}

class CycleTiming(private val onInterval: Int, private val offInterval: Int): Timing() {
    init {
        if(onInterval <= 0)
            throw IllegalArgumentException("onInterval $onInterval must be positive")
        if(offInterval <= 0)
            throw IllegalArgumentException("offInterval $offInterval must be positive")
    }

    override fun isPressed(): Boolean = time % (onInterval + offInterval) < onInterval
    override fun wasPressed(): Boolean = time % (onInterval + offInterval) == 0
}

