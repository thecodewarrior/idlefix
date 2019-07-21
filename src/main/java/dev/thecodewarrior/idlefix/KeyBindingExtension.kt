package dev.thecodewarrior.idlefix

interface KeyBindingExtension {
    fun saveState()
    fun clearSaved()
    fun resetState()
    fun loadPressed()
    fun loadTimesPressed()
}