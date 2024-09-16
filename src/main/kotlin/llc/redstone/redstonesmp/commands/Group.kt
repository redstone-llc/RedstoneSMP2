package llc.redstone.redstonesmp.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import llc.redstone.redstonesmp.RedstoneSMP.Companion.groupData
import llc.redstone.redstonesmp.RedstoneSMP.Companion.playerChatMap
import llc.redstone.redstonesmp.schema.GroupSchema
import llc.redstone.redstonesmp.utils.sendMessage
import llc.redstone.redstonesmp.utils.sendToConsole
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.minecraft.command.CommandSource
import net.minecraft.entity.attribute.DefaultAttributeRegistry
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import java.util.UUID
import java.util.concurrent.CompletableFuture


fun createGroupCommand() {
    CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
        val gc = dispatcher.register(literal("gc")
            .then(argument("group", StringArgumentType.word())
                .suggests(GroupSuggestions())
                .executes { context ->
                    val source = context.source
                    val group = StringArgumentType.getString(context, "group")
                    Group.chat(source, group, null)
                    1
                }
                .then(argument("message", StringArgumentType.greedyString())
                    .executes { context ->
                        val source = context.source
                        val group = StringArgumentType.getString(context, "group")
                        val message = StringArgumentType.getString(context, "message")
                        Group.chat(source, group, message)
                        1
                    }
                )
            )
        )

        val group = dispatcher.register(literal("group")
            .then(literal("leave")
                .then(argument("group", StringArgumentType.word())
                    .suggests(GroupSuggestions())
                    .executes { context ->
                        val source = context.source
                        val group = StringArgumentType.getString(context, "group")
                        Group.leave(source, group)
                        1
                    }
                )
            )
            .then(literal("create")
                .then(argument("name", StringArgumentType.word())
                    .executes { context ->
                        val source = context.source
                        val name = StringArgumentType.getString(context, "name")
                        Group.create(source, name)
                        1
                    }
                )
            )
            .then(literal("add")
                .then(argument("player", StringArgumentType.word())
                    .suggests(PlayerSuggestions())
                    .then(argument("group", StringArgumentType.word())
                        .suggests(GroupSuggestions())
                        .executes { context ->
                            val source = context.source
                            val player = StringArgumentType.getString(context, "player")
                            val group = StringArgumentType.getString(context, "group")
                            Group.add(source, player, group)
                            1
                        }
                    )
                )
            )
            .then(literal("remove")
                .then(argument("player", StringArgumentType.word())
                    .suggests(PlayerSuggestions())
                    .then(argument("group", StringArgumentType.word())
                        .suggests(GroupSuggestions())
                        .executes { context ->
                            val source = context.source
                            val player = StringArgumentType.getString(context, "player")
                            val group = StringArgumentType.getString(context, "group")
                            Group.remove(source, player, group)
                            1
                        }
                    )
                )
            )
            .then(literal("list")
                .then(argument("group", StringArgumentType.word())
                    .suggests(GroupSuggestions())
                    .executes { context ->
                        val source = context.source
                        val group = StringArgumentType.getString(context, "group")
                        Group.list(source, group)
                        1
                    }
                )
            )
            .then(literal("listall")
                .executes { context ->
                    val source = context.source
                    Group.listAll(source)
                    1
                }
            )
            .then(literal("delete")
                .then(argument("group", StringArgumentType.word())
                    .suggests(GroupSuggestions())
                    .executes { context ->
                        val source = context.source
                        val group = StringArgumentType.getString(context, "group")
                        Group.delete(source, group)
                        1
                    }
                )
            )
            .then(literal("setname")
                .then(argument("group", StringArgumentType.word())
                    .suggests(GroupSuggestions())
                    .then(argument("name", StringArgumentType.word())
                        .executes { context ->
                            val source = context.source
                            val group = StringArgumentType.getString(context, "group")
                            val name = StringArgumentType.getString(context, "name")
                            Group.setName(source, group, name)
                            1
                        }
                    )
                )
            )
            .then(literal("chat")
                .then(argument("group", StringArgumentType.word())
                    .suggests(GroupSuggestions())
                    .executes { context ->
                        val source = context.source
                        val group = StringArgumentType.getString(context, "group")
                        Group.chat(source, group, null)
                        1
                    }
                    .then(argument("message", StringArgumentType.greedyString())
                        .executes { context ->
                            val source = context.source
                            val group = StringArgumentType.getString(context, "group")
                            val message = StringArgumentType.getString(context, "message")
                            Group.chat(source, group, message)
                            1
                        }
                    )
                )
            )
        )
    }
}

