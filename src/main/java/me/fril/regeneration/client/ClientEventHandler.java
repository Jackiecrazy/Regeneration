package me.fril.regeneration.client;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import me.fril.regeneration.RegenerationMod;
import me.fril.regeneration.common.capability.CapabilityRegeneration;
import me.fril.regeneration.common.capability.IRegeneration;
import me.fril.regeneration.handlers.RegenObjects;
import me.fril.regeneration.network.MessageTriggerRegeneration;
import me.fril.regeneration.network.NetworkHandler;
import me.fril.regeneration.util.RegenState;
import me.fril.regeneration.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by Sub
 * on 16/09/2018.
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RegenerationMod.MODID)
public class ClientEventHandler {

	@SubscribeEvent
	public static void onRenderHand(RenderHandEvent e) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		float f = 0.2F;
		
		if (player.getHeldItemMainhand().getItem() != Items.AIR || mc.gameSettings.thirdPersonView > 0)
			return;
		
		IRegeneration cap = CapabilityRegeneration.getForPlayer(player);
		if (!cap.getState().isGraceful())
			return;
		
		
		GlStateManager.pushMatrix();
		
		float leftHandedFactor = mc.gameSettings.mainHand.equals(EnumHandSide.RIGHT) ? 1 : -1;
		GlStateManager.translate(0.33F*leftHandedFactor, -0.23F, -0.5F); //move in place
		GlStateManager.translate(-.8F*player.swingProgress*leftHandedFactor, -.8F*player.swingProgress, -.4F*player.swingProgress); //compensate for 'punching' motion
		GlStateManager.translate(-(player.renderArmYaw - player.prevRenderArmYaw)/400F, (player.renderArmPitch - player.prevRenderArmPitch)/500F, 0); //compensate for 'swinging' motion
		
		RenderUtil.setupRenderLightning();
		GlStateManager.rotate((mc.player.ticksExisted + RenderUtil.renderTick) / 2F, 0, 1, 0);
		for (int i = 0; i < 15; i++) {
			GlStateManager.rotate((mc.player.ticksExisted + RenderUtil.renderTick) * i / 70F, 1, 1, 0);
			Vec3d primaryColor = cap.getPrimaryColor();
			
			Random rand = player.world.rand;
			RenderUtil.drawGlowingLine(new Vec3d((-f / 2F) + rand.nextFloat() * f, (-f / 2F) + rand.nextFloat() * f, (-f / 2F) + rand.nextFloat() * f), new Vec3d((-f / 2F) + rand.nextFloat() * f, (-f / 2F) + rand.nextFloat() * f, (-f / 2F) + rand.nextFloat() * f), 0.1F, primaryColor, 0);
		}
		RenderUtil.finishRenderLightning();
		
		GlStateManager.popMatrix();
	}
	
	
	
	@SuppressWarnings("incomplete-switch")
	@SubscribeEvent
	public static void onRenderGui(RenderGameOverlayEvent.Post event) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.ALL)
			return;
		
		IRegeneration cap = CapabilityRegeneration.getForPlayer(Minecraft.getMinecraft().player);
		
		switch (cap.getState()) {
			case GRACE:
                renderVignette(cap.getPrimaryColor(), 0.3F, cap.getState());
				break;
				
			case GRACE_CRIT:
                renderVignette(new Vec3d(1, 0, 0), 0.5F, cap.getState());
				break;
				
			case REGENERATING:
                renderVignette(cap.getSecondaryColor(), 0.5F, cap.getState());
				break;
		}
	}
	
	private static final ResourceLocation VIGNETTE_TEX_PATH = new ResourceLocation(RegenerationMod.MODID + ":" + "textures/misc/vignette.png");

    private static void renderVignette(Vec3d color, float a, RegenState state) {
		GlStateManager.color((float)color.x, (float)color.y, (float)color.z, a);
		GlStateManager.disableAlpha();
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(VIGNETTE_TEX_PATH);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		
		ScaledResolution scaledRes = new ScaledResolution(Minecraft.getMinecraft());
		int z = -91; //below the HUD
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(0, scaledRes.getScaledHeight(), z).tex(0, 1).endVertex();
		bufferbuilder.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), z).tex(1.0D, 1.0D).endVertex();
		bufferbuilder.pos(scaledRes.getScaledWidth(), 0, z).tex(1, 0).endVertex();
		bufferbuilder.pos(0, 0, z).tex(0, 0).endVertex();
		tessellator.draw();

        if (!Loader.isModLoaded("lucraftcore")) {
            String warning = "";
            switch (state) {
                case GRACE:
                    warning = new TextComponentTranslation("regeneration.messages.warning.grace", RegenKeyBinds.REGEN_NOW.getDisplayName()).getUnformattedText();
                    break;
                case GRACE_CRIT:
                    warning = new TextComponentTranslation("regeneration.messages.warning.grace_critical", RegenKeyBinds.REGEN_NOW.getDisplayName()).getUnformattedText();
                    break;
                case ALIVE:
                    break;
                case REGENERATING:
                    break;
            }
            Minecraft.getMinecraft().fontRenderer.drawString(warning, scaledRes.getScaledWidth() / 2 - 135, scaledRes.getScaledHeight() / 2 - 115, Color.WHITE.getRGB());
        }

		GlStateManager.depthMask(true);
		GlStateManager.enableAlpha();
		GlStateManager.color(1, 1, 1, 1);
	}
	
	
	
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent e) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null || Minecraft.getMinecraft().world == null)
			return;
		
		if (RegenKeyBinds.REGEN_NOW.isPressed() && CapabilityRegeneration.getForPlayer(player).getState().isGraceful()) {
			NetworkHandler.INSTANCE.sendToServer(new MessageTriggerRegeneration(player));
		}
	}
	
	@SubscribeEvent
	public static void onRenderPlayerPre(RenderPlayerEvent.Pre e) {
		EntityPlayer player = e.getEntityPlayer();
		IRegeneration cap = CapabilityRegeneration.getForPlayer(player);
		
		if (cap.getState() == RegenState.REGENERATING) {
			cap.getType().getRenderer().onRenderRegeneratingPlayerPre(cap.getType(), e, cap);
		}
	}
	
	@SubscribeEvent
	public static void keyInput(InputUpdateEvent e) {
		if (Minecraft.getMinecraft().player == null) return;
		
		IRegeneration cap = CapabilityRegeneration.getForPlayer(Minecraft.getMinecraft().player);
		if (cap.getState() == RegenState.REGENERATING) { //locking user
			MovementInput moveType = e.getMovementInput();
			moveType.rightKeyDown = false;
			moveType.leftKeyDown = false;
			moveType.backKeyDown = false;
			moveType.jump = false;
			moveType.moveForward = 0.0F;
			moveType.sneak = false;
			moveType.moveStrafe = 0.0F;
		}
	}
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent ev) {
		RegenObjects.ITEMS.forEach(RenderUtil::setItemRender);
		RegenObjects.ITEMS = new ArrayList<>();
	}
	
}
