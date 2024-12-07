package llc.redstone.redstonesmp.database;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import llc.redstone.redstonesmp.RedstoneSMP;
import llc.redstone.redstonesmp.database.schema.OriginContinents;
import llc.redstone.redstonesmp.database.schema.PortalLocation;
import net.minecraft.util.math.Vec3d;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class OriginContinentCollection {
    private MongoClient mongoClient;
    private MongoCollection<OriginContinents> portalLocationCollection;

    public OriginContinentCollection() {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(OriginContinents.class).automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

        String uri = RedstoneSMP.config.get("mongo-uri").getAsString();
        mongoClient = MongoClients.create(uri);
        try {
            portalLocationCollection = mongoClient.getDatabase("smp")
                    .withCodecRegistry(pojoCodecRegistry)
                    .getCollection("originContinents", OriginContinents.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertOriginLocation(OriginContinents portalLocation) {
        portalLocationCollection.insertOne(portalLocation);
    }

    public void updateOriginLocation(OriginContinents portalLocation) {
        portalLocationCollection.replaceOne(BsonDocument.parse("{originId: \"" + portalLocation.originId + "\"}"), portalLocation);
    }

    public boolean hasOriginLocation(String originId) {
        return portalLocationCollection.find(BsonDocument.parse("{originId: \"" + originId + "\"}")).first() != null;
    }

    public OriginContinents getOriginLocation(String originId) {
        return portalLocationCollection.find(BsonDocument.parse("{originId: \"" + originId + "\"}")).first();
    }

    public void close() {
        mongoClient.close();
    }
}
