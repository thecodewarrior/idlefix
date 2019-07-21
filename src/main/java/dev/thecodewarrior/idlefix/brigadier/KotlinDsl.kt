package dev.thecodewarrior.idlefix.brigadier

import com.mojang.brigadier.Command
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.SingleRedirectModifier
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.ArgumentCommandNode
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import java.util.concurrent.CompletableFuture
import java.util.function.Predicate
import kotlin.math.min

@DslMarker
@Target(AnnotationTarget.CLASS)
annotation class BrigadierDsl

fun <S> literal(name: String, config: LiteralArgumentDsl<S>.() -> Unit): LiteralCommandNode<S> {
    val argument = LiteralArgumentBuilder.literal<S>(name)
    LiteralArgumentDsl(argument).config()
    return argument.build()
}

fun <S, T> argument(
    name: String,
    type: ArgumentType<T>,
    config: RequiredArgumentDsl<S, T>.(Argument<T>) -> Unit
): ArgumentCommandNode<S, T> {
    val argument: RequiredArgumentBuilder<S, T> = RequiredArgumentBuilder.argument(name, type)
    RequiredArgumentDsl(argument).config(Argument(name, type))
    return argument.build()
}

class Argument<T>(val name: String, val type: ArgumentType<T>)

@BrigadierDsl
class Execution<S>(val context: CommandContext<S>) {
    val source: S get() = context.source
    var result = 1

    inline operator fun <reified T> Argument<T>.invoke(): T {
        return context.getArgument(this.name, T::class.java)
    }

    inline operator fun <reified T> get(name: String): T {
        return context.getArgument(name, T::class.java)
    }
}

@BrigadierDsl
abstract class ArgumentDsl<S, T: ArgumentBuilder<S, T>>(val builder: T) {

    operator fun <T: CommandNode<S>> T.unaryPlus(): T {
        builder.then(this)
        return this
    }

    fun literal(name: String, config: LiteralArgumentDsl<S>.() -> Unit): LiteralCommandNode<S> {
        return literal<S>(name, config)
    }

    fun <T> argument(
        name: String,
        type: ArgumentType<T>,
        config: RequiredArgumentDsl<S, T>.(Argument<T>) -> Unit
    ): ArgumentCommandNode<S, T> {
        val argument = RequiredArgumentBuilder.argument<S, T>(name, type)
        RequiredArgumentDsl(argument).config(Argument(name, type))
        return argument.build()
    }

    operator fun String.invoke(config: LiteralArgumentDsl<S>.() -> Unit): LiteralCommandNode<S> {
        return literal(this, config)
    }

    fun String.boolean(
        config: RequiredArgumentDsl<S, Boolean>.(Argument<Boolean>) -> Unit
    ): ArgumentCommandNode<S, Boolean> {
        return argument(this, BoolArgumentType.bool(), config)
    }

    fun String.int(
        range: IntRange = Int.MIN_VALUE .. Int.MAX_VALUE,
        config: RequiredArgumentDsl<S, Int>.(Argument<Int>) -> Unit
    ): ArgumentCommandNode<S, Int> {
        return argument(this, IntegerArgumentType.integer(range.first, range.last), config)
    }

    fun String.long(
        range: LongRange = Long.MIN_VALUE .. Long.MAX_VALUE,
        config: RequiredArgumentDsl<S, Long>.(Argument<Long>) -> Unit
    ): ArgumentCommandNode<S, Long> {
        return argument(this, LongArgumentType.longArg(range.first, range.last), config)
    }

    fun String.float(
        min: Float = -Float.MAX_VALUE, max: Float = Float.MAX_VALUE,
        config: RequiredArgumentDsl<S, Float>.(Argument<Float>) -> Unit
    ): ArgumentCommandNode<S, Float> {
        return argument(this, FloatArgumentType.floatArg(min, max), config)
    }

    fun String.double(
        min: Double = -Double.MAX_VALUE, max: Double = Double.MAX_VALUE,
        config: RequiredArgumentDsl<S, Double>.(Argument<Double>) -> Unit
    ): ArgumentCommandNode<S, Double> {
        return argument(this, DoubleArgumentType.doubleArg(min, max), config)
    }

    /**
     * A single word string argument
     */
    fun String.word(
        config: RequiredArgumentDsl<S, String>.(Argument<String>) -> Unit
    ): ArgumentCommandNode<S, String> {
        return argument(this, StringArgumentType.word(), config)
    }

    /**
     * An optionally quoted string argument
     */
    fun String.string(
        config: RequiredArgumentDsl<S, String>.(Argument<String>) -> Unit
    ): ArgumentCommandNode<S, String> {
        return argument(this, StringArgumentType.string(), config)
    }

    /**
     * A greedy string argument
     */
    fun String.greedyString(
        config: RequiredArgumentDsl<S, String>.(Argument<String>) -> Unit
    ): ArgumentCommandNode<S, String> {
        return argument(this, StringArgumentType.greedyString(), config)
    }

//    fun argument(name: String): RequiredArgumentTypePicker<S> {
//        return RequiredArgumentTypePicker(name)
//    }

    private val executions = mutableListOf<Execution<S>.() -> Unit>()
    private val command = Command<S> { context ->
        val exec = Execution(context)
        executions.forEach { it(exec) }
        exec.result
    }

    fun execute(executor: Execution<S>.() -> Unit) {
        executions.add(executor)
        builder.executes(command)
    }

    private val requirements = mutableListOf<(S) -> Boolean>()
    private val predicate = Predicate<S> {
        requirements.all { req -> req(it) }
    }

    fun require(predicate: (S) -> Boolean) {
        requirements.add(predicate)
        builder.requires(this.predicate)
    }

    fun redirect(target: CommandNode<S>) =
        builder.redirect(target)

    fun redirect(target: CommandNode<S>, modifier: SingleRedirectModifier<S>) =
        builder.redirect(target, modifier)
    fun fork(target: CommandNode<S>, modifier: RedirectModifier<S>) =
        builder.fork(target, modifier)
    fun forward(target: CommandNode<S>, modifier: RedirectModifier<S>, fork: Boolean) =
        builder.forward(target, modifier, fork)
}

class LiteralArgumentDsl<S>(builder: LiteralArgumentBuilder<S>): ArgumentDsl<S, LiteralArgumentBuilder<S>>(builder)

class RequiredArgumentDsl<S, T>(builder: RequiredArgumentBuilder<S, T>): ArgumentDsl<S, RequiredArgumentBuilder<S, T>>(builder) {
    var suggestions: SuggestionProvider<S>
        get() = builder.suggestionsProvider
        set(value) {
            builder.suggests(value)
        }

    fun suggest(choices: Execution<S>.() -> List<String>) {
        suggestions = ChoiceListSuggestionProvider { context, _ ->
            Execution(context).choices()
        }
    }

    fun suggest(choices: List<String>) {
        suggestions = ChoiceListSuggestionProvider { _, _ -> choices }
    }
}

