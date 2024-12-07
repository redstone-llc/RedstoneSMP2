package llc.redstone.redstonesmp.database;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import llc.redstone.redstonesmp.RedstoneSMP;
import llc.redstone.redstonesmp.database.schema.PortalLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class PortalLocationCollection {
    private MongoClient mongoClient;
    private MongoCollection<PortalLocation> portalLocationCollection;

    public PortalLocationCollection() {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(PortalLocation.class).automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

        String uri = RedstoneSMP.config.get("mongo-uri").getAsString();
        mongoClient = MongoClients.create(uri);
        try {
            portalLocationCollection = mongoClient.getDatabase("smp")
                    .withCodecRegistry(pojoCodecRegistry)
                    .getCollection("portalLocations", PortalLocation.class);

            if (!hasPortalLocation("spawn")) {
                PortalLocation spawn = new PortalLocation("spawn",
                        2301, 63, -2302, 2306, 63, -2307,
                        2301, 83, -2302, 2306, 83, -2307
                );
                insertPortalLocation(spawn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertPortalLocation(PortalLocation portalLocation) {
        portalLocationCollection.insertOne(portalLocation);
    }

    public void updatePortalLocation(PortalLocation portalLocation) {
        portalLocationCollection.replaceOne(BsonDocument.parse("{name: \"" + portalLocation.getName() + "\"}"), portalLocation);
    }

    public boolean hasPortalLocation(String name) {
        return portalLocationCollection.find(BsonDocument.parse("{name: \"" + name + "\"}")).first() != null;
    }

    public PortalLocation getPortalLocation(String name) {
        return portalLocationCollection.find(BsonDocument.parse("{name: \"" + name + "\"}")).first();
    }

    public List<PortalLocation> getPortalLocations() {
        List<PortalLocation> portalLocations = new ArrayList<>();
        portalLocationCollection.find().forEach((Consumer<? super PortalLocation>) (portalLocations::add));
        return portalLocations;
    }

    public PortalLocation getPortalInsideA(double x, double y, double z) {
        for (PortalLocation portalLocation : portalLocationCollection.find()) {
            if (portalLocation.isInsideA(x, y, z)) {
                return portalLocation;
            }
        }
        return null;
    }

    public PortalLocation getPortalInsideB(double x, double y, double z) {
        for (PortalLocation portalLocation : portalLocationCollection.find()) {
            if (portalLocation.isInsideB(x, y, z)) {
                return portalLocation;
            }
        }
        return null;
    }

    public PortalLocation getPortalInsideA(Vec3d pos) {
        return getPortalInsideA(pos.getX(), pos.getY(), pos.getZ());
    }

    public PortalLocation getPortalInsideB(Vec3d pos) {
        return getPortalInsideB(pos.getX(), pos.getY(), pos.getZ());
    }

    public PortalLocation getPortalInside(Vec3d pos) {
        PortalLocation portalLocation = getPortalInsideA(pos);
        if (portalLocation == null) {
            portalLocation = getPortalInsideB(pos);
        }
        return portalLocation;
    }

    public void close() {
        mongoClient.close();
    }
}
