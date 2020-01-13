package me.swirtzly.regeneration.client.rendering.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.swirtzly.regeneration.client.rendering.types.TypeFieryRenderer;
import me.swirtzly.regeneration.common.capability.IRegeneration;
import me.swirtzly.regeneration.common.capability.RegenCap;
import me.swirtzly.regeneration.common.types.TypeHandler;
import me.swirtzly.regeneration.util.PlayerUtil;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.HandSide;

public class LayerHands implements LayerRenderer<PlayerEntity> {
    protected final LivingRenderer<?> livingEntityRenderer;

    public LayerHands(LivingRenderer<?> livingEntityRendererIn) {
        this.livingEntityRenderer = livingEntityRendererIn;
    }


    public void doRenderLayer(PlayerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();

        IRegeneration data = RegenCap.get(entitylivingbaseIn);

        if (this.livingEntityRenderer.getEntityModel().isChild) {
            RenderSystem.translatef(0.0F, 0.75F, 0.0F);
            RenderSystem.scaled(0.5F, 0.5F, 0.5F);
        }
        if (data.areHandsGlowing()) {
            renderHand(entitylivingbaseIn, HandSide.LEFT, EnumHandRenderType.GRACE);
            renderHand(entitylivingbaseIn, HandSide.RIGHT, EnumHandRenderType.GRACE);
        }

        if (data.getState() == PlayerUtil.RegenState.REGENERATING) {
            renderHand(entitylivingbaseIn, HandSide.LEFT, EnumHandRenderType.REGEN);
            renderHand(entitylivingbaseIn, HandSide.RIGHT, EnumHandRenderType.REGEN);
        }

        if (data.isSyncingToJar()) {
            renderHand(entitylivingbaseIn, HandSide.LEFT, EnumHandRenderType.JAR);
            renderHand(entitylivingbaseIn, HandSide.RIGHT, EnumHandRenderType.JAR);
        }

        GlStateManager.popMatrix();
    }

    public void renderHand(PlayerEntity player, HandSide handSide, EnumHandRenderType type) {
        GlStateManager.pushMatrix();

        IRegeneration data = RegenCap.get(player);

        if (player.isCrouching()) {
            RenderSystem.translatef(0.0F, 0.2F, 0.0F);
        }

        this.translateToHand(handSide);
        boolean flag = handSide == HandSide.LEFT;
        RenderSystem.translatef((float) (flag ? -1 : 1) / 25.0F, 0.125F, -0.625F);
        RenderSystem.translated(0, -0.050, 0.6);

        if (type == EnumHandRenderType.GRACE) {
            LayerRegeneration.renderGlowingHands(player, data, 1.5F, handSide);
        }

        if (type == EnumHandRenderType.REGEN) {
            TypeHandler.getTypeInstance(data.getType()).getRenderer().renderHand(player, handSide, livingEntityRenderer);
        }

        if (type == EnumHandRenderType.JAR) {
            TypeFieryRenderer.renderConeAtArms(player);
        }

        GlStateManager.popMatrix();
    }

    protected void translateToHand(HandSide handSide) {
        ((BipedModel) this.livingEntityRenderer.getEntityModel()).postRenderArm(0.0625F, handSide);
    }

    public boolean shouldCombineTextures() {
        return false;
    }

    public enum EnumHandRenderType {
        REGEN, GRACE, JAR
    }
}