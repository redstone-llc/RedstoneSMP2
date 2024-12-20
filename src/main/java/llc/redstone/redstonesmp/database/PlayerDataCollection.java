package llc.redstone.redstonesmp.database;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import llc.redstone.redstonesmp.PlayerData;
import llc.redstone.redstonesmp.RedstoneSMP;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class PlayerDataCollection {
    private MongoClient mongoClient;
    private MongoCollection<PlayerData> playerDataCollection;
    public PlayerDataCollection() {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(PlayerData.class, PlayerData.FactionData.class).automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

        String uri = RedstoneSMP.config.get("mongo-uri").getAsString();
        mongoClient = MongoClients.create(uri);
        try {
            playerDataCollection = mongoClient.getDatabase("smp")
                    .withCodecRegistry(pojoCodecRegistry)
                    .getCollection("playerData", PlayerData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertPlayerData(PlayerData playerData) {
        playerDataCollection.insertOne(playerData);
    }

    public void updatePlayerData(PlayerData playerData) {
        playerDataCollection.replaceOne(BsonDocument.parse("{uuid: \"" + playerData.getUuid() + "\"}"), playerData);
    }

    public PlayerData getPlayerData(UUID uuid) {
        String id = uuid.toString().replace("-", "");
        return playerDataCollection.find(BsonDocument.parse("{uuid: \"" + id + "\"}")).first();
    }

    public boolean hasPlayerData(UUID uuid) {
        String id = uuid.toString().replace("-", "");
        return playerDataCollection.find(BsonDocument.parse("{uuid: \"" + id + "\"}")).first() != null;
    }

    public List<PlayerData> all() {
        List<PlayerData> playerData = new ArrayList<>();
        for (PlayerData data : playerDataCollection.find()) {
            playerData.add(data);
        }
        return playerData;
    }

    public void deletePlayerData(UUID uuid) {
        playerDataCollection.deleteOne(BsonDocument.parse("{uuid: \"" + uuid + "\"}"));
    }

    public void close() {
        mongoClient.close();
    }
}
