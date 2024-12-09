package llc.redstone.redstonesmp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagParser;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import eu.pb4.styledchat.StyledChatEvents;
import eu.pb4.styledchat.StyledChatStyles;
import eu.pb4.styledchat.other.StyledChatSentMessage;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayerManager;
import io.github.apace100.origins.origin.OriginManager;
import io.github.apace100.origins.registry.ModComponents;
import llc.redstone.redstonesmp.command.*;
import llc.redstone.redstonesmp.database.MessageQueueCollection;
import llc.redstone.redstonesmp.database.OriginContinentCollection;
import llc.redstone.redstonesmp.database.PlayerDataCollection;
import llc.redstone.redstonesmp.database.PortalLocationCollection;
import llc.redstone.redstonesmp.database.schema.OriginContinents;
import llc.redstone.redstonesmp.database.schema.OriginRegion;
import llc.redstone.redstonesmp.database.schema.PortalLocation;
import llc.redstone.redstonesmp.database.schema.ServerMessage;
import llc.redstone.redstonesmp.factions.FactionManager;
import llc.redstone.redstonesmp.interfaces.IServerStatHandler;
import llc.redstone.redstonesmp.utils.ContinentMessageUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.kyrptonaught.customportalapi.util.SHOULDTP;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static llc.redstone.redstonesmp.utils.MessageUtils.formatMessage;

public class RedstoneSMP implements ModInitializer {
    public static Map<String, Vec3d> originsLocations = new HashMap<>();

    public static JsonObject config = new JsonObject();

    public static PlayerDataCollection playerDataCollection;
    public static PortalLocationCollection portalLocationCollection;
    public static OriginContinentCollection originContinentCollection;
    public static MessageQueueCollection messageQueueCollection;
    public static AtomicBoolean serverSwitch = new AtomicBoolean(false);


    public static HashMap<UUID, Boolean> frozenPlayers = new HashMap<>();

    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            File configFile = new File("config/smp_config.json");
            if (!configFile.exists()) {
                try {
                    Files.write(configFile.toPath(), getClass().getResourceAsStream("/config.json").readAllBytes());

                    config = new Gson().fromJson(Files.readString(configFile.toPath()), JsonObject.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    config = new Gson().fromJson(Files.readString(configFile.toPath()), JsonObject.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            playerDataCollection = new PlayerDataCollection();
            portalLocationCollection = new PortalLocationCollection();
            messageQueueCollection = new MessageQueueCollection();
            originContinentCollection = new OriginContinentCollection();

            FactionManager.register();

            // load originlocations.json
            File originsLocationsFile = new File("config/originlocations.json");
            if (originsLocationsFile.exists()) {
                try {
                    JsonObject originsLocationsJson = new Gson().fromJson(new String(Files.readAllBytes(originsLocationsFile.toPath())), JsonObject.class);
                    originsLocations = originsLocationsJson.entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> new Vec3d(entry.getValue().getAsJsonObject().get("x").getAsDouble(),
                                            entry.getValue().getAsJsonObject().get("y").getAsDouble(),
                                            entry.getValue().getAsJsonObject().get("z").getAsDouble())
                            ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            AtomicReference<SignedMessage> lastMessage = new AtomicReference<>(null);
            ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
                if (lastMessage.get() != null && lastMessage.get().equals(message)) {
                    return;
                }

                lastMessage.set(message);

                Text txt = StyledChatStyles.getChat(sender, message.getContent());

                messageQueueCollection.insertMessage(
                        new ServerMessage(
                                txt.getString(),
                                (sender.getServer().getOverworld().getSeed() == 27594263L) ? "smp" : "adventure"
                        )
                );
            });

            ServerTickEvents.START_SERVER_TICK.register((server) -> {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    if (frozenPlayers.containsKey(player.getUuid()) && server.getOverworld().getSeed() == 27594263L) {
                        BlockPos pos = server.getOverworld().getSpawnPos();
                        player.teleport(server.getOverworld(), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 45, -10);
                    }
                }

                ServerMessage message = messageQueueCollection.shift((server.getOverworld().getSeed() == 27594263L) ? "adventure" : "smp");
                if (message != null) {
                    server.getPlayerManager().getPlayerList().forEach(player -> {
                        player.sendMessage(Text.of(message.getMessage()), false);
                    });
                    server.sendMessage(Text.of(message.getMessage()));
                }
            });

            //events

            ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    updatePlayerData(player, null);
                }

                playerDataCollection.close();
                portalLocationCollection.close();
            });

