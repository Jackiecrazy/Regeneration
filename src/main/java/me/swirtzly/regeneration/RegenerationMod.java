package me.swirtzly.regeneration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.swirtzly.regeneration.client.gui.GuiHandler;
import me.swirtzly.regeneration.common.RegenPermission;
import me.swirtzly.regeneration.common.advancements.RegenTriggers;
import me.swirtzly.regeneration.common.capability.IRegeneration;
import me.swirtzly.regeneration.common.capability.RegenCap;
import me.swirtzly.regeneration.common.capability.RegenerationStorage;
import me.swirtzly.regeneration.common.commands.RegenDebugCommand;
import me.swirtzly.regeneration.common.tiles.TileEntityHandInJar;
import me.swirtzly.regeneration.common.traits.DnaHandler;
import me.swirtzly.regeneration.common.types.TypeHandler;
import me.swirtzly.regeneration.handlers.ActingForwarder;
import me.swirtzly.regeneration.network.NetworkHandler;
import me.swirtzly.regeneration.proxy.CommonProxy;
import me.swirtzly.regeneration.util.PlayerUtil;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RegenerationMod.MODID)
public class RegenerationMod {

    public static final String MODID = "regeneration";
    public static final String NAME = "Regeneration";
    public static final String VERSION = "2.0.7";

    public static final ResourceLocation LOOT_FILE = new ResourceLocation(MODID, "fob_watch_loot");

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Mod.Instance(MODID)
    public static RegenerationMod INSTANCE;

    public static Logger LOG = LogManager.getLogger(NAME);

    @SidedProxy(clientSide = "me.swirtzly.regeneration.proxy.ClientProxy", serverSide = "me.swirtzly.regeneration.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static boolean isDevEnv() {
        return (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
        CapabilityManager.INSTANCE.register(IRegeneration.class, new RegenerationStorage(), RegenCap::new);

        ActingForwarder.init();
        RegenTriggers.init();
        GameRegistry.registerTileEntity(TileEntityHandInJar.class, new ResourceLocation(MODID, "handinjar"));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
        NetworkHandler.init();
        LootTables.register(LOOT_FILE);
        DnaHandler.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiHandler());
        TypeHandler.init();
        RegenPermission.registerPermissions();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit();
        PlayerUtil.createPostList();
    }

    @EventHandler
    public void serverStart(FMLServerStartedEvent event) {
        event.registerServerCommand(new RegenDebugCommand());
    }


}
