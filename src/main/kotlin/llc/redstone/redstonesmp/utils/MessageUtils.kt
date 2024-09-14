package llc.redstone.redstonesmp.utils

import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.PlainTextContent
import net.minecraft.text.Text
import net.minecraft.text.Text.*
import net.minecraft.text.TextContent

//Prefix must end with a space
fun sendMessage(sender: ServerPlayerEntity, player: ServerPlayerEntity, message: String, prefix: String) {
    val api: LuckPerms = LuckPermsProvider.get()
    val userGroup = api.userManager.getUser(sender.uuid)?.primaryGroup?: "default"
    when (userGroup) {
        "alt" -> {
            val text = MutableText.of(PlainTextContent.of("$prefix§8●§r §7")).append(sender.displayName).append(of(" §8>>§r §7$message"))
            player.sendMessage(text)
        }
        else -> {
            val text = MutableText.of(PlainTextContent.of("$prefix§8●§r ")).append(sender.displayName).append(of(" §8>>§r §f$message"))
            player.sendMessage(text)
        }
    }
}

fun sendToConsole(sender: ServerPlayerEntity, message: String, prefix: String) {
    val console = sender.server.commandSource
    val api: LuckPerms = LuckPermsProvider.get()
    val userGroup = api.userManager.getUser(sender.uuid)?.primaryGroup?: "default"
    when (userGroup) {
        "alt" -> {
            val text = MutableText.of(PlainTextContent.of("$prefix§8●§r §7")).append(sender.displayName).append(of(" §8>>§r §7$message"))
            console.sendMessage(text)
        }
        else -> {
            val text = MutableText.of(PlainTextContent.of("$prefix§8●§r ")).append(sender.displayName).append(of(" §8>>§r §f$message"))
            console.sendMessage(text)
        }
    }
}