package llc.redstone.redstonesmp.networking.s2c;

import io.github.apace100.origins.Origins;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record OpenChooseContinentScreenS2CPacket(boolean showBackground) implements CustomPayload {

    public static final Id<OpenChooseContinentScreenS2CPacket> PACKET_ID = new Id<>(Origins.identifier("s2c/open_continent_screen"));
    public static final PacketCodec<ByteBuf, OpenChooseContinentScreenS2CPacket> PACKET_CODEC = PacketCodecs.BOOL.xmap(OpenChooseContinentScreenS2CPacket::new, OpenChooseContinentScreenS2CPacket::showBackground);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
