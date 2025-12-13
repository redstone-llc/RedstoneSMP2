package llc.redstone.redstonesmp.utils

import eu.pb4.placeholders.api.PlaceholderContext
import eu.pb4.placeholders.api.node.LiteralNode
import eu.pb4.placeholders.api.node.TextNode
import eu.pb4.styledchat.StyledChatStyles
import eu.pb4.styledchat.StyledChatUtils
import eu.pb4.styledchat.config.ConfigManager
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text.*

//Prefix must end with a space
fun sendMessage(sender: ServerPlayerEntity, player: ServerPlayerEntity, message: String, prefix: String) {
    val context = PlaceholderContext.of(sender) ?: return
    val config = ConfigManager.getConfig();
    val parser = StyledChatUtils.createParser(context);
    var value = TextNode.asSingle(*parser.parseNodes(LiteralNode(message))).toText(context);
    if (config.configData.formatting.respectColors) {
        try {
            value = context.server().messageDecorator.decorate(context.player(), value)
        } catch (e: Exception) {
            // noop
        }
    }
    var text = StyledChatStyles.getChat(sender, value);
    text = literal(prefix).append(text);
    player.sendMessage(text)
}

fun sendToConsole(sender: ServerPlayerEntity, message: String, prefix: String) {
    val console = sender.entityWorld.server?.commandSource
    var text = StyledChatStyles.getChat(sender, literal(message));
    text = literal(prefix).append(text);

    console?.sendMessage(text)
}