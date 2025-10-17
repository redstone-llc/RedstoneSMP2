package llc.redstone.redstonesmp.listeners

import de.maxhenkel.voicechat.api.Group
import llc.redstone.redstonesmp.RedstoneSMP
import llc.redstone.redstonesmp.RedstoneSMP.Companion.groupData
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents

object PlayerListener {
    fun startListening() {
        ServerPlayerEvents.JOIN.register { player ->
            val groups = groupData.filter { it.value.players.contains(player.uuid.toString()) }

            groups.forEach { (key, group) ->
                if (RedstoneSMP.API.groups.find { it.name == group.name } == null) {
                    val voiceGroup = RedstoneSMP.API.groupBuilder()
                        .setName(group.name)
                        .setId(group.uuid)
                        .setType(Group.Type.NORMAL)
                        .build()
                }
            }
        }

        ServerPlayerEvents.LEAVE.register { player ->
            val groups = groupData.filter { it.value.players.contains(player.uuid.toString()) }

            groups.forEach { (key, group) ->

                //Make sure no one is online
                val isAnyoneOnline = player.entityWorld.server.playerManager.playerList.any { p ->
                    group.players.contains(p.uuid.toString())
                }

                if (isAnyoneOnline) {
                    return@forEach
                }

                val voiceGroup = RedstoneSMP.API.groups.find { it.name == group.name }
                if (voiceGroup != null) {
                    RedstoneSMP.API.removeGroup(voiceGroup.id)
                }
            }
        }
    }
}