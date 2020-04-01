package me.swirtzly.regeneration.network;

import me.swirtzly.regeneration.RegenerationMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

/**
 * Created by Sub on 16/09/2018.
 */
public class NetworkHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(RegenerationMod.MODID);

    public static void init() {
        INSTANCE.registerMessage(MessageSaveStyle.Handler.class, MessageSaveStyle.class, 0, Dist.DEDICATED_SERVER);
        INSTANCE.registerMessage(MessageSetPerspective.Handler.class, MessageSetPerspective.class, 1, Dist.CLIENT);
        INSTANCE.registerMessage(MessageRegenStateEvent.Handler.class, MessageRegenStateEvent.class, 2, Dist.CLIENT);
        INSTANCE.registerMessage(MessageTriggerRegeneration.Handler.class, MessageTriggerRegeneration.class, 3, Dist.DEDICATED_SERVER);
        INSTANCE.registerMessage(MessageSynchronisationRequest.Handler.class, MessageSynchronisationRequest.class, 4, Dist.DEDICATED_SERVER);
        INSTANCE.registerMessage(MessageSynchroniseRegeneration.Handler.class, MessageSynchroniseRegeneration.class, 5, Dist.CLIENT);
        INSTANCE.registerMessage(MessagetickSkin.Handler.class, MessagetickSkin.class, 6, Dist.DEDICATED_SERVER);
        INSTANCE.registerMessage(MessageRemovePlayer.Handler.class, MessageRemovePlayer.class, 7, Dist.CLIENT);
        INSTANCE.registerMessage(MessagePlayRegenerationSound.Handler.class, MessagePlayRegenerationSound.class, 8, Dist.CLIENT);
        INSTANCE.registerMessage(MessagetickModel.Handler.class, MessagetickModel.class, 9, Dist.DEDICATED_SERVER);
        INSTANCE.registerMessage(MessageTriggerForcedRegen.Handler.class, MessageTriggerForcedRegen.class, 10, Dist.DEDICATED_SERVER);
        INSTANCE.registerMessage(MessageNextSkin.Handler.class, MessageNextSkin.class, 11, Dist.DEDICATED_SERVER);
        INSTANCE.registerMessage(MessageChangeType.Handler.class, MessageChangeType.class, 12, Dist.DEDICATED_SERVER);
        INSTANCE.registerMessage(MessageOpenArch.Handler.class, MessageOpenArch.class, 13, Dist.DEDICATED_SERVER);
        INSTANCE.registerMessage(MessageUseArch.Handler.class, MessageUseArch.class, 14, Dist.DEDICATED_SERVER);
    }
	
}
