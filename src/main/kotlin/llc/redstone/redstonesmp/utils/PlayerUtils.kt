package llc.redstone.redstonesmp.utils

import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

fun ServerPlayerEntity.sendMessage(message: String) {
    this.sendMessage(Text.literal(message))
}

fun ServerCommandSource.sendMessage(message: String) {
    this.sendMessage(Text.literal(message))
}