package dev.thecodewarrior.idlefix

import com.mojang.brigadier.CommandDispatcher
import dev.thecodewarrior.idlefix.brigadier.ArgumentDsl
import dev.thecodewarrior.idlefix.brigadier.LiteralArgumentDsl
import dev.thecodewarrior.idlefix.brigadier.literal
import dev.thecodewarrior.idlefix.timing.CycleTiming
import dev.thecodewarrior.idlefix.timing.TapAndHoldTiming
import dev.thecodewarrior.idlefix.timing.Timing
import io.github.cottonmc.clientcommands.ArgumentBuilders
import io.github.cottonmc.clientcommands.ClientCommandPlugin
import io.github.cottonmc.clientcommands.CottonClientCommandSource
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

object IdlefixCommands: ClientCommandPlugin {
    override fun registerCommands(dispatcher: CommandDispatcher<CottonClientCommandSource>) {
        dispatcher.root.addChild(
            literal("idlefix") {
                +"help" {
                    +"simple" {
                        execute {
                            source.sendFeedback(TranslatableText("commands.idlefix.help.simple"))
                        }
                    }
                    +"cycle" {
                        execute {
                            source.sendFeedback(TranslatableText("commands.idlefix.help.cycle"))
                        }
                    }
                }

                +"set" {
                    +"simple" {
                        +"tapInterval".float(0f, Float.MAX_VALUE) { interval ->
                            +"hold".boolean { hold ->
                                execute {
                                    IdlefixMod.timing = TapAndHoldTiming(hold(), (interval() * 20).toInt())
                                }
                            }
                            execute {
                                IdlefixMod.timing = TapAndHoldTiming(true, (interval() * 20).toInt())
                            }
                        }
                    }

                    +"cycle" {
                        +"onInterval".float(0f, Float.MAX_VALUE) { onInterval ->
                            +"offInterval".float(0f, Float.MAX_VALUE) { offInterval ->
                                +"tapInterval".float(0f, Float.MAX_VALUE) { tapInterval ->
                                    execute {
                                        IdlefixMod.timing = CycleTiming((onInterval() * 20).toInt(), (offInterval() * 20).toInt(), (tapInterval() * 20).toInt())
                                    }
                                }
                                execute {
                                    IdlefixMod.timing = CycleTiming((onInterval() * 20).toInt(), (offInterval() * 20).toInt(), Int.MAX_VALUE)
                                }
                            }
                            execute {
                                IdlefixMod.timing = CycleTiming((onInterval() * 20).toInt(), (onInterval() * 20).toInt(), Int.MAX_VALUE)
                            }
                        }
                    }
                }

            }
        )
    }
}