package me.swirtzly.regeneration.network.messages;

import me.swirtzly.regeneration.common.capability.IRegen;
import me.swirtzly.regeneration.common.capability.RegenCap;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.function.Supplier;

public class SyncDataMessage {

	private Entity player;
	private DimensionType dimensionType;

	public SyncDataMessage(Entity player) {
		this.player = player;
		this.dimensionType = player.world.dimension.getType();
	}
	
	public static void encode(SyncDataMessage message, PacketBuffer packetBuffer) {
		packetBuffer.writeInt(message.player.getEntityId());
		packetBuffer.writeResourceLocation(message.dimensionType.getRegistryName());

	}
	
	public static SyncDataMessage decode(PacketBuffer buffer) {
		int entityID = buffer.readInt();
		DimensionType type = DimensionType.byName(buffer.readResourceLocation());
		return new SyncDataMessage(ServerLifecycleHooks.getCurrentServer().getWorld(type).getEntityByID(entityID));
	}
	
	public static class Handler {
		public static void handle(SyncDataMessage message, Supplier<NetworkEvent.Context> ctx) {
			Entity player = message.player;
            ctx.get().getSender().getServer().deferTask(() -> {
				if (player != null) {
                    RegenCap.get(player).ifPresent(IRegen::synchronise);
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}
	
}
