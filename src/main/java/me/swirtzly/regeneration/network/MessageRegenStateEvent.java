package me.swirtzly.regeneration.network;

import io.netty.buffer.ByteBuf;
import me.swirtzly.regeneration.common.capability.CapabilityRegeneration;
import me.swirtzly.regeneration.handlers.ActingForwarder;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessageRegenStateEvent implements IMessage {

    private PlayerEntity player;
    private String event;

    public MessageRegenStateEvent() {
    }

    public MessageRegenStateEvent(PlayerEntity player, String event) {
        this.player = player;
        this.event = event;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, player.getGameProfile().getId().toString());
        ByteBufUtils.writeUTF8String(buf, event);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if (Minecraft.getInstance().player == null) return;
        player = Minecraft.getInstance().player.world.getPlayerEntityByUUID(UUID.fromString(ByteBufUtils.readUTF8String(buf)));
        event = ByteBufUtils.readUTF8String(buf);
    }

    public static class Handler implements IMessageHandler<MessageRegenStateEvent, IMessage> {

        @Override
        public IMessage onMessage(MessageRegenStateEvent message, MessageContext ctx) {
            Minecraft.getInstance().addScheduledTask(() -> ActingForwarder.onClient(ActingForwarder.RegenEvent.valueOf(message.event), CapabilityRegeneration.getForPlayer(message.player)));
            return null;
        }
    }
	
}
