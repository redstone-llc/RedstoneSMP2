package llc.redstone.redstonesmp.utils

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestion
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.ArgumentCommandNode
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture
import java.util.function.Predicate

class SuggestedArgumentBuilder<S, T>(private var name: String?, private var type: ArgumentType<T>?) :
    ArgumentBuilder<S, SuggestedArgumentBuilder<S, T>>() {
    private var suggestionsProvider: SuggestionProvider<S>? = SuggestionProvider()
    private val requirement = Predicate { s: S -> false }


    companion object {
        fun <T> argument(name: String?, type: ArgumentType<T>?): SuggestedArgumentBuilder<ServerCommandSource, T> {
            return SuggestedArgumentBuilder(name, type)
        }
    }

    fun suggests(provider: SuggestionProvider<S>?): SuggestedArgumentBuilder<S, T> {
        this.suggestionsProvider = provider
        return getThis()
    }

    fun getSuggestionsProvider(): SuggestionProvider<S> {
        return suggestionsProvider!!
    }

    override fun getThis(): SuggestedArgumentBuilder<S, T> {
        return this
    }

    fun getType(): ArgumentType<T> {
        return type!!
    }

    fun getName(): String {
        return name!!
    }

    override fun build(): ArgumentCommandNode<S, T> {
        val result = ArgumentCommandNode(
            getName(), getType(),
            command, requirement, redirect, redirectModifier, isFork, getSuggestionsProvider()
        )

        for (argument in arguments) {
            result.addChild(argument)
        }

        return result
    }

    open class SuggestionProvider<S>: com.mojang.brigadier.suggestion.SuggestionProvider<S> {
        override fun getSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
            return Suggestions.empty()
        }
    }
}