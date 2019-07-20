package dev.thecodewarrior.idlefix

interface KeyBindingExtension {
    fun saveState()
    fun resetState()
    fun loadPressed()
    fun loadTimesPressed()
}