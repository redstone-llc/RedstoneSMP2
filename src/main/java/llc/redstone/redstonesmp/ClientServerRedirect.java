package llc.redstone.redstonesmp;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import static llc.redstone.redstonesmp.ServerRedirect.*;

public class ClientServerRedirect {
    public static final Logger LOGGER = LogManager.getLogger("serverredirect");
    public static final Pattern ADDRESS_PREVALIDATOR = Pattern.compile("^[A-Za-z0-9-_.:]+$"); // allowed characters in a server address
    protected static final Set<UUID> players = Collections.synchronizedSet(new HashSet<>());

    @Environment(EnvType.CLIENT)
    public static volatile String fallbackServerAddress;
    @Environment(EnvType.CLIENT)
    public static boolean connected;

    public void onInitialize() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {

            ClientPlayNetworking.registerGlobalReceiver(RedirectAddressPayload.PACKET_ID, (payload, context) -> {
                try {
                    String addr = payload.addr();
                    if (ADDRESS_PREVALIDATOR.matcher(addr).matches()) {
                        context.client().executeSync(() -> {
                            try {
                                redirect(addr);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            ClientPlayNetworking.registerGlobalReceiver(FallbackAddressPayload.PACKET_ID, (payload, context) -> {
                try {
                    String addr = payload.addr();
                    if (ADDRESS_PREVALIDATOR.matcher(addr).matches()) {
                        if (ClientFallbackEvent.EVENT.invoker().fallback(addr) != ActionResult.SUCCESS) {
                            return;
                        }

                        fallbackServerAddress = addr;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            ClientTickEvents.START_CLIENT_TICK.register(c -> {
                try {
                    if (connected == (c.world == null)) {
                        connected = c.world != null;
                        if (connected) {
                            ClientPlayNetworking.send(new AnnounceAddressPayload());
                        }
                    } else if (fallbackServerAddress != null) {
                        if (c.currentScreen instanceof DisconnectedScreen) {
                            String addr = fallbackServerAddress;
                            fallbackServerAddress = null;
                            redirect(addr);
                        } else if (c.currentScreen instanceof TitleScreen || c.currentScreen instanceof MultiplayerScreen) {
                            fallbackServerAddress = null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Processes the redirect client side.<br>
     * This simulates clicking the disconnect button and a direct connection to the specified server address.
     *
     * @param serverAddress the new server address this client should connect to
     * @throws IllegalStateException if called while not in the main thread
     */
    @Environment(EnvType.CLIENT)
    public static void redirect(String serverAddress) {
        if (!MinecraftClient.getInstance().isOnThread()) {
            throw new IllegalStateException("Not in the main thread");
        }

        if (ClientRedirectEvent.EVENT.invoker().redirect(serverAddress) != ActionResult.SUCCESS) {
            return;
        }

        LOGGER.info("Connecting to {}", serverAddress);
        final MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            client.world.disconnect();
        }

        client.disconnect();

        client.setScreen(new MultiplayerScreen(new TitleScreen()));
        ConnectScreen.connect(client.currentScreen, client, ServerAddress.parse(serverAddress), new ServerInfo(serverAddress, serverAddress, ServerInfo.ServerType.OTHER), false, null);
    }

    /**
     * <b>WARNING:</b> this will likely return false for a player that just logged in,
     * as it takes some time for the client to send the announce packet to the server.
     *
     * @param player the player to check
     * @return whether the specified player is using Server Redirect
     */
    public static boolean isUsingServerRedirect(ServerPlayerEntity player) {
        return isUsingServerRedirect(player.getUuid());
    }

    /**
     * <b>WARNING:</b> this will likely return false for a player that just logged in,
     * as it takes some time for the client to send the announce packet to the server.
     *
     * @param playerId the player to check
     * @return whether the specified player is using Server Redirect
     */
    public static boolean isUsingServerRedirect(UUID playerId) {
        return players.contains(playerId);
    }

    @Environment(EnvType.CLIENT)
    public interface ClientRedirectEvent {
        Event<ClientRedirectEvent> EVENT = EventFactory.createArrayBacked(ClientRedirectEvent.class, listeners -> address -> {
            for (ClientRedirectEvent listener : listeners) {
                ActionResult result = listener.redirect(address);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }

            return ActionResult.SUCCESS;
        });

        ActionResult redirect(String address);
    }

    @Environment(EnvType.CLIENT)
    public interface ClientFallbackEvent {
        Event<ClientFallbackEvent> EVENT = EventFactory.createArrayBacked(ClientFallbackEvent.class, listeners -> address -> {
            for (ClientFallbackEvent listener : listeners) {
                ActionResult result = listener.fallback(address);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }

            return ActionResult.SUCCESS;
        });

        ActionResult fallback(String address);
    }
}