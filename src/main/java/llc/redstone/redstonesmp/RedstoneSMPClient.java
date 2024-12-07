package llc.redstone.redstonesmp;

import net.fabricmc.api.ClientModInitializer;

public class RedstoneSMPClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new ClientServerRedirect().onInitialize();
    }
}
