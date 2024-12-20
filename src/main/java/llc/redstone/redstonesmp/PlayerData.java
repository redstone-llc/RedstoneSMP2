package llc.redstone.redstonesmp;

import java.util.Stack;

public class PlayerData {
    private String uuid;
    private String originId;
    private String layerId;
    private String playerNBT;
    private String playerStats;
    public Stack<String> playerNBTBackups = new Stack<>();
    private String tpCoords;
    private boolean inAdventureServer;
    public String regionName;
    public boolean selectedContinent;
    public String username;


    public FactionData factionData;

    public PlayerData() {
    }

    public PlayerData(String uuid, String originId, String layerId, String playerNBT, String playerStats, String tpCoords, boolean inAdventureServer, boolean selectedContinent) {
        this.uuid = uuid;
        this.originId = originId;
        this.layerId = layerId;
        this.playerNBT = playerNBT;
        this.playerStats = playerStats;
        this.tpCoords = tpCoords;
        this.inAdventureServer = inAdventureServer;
        this.selectedContinent = selectedContinent;
    }

    public String getUuid() {
        return uuid;
    }

    public String getOriginId() {
        return originId;
    }

    public String getLayerId() {
        return layerId;
    }

    public String getPlayerNBT() {
        return playerNBT;
    }

    public String getPlayerStats() {
        return playerStats;
    }

    public String getTpCoords() {
        return tpCoords;
    }

    public boolean isInAdventureServer() {
        return inAdventureServer;
    }

    public void setInAdventureServer(boolean inAdventureServer) {
        this.inAdventureServer = inAdventureServer;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public void setLayerId(String layerId) {
        this.layerId = layerId;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setPlayerNBT(String playerNBT) {
        this.playerNBT = playerNBT;
    }

    public void setPlayerStats(String playerStats) {
        this.playerStats = playerStats;
    }

    public void setTpCoords(String tpCoords) {
        this.tpCoords = tpCoords;
    }

    @Override
    public String toString() {
        return "PlayerData{" +
                "uuid='" + uuid + '\'' +
                ", originId='" + originId + '\'' +
                ", layerId='" + layerId + '\'' +
                ", playerNBT='" + playerNBT + '\'' +
                ", playerStats='" + playerStats + '\'' +
                ", tpCoords='" + tpCoords + '\'' +
                ", inAdventureServer=" + inAdventureServer +
                '}';
    }

    public static class FactionData { //This is only used for cross server communication
        public String factionName;
        public String color;

        public FactionData() {
        }

        public FactionData(String factionName, String color) {
            this.factionName = factionName;
            this.color = color;
        }
    }
}
