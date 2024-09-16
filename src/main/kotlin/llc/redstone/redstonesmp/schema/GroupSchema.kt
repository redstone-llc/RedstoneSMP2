package llc.redstone.redstonesmp.schema

import com.google.gson.JsonObject
import java.util.*
import kotlin.collections.ArrayList

data class GroupSchema(
    var name: String,
    var owner: String,
    var players: ArrayList<String>//uuids
) {
    fun setOwner(newOwner: UUID) = apply { owner = newOwner.toString() }
    fun addPlayer(player: UUID) = apply { players += player.toString() }
    fun removePlayer(player: UUID) = apply { players -= player.toString() }
    fun setPlayers(newPlayers: List<UUID>) = apply { players = newPlayers.map { it.toString() } as ArrayList<String> }
    fun setName(newName: String) = apply { name = newName }

    companion object {
        fun decode(json: JsonObject): GroupSchema {
            return GroupSchema(
                json["name"].asString,
                json["owner"].asString,
                json["players"].asJsonArray.map { it.asString } as ArrayList<String>
            )
        }
    }
}
