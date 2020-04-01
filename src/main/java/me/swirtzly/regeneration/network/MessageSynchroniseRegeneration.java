package me.swirtzly.regeneration.network;

import io.netty.buffer.ByteBuf;
import me.swirtzly.regeneration.common.capability.CapabilityRegeneration;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

/**
 * Created by Sub on 16/09/2018.
 */
public class MessageSynchroniseRegeneration implements IMessage {

    private PlayerEntity player;
    private CompoundNBT data;

    public MessageSynchroniseRegeneration() {
    }

    public MessageSynchroniseRegeneration(PlayerEntity player, CompoundNBT data) {
        this.player = player;
        this.data = data;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, player.getGameProfile().getId().toString());
        ByteBufUtils.writeTag(buf, data);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if (Minecraft.getInstance().player == null) return;
        player = Minecraft.getInstance().player.world.getPlayerEntityByUUID(UUID.fromString(ByteBufUtils.readUTF8String(buf)));
        data = ByteBufUtils.readTag(buf);
    }

    public static class Handler implements IMessageHandler<MessageSynchroniseRegeneration, IMessage> {

        @Override
        public IMessage onMessage(MessageSynchroniseRegeneration message, MessageContext ctx) {
            PlayerEntity player = message.player;
            if (player != null)
                Minecraft.getInstance().addScheduledTask(() -> CapabilityRegeneration.getForPlayer(player).deserializeNBT(message.data));
            return null;
        }
    }
	
}
