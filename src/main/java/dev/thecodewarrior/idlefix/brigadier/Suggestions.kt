package dev.thecodewarrior.idlefix.brigadier

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.Locale
import java.util.concurrent.CompletableFuture
import kotlin.math.min

class ChoiceListSuggestionProvider<S>(val listProvider: (context: CommandContext<S>, builder: SuggestionsBuilder) -> List<String>): SuggestionProvider<S> {
    override fun getSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val remaining = builder.remaining
        listProvider(context, builder).filter { it.startsWith(remaining, true) }.forEach { builder.suggest(it) }
        return builder.buildFuture()
    }

    private companion object {
        val matrixSize = 128
        val distance: Array<IntArray> = Array(matrixSize + 1) { IntArray(matrixSize + 1) }

        private fun min(a: Int, b: Int, c: Int): Int {
            return min(min(a, b), c)
        }

        fun computeLevenshteinDistance(lhs: CharSequence, rhs: CharSequence): Int {
            if(lhs.length > matrixSize)
                throw IllegalArgumentException("String length ${lhs.length} is too long")
            if(rhs.length > matrixSize)
                throw IllegalArgumentException("String length ${rhs.length} is too long")


            for (i in 0..lhs.length)
                distance[i][0] = i
            for (j in 1..rhs.length)
                distance[0][j] = j

            for (i in 1..lhs.length)
                for (j in 1..rhs.length)
                    distance[i][j] = min(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + if (lhs[i - 1] == rhs[j - 1]) 0 else 1
                    )

            return distance[lhs.length][rhs.length]
        }
    }
}