            ServerPlayConnectionEvents.JOIN.register((ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) -> {
                if (playerDataCollection.hasPlayerData(handler.getPlayer().getUuid())) {
                    PlayerData playerDataE = playerDataCollection.getPlayerData(handler.getPlayer().getUuid());

                    //Server Switcher
                    if (playerDataE.isInAdventureServer() && server.getOverworld().getSeed() == 27594263L) {
                        ServerRedirect.sendTo(handler.getPlayer(), "adventure.redstone.llc");
                        serverSwitch.set(true);
                        return;
                    } else if (!playerDataE.isInAdventureServer() && server.getOverworld().getSeed() != 27594263L) {
                        serverSwitch.set(true);
                        handler.getPlayer().networkHandler.disconnect(Text.of("Join the server using smp.redstone.llc"));
                        return;
                    }

                    if (!playerDataE.selectedContinent && playerDataE.getOriginId() != null) {
                        ContinentMessageUtils.sendContinentMessage(handler.getPlayer(), playerDataE.getOriginId());
                        frozenPlayers.put(handler.getPlayer().getUuid(), true);
                    }

                    //Origin Sync
                    OriginComponent component = ModComponents.ORIGIN.get(handler.getPlayer());
                    component.getOrigins().values().stream().findFirst().ifPresent(origin -> {
                        if (origin.getId() != null && playerDataE.getOriginId() != null && playerDataE.getLayerId() != null) {
                            OriginLayer layer = OriginLayerManager.get(Identifier.of(playerDataE.getLayerId()));
                            Origin newOrigin = OriginManager.get(Identifier.of(playerDataE.getOriginId()));
                            if (layer != null && newOrigin != null) {
                                component.setOrigin(layer, newOrigin);
                            }
                        }
                    });

                    if (component.getOrigins().size() > 1) {
                        component.getOrigins().keySet().stream().skip(1).forEach(layer -> {
                            component.getOrigins().remove(layer);
                        });
                    }

                    if (playerDataE.regionName == null && playerDataE.selectedContinent) {
                        OriginContinents r = originContinentCollection.getOriginLocation(playerDataE.getOriginId());
                        if (r == null) return;
                        List<OriginRegion> regions = r.regions;
                        //random region
                        OriginRegion region = regions.get((int) (Math.random() * regions.size()));
                        playerDataE.regionName = region.name;
                        playerDataCollection.updatePlayerData(playerDataE);
                    }

                    //Player Inv, Stats, and TP
                    if (playerDataE.getPlayerNBT() != null) {
//                        NbtCompound nbtComponent = new NbtCompound();
//                        handler.getPlayer().writeNbt(nbtComponent);
                        NbtCompound newNbt;
                        try {
                            newNbt = StringNbtReader.parse(playerDataE.getPlayerNBT());
                        } catch (CommandSyntaxException e) {
                            throw new RuntimeException(e);
                        }

                        handler.getPlayer().readNbt(newNbt);
                    }

                    if (playerDataE.getPlayerStats() != null) {
                        ((IServerStatHandler) handler.getPlayer().getStatHandler()).writeStatData(playerDataE.getPlayerStats());
                    }

                    if (playerDataE.getTpCoords() != null) {
                        String[] coords = playerDataE.getTpCoords().split(",");
                        handler.getPlayer().teleport(server.getOverworld(), Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2]), 0, 0);

                        playerDataE.setTpCoords(null);
                        playerDataCollection.updatePlayerData(playerDataE);
                    }
                } else {
                    //If player is new to the server
                    NbtCompound nbtComponent = new NbtCompound();
                    handler.getPlayer().writeNbt(nbtComponent);
                    String playerNBT = nbtComponent.toString();

                    String playerStats = ((IServerStatHandler) handler.getPlayer().getStatHandler()).readStatData();


                    playerDataCollection.insertPlayerData(
                            new PlayerData(
                                    handler.getPlayer().getUuid().toString().replace("-", ""),
                                    null, null, playerNBT, playerStats, null, false, false
                            )
                    );
                }
            });

            ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
                if (!playerDataCollection.hasPlayerData(newPlayer.getUuid())) return;
                PlayerData playerDataE = playerDataCollection.getPlayerData(newPlayer.getUuid());
                if (!playerDataE.isInAdventureServer()) {
                    BlockPos pos = newPlayer.getSpawnPointPosition();
                    if (pos != null) {
                        BlockState blockState = newPlayer.getServerWorld().getBlockState(pos);
                        Block block = blockState.getBlock();
                        if (block instanceof BedBlock) {
                            System.out.println("Bed");
                            return;
                        }
                    }
                    if (playerDataE.selectedContinent && playerDataE.regionName != null) {
                        OriginRegion region = originContinentCollection.getOriginLocation(playerDataE.getOriginId()).getRegion(playerDataE.regionName);
                        region.teleportPlayer(newPlayer);
                    } else {
                        ContinentMessageUtils.sendContinentMessage(newPlayer, playerDataE.getOriginId());
                        frozenPlayers.put(newPlayer.getUuid(), true);
                    }
                }
            });

            AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
                if (frozenPlayers.containsKey(player.getUuid())) {
                    return ActionResult.FAIL;
                }
                return ActionResult.PASS;
            });

            ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
                return !(entity instanceof ServerPlayerEntity player) || !frozenPlayers.containsKey(player.getUuid());
            });


            ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
                if (serverSwitch.get()) { //We need this to make sure it doesnt update the player data when the player is switching servers
                    serverSwitch.set(false);
                    return;
                }
                updatePlayerData(handler.player, null);
            });

            CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, env) -> {
                SwitchServerCommand.register(dispatcher);
                CreateRegionCommand.register(dispatcher);
                RegionSelectCommand.register(dispatcher);
                UnFreezeCommand.register(dispatcher);
            });
            new LocalChat();
            new FactionChat();
            new ChatCommand();
        }

        new ServerRedirect().onInitialize();

        CustomPortalBuilder.beginPortal()
                .frameBlock(Blocks.BEDROCK)
                .lightWithItem(Items.DIAMOND)
                .destDimID(Identifier.of("redstone:adventure")) //Not needed, but library requires it
                .tintColor(0, 255, 255)
                .flatPortal()
                .registerBeforeTPEvent((player) -> {
                    if (!(player instanceof ServerPlayerEntity playerEntity)) return SHOULDTP.CANCEL_TP;
                    if (!playerDataCollection.hasPlayerData(player.getUuid())) return SHOULDTP.CANCEL_TP;

                    PlayerData playerDataE = playerDataCollection.getPlayerData(player.getUuid());
                    if (playerDataE.isInAdventureServer()) {
                        //From B to A aka Adventure to SMP
                        for (PortalLocation loc : portalLocationCollection.getPortalLocations()) {
                            if (loc.isInsideB(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ())) {
                                Vec3d pos = loc.centerA();
                                playerDataE.setTpCoords(pos.getX() + "," + pos.getY() + "," + pos.getZ());
                                playerDataCollection.updatePlayerData(playerDataE);
                                break;
                            }
                        }

                        ServerRedirect.sendTo(playerEntity, "smp.redstone.llc");

                        playerDataE.setInAdventureServer(false);
                        playerDataCollection.updatePlayerData(playerDataE);
                    } else {
                        //From A to B aka SMP to Adventure
                        for (PortalLocation loc : portalLocationCollection.getPortalLocations()) {
                            if (loc.isInsideA(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ())) {
                                Vec3d pos = loc.centerB();
                                playerDataE.setTpCoords(pos.getX() + "," + pos.getY() + "," + pos.getZ());
                                playerDataCollection.updatePlayerData(playerDataE);
                                break;
                            }
                        }

                        ServerRedirect.sendTo(playerEntity, "adventure.redstone.llc");

                        playerDataE.setInAdventureServer(true);
                        playerDataCollection.updatePlayerData(playerDataE);
                    }
                    return SHOULDTP.CANCEL_TP;
                })
                .registerPortal();
    }

    public static void updatePlayerData(ServerPlayerEntity player, PlayerData playerData) {
        PlayerData playerDataE = playerData == null ? playerDataCollection.getPlayerData(player.getUuid()) : playerData;
        NbtCompound nbtComponent = new NbtCompound();
        player.writeNbt(nbtComponent);
        String playerNBT = nbtComponent.toString();
        playerDataE.setPlayerNBT(playerNBT);

        String playerStats = ((IServerStatHandler) player.getStatHandler()).readStatData();
        playerDataE.setPlayerStats(playerStats);

        //origin
        OriginComponent component = ModComponents.ORIGIN.get(player);
        component.getOrigins().entrySet().stream().findFirst().ifPresent((entry) -> {
            Origin origin = entry.getValue();
            OriginLayer layer = entry.getKey();
            playerDataE.setOriginId(origin.getId().toString());
            playerDataE.setLayerId(layer.getId().toString());
        });

        playerDataCollection.updatePlayerData(playerDataE);
    }

    public static Map<String, Vec3d> getOriginsLocations() {
        return originsLocations;
    }
}
