package llc.redstone.redstonesmp.database;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import llc.redstone.redstonesmp.PlayerData;
import llc.redstone.redstonesmp.RedstoneSMP;
import llc.redstone.redstonesmp.database.schema.ServerMessage;
import org.apache.logging.log4j.core.jmx.Server;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.UUID;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MessageQueueCollection {
    private MongoClient mongoClient;
    private MongoCollection<ServerMessage> playerDataCollection;
    public MessageQueueCollection() {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(ServerMessage.class).automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

        String uri = RedstoneSMP.config.get("mongo-uri").getAsString();
        mongoClient = MongoClients.create(uri);
        try {
            playerDataCollection = mongoClient.getDatabase("smp")
                    .withCodecRegistry(pojoCodecRegistry)
                    .getCollection("queue", ServerMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertMessage(ServerMessage serverMessage) {
        playerDataCollection.insertOne(serverMessage);
    }

    public ServerMessage shift(String server) {
        return playerDataCollection.findOneAndDelete(BsonDocument.parse("{server: \"" + server + "\"}"));
    }

    public void close() {
        mongoClient.close();
    }
}
