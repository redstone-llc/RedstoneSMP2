package llc.redstone.redstonesmp.networking.c2s;

import io.github.apace100.origins.Origins;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ChooseContinentC2SPacket(Identifier continentId) implements CustomPayload {

    public static final Id<ChooseContinentC2SPacket> PACKET_ID = new Id<>(Origins.identifier("c2s/choose_continent"));
    public static final PacketCodec<ByteBuf, ChooseContinentC2SPacket> PACKET_CODEC = PacketCodec.tuple(
        Identifier.PACKET_CODEC, ChooseContinentC2SPacket::continentId,
        ChooseContinentC2SPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