class Group {
    companion object {

        fun leave(context: ServerCommandSource, group: String) {
            if (!groupData.containsKey(group)) {
                context.sendMessage("§cCHAT §8|§r §cGroup $group does not exist.")
                return
            }

            val player = context.player ?: return
            if (!groupData[group]!!.players.contains(player.uuid.toString())) {
                context.sendMessage("§cCHAT §8|§r §cYou are not in group $group.")
                return
            }

            if (groupData[group]!!.owner == player.uuid.toString()) {
                context.sendMessage("§cCHAT §8|§r §cYou are the owner of group $group. You cannot leave.")
                return
            }

            groupData[group]!!.players.remove(player.uuid.toString())
            context.sendMessage("§cCHAT §8|§r §7You have left group $group.")
        }

        fun create(context: ServerCommandSource, name: String) {
            if (groupData.containsKey(name)) {
                context.sendMessage("§cCHAT §8|§r §cGroup $name already exists.")
                return
            }

            if (name == "all" || name == "global" || name == "local") {
                context.sendMessage("§cCHAT §8|§r §cGroup $name is a reserved name.")
                return
            }

            val player = context.player ?: return
            groupData[name] = GroupSchema(name, player.uuid.toString(), arrayListOf(player.uuid.toString()))
            context.sendMessage("§cCHAT §8|§r §7Group $name has been created.")
        }

        fun add(context: ServerCommandSource, player: String, group: String) {
            if (!groupData.containsKey(group)) {
                context.sendMessage("§cCHAT §8|§r §cGroup $group does not exist.")
                return
            }

            val p = context.player ?: return
            if (groupData[group]!!.owner != p.uuid.toString()) {
                context.sendMessage("§cCHAT §8|§r §cYou are not the owner of group $group.")
                return
            }

            if (groupData[group]!!.players.contains(p.uuid.toString())) {
                context.sendMessage("§cCHAT §8|§r §cPlayer $player is already in group $group.")
                return
            }

            val target = context.server.playerManager.getPlayer(player) ?: run {
                context.sendMessage("§cCHAT §8|§r §cPlayer $player does not exist.")
                return
            }

            groupData[group]!!.players.add(target.uuid.toString())
            context.sendMessage("§cCHAT §8|§r §7Player ${target.styledDisplayName.string} has been added to group $group.")
        }

        fun remove(context: ServerCommandSource, player: String, group: String) {
            if (!groupData.containsKey(group)) {
                context.sendMessage("§cCHAT §8|§r §cGroup $group does not exist.")
                return
            }

            val p = context.player ?: return
            if (groupData[group]!!.owner != p.uuid.toString()) {
                context.sendMessage("§cCHAT §8|§r §cYou are not the owner of group $group.")
                return
            }

            val target = context.server.playerManager.getPlayer(player) ?: run {
                context.sendMessage("§cCHAT §8|§r §cPlayer $player does not exist.")
                return
            }
            if (groupData[group]!!.owner == target.uuid.toString()) {
                context.sendMessage("§cCHAT §8|§r §cYou cannot remove the owner of group $group.")
                return
            }
            if (!groupData[group]!!.players.contains(target.uuid.toString())) {
                context.sendMessage("§cCHAT §8|§r §cPlayer $player is not in group $group.")
                return
            }
            groupData[group]!!.players.remove(target.uuid.toString())
            context.sendMessage("§cCHAT §8|§r §7Player ${target.styledDisplayName.string} has been removed from group $group.")
        }

        fun list(context: ServerCommandSource, group: String) {
            if (!groupData.containsKey(group)) {
                context.sendMessage("§cCHAT §8|§r §cGroup $group does not exist.")
                return
            }

            val p = context.player ?: return
            if (!groupData[group]!!.players.contains(p.uuid.toString())) {
                context.sendMessage("§cCHAT §8|§r §cYou are not in group $group.")
                return
            }

            val players = groupData[group]!!.players
            context.sendMessage("§cCHAT §8|§r §7Players in group $group: ")
            players.forEach {
                //Get offline player names
                val api: LuckPerms = LuckPermsProvider.get()
                val user = api.userManager.getUser(UUID.fromString(it))
                val name = user?.username
                context.sendMessage("§8- §f$name")
            }
        }

        fun listAll(context: ServerCommandSource) {
            val p = context.player ?: return
            val groups = groupData.filter { it.value.players.contains(p.uuid.toString()) }
            if (groups.isEmpty()) {
                context.sendMessage("§cCHAT §8|§r §cYou are not in any groups.")
                return
            }

            groups.forEach { (name, group) ->
                context.sendMessage("§cCHAT §8|§r §7Players in group $name:")
                group.players.forEach {
                    //Get offline player names
                    val api: LuckPerms = LuckPermsProvider.get()
                    val user = api.userManager.getUser(UUID.fromString(it))
                    val name = user?.username
                    context.sendMessage("§8- §f$name")
                }
            }
        }

        fun delete(context: ServerCommandSource, group: String) {
            if (!groupData.containsKey(group)) {
                context.sendMessage("§cCHAT §8|§r §cGroup $group does not exist.")
                return
            }

            val p = context.player ?: return
            if (groupData[group]!!.owner != p.uuid.toString()) {
                context.sendMessage("§cCHAT §8|§r §cYou are not the owner of group $group.")
                return
            }

            groupData.remove(group)
            context.sendMessage("§cCHAT §8|§r §7Group $group has been deleted.")
        }

        fun setName(context: ServerCommandSource, group: String, name: String) {
            if (!groupData.containsKey(group)) {
                context.sendMessage("§cCHAT §8|§r §cGroup $group does not exist.")
                return
            }

            val p = context.player ?: return
            if (groupData[group]!!.owner != p.uuid.toString()) {
                context.sendMessage("§cCHAT §8|§r §cYou are not the owner of group $group.")
                return
            }

            val data = groupData[group]!!
            groupData[name] = data
            groupData.remove(group)
            groupData[name]!!.name = name
            context.sendMessage("§cCHAT §8|§r §7Group $group has been renamed to $name.")
        }

        fun chat(context: ServerCommandSource, group: String, message: String?) {
            if (!groupData.containsKey(group)) {
                context.sendMessage("§cCHAT §8|§r §cGroup $group does not exist.")
                return
            }

            val p = context.player ?: return
            if (!groupData[group]!!.players.contains(p.uuid.toString())) {
                context.sendMessage("§cCHAT §8|§r §cYou are not in group $group.")
                return
            }

            val data = groupData[group]!!
            if (message == null) {
                playerChatMap[p.uuid] = group
                context.sendMessage("§cCHAT §8|§r §7Now chatting in group ${data.name}.")
                return
            }

            val players = groupData[group]!!.players
            players.forEach {
                val target = context.server.playerManager.getPlayer(UUID.fromString(it)) ?: return
                sendMessage(p, target, message, "§a${data.name.uppercase()}")
            }
            sendToConsole(p, message, "§a${data.name.uppercase()}")
        }
    }
}

class GroupSuggestions : SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>?,
        builder: SuggestionsBuilder?
    ): CompletableFuture<Suggestions> {
        val player = context?.source?.player ?: return Suggestions.empty()
        val groups = groupData.filter { it.value.players.contains(player.uuid.toString()) }.keys
        return CommandSource.suggestMatching(groups, builder)
    }
}

class PlayerSuggestions : SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>?,
        builder: SuggestionsBuilder?
    ): CompletableFuture<Suggestions> {
        val players =
            context?.source?.server?.playerManager?.playerList?.map { it.name.string } ?: return Suggestions.empty()
        return CommandSource.suggestMatching(players, builder)
    }
}