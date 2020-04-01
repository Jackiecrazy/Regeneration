package me.swirtzly.regeneration.client.rendering.types;

import com.mojang.blaze3d.platform.GlStateManager;
import me.swirtzly.regeneration.client.animation.AnimationContext;
import me.swirtzly.regeneration.client.animation.RenderCallbackEvent;
import me.swirtzly.regeneration.common.capability.IRegeneration;
import me.swirtzly.regeneration.common.types.TypeLayFade;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.HandSide;
import net.minecraftforge.client.event.RenderPlayerEvent;

import static me.swirtzly.regeneration.client.animation.AnimationHandler.copyAndReturn;
import static me.swirtzly.regeneration.client.rendering.types.TypeFieryRenderer.renderOverlay;

public class TypeLayFadeRenderer extends ATypeRenderer<TypeLayFade> {

    public static final TypeLayFadeRenderer INSTANCE = new TypeLayFadeRenderer();

    private TypeLayFadeRenderer() {
    }

    @Override
    protected void onRenderRegeneratingPre(TypeLayFade type, RenderPlayerEvent.Pre event, IRegeneration capability) {

    }

    @Override
    protected void onRenderRegeneratingPost(TypeLayFade type, RenderPlayerEvent.Post event, IRegeneration capability) {

    }

    @Override
    protected void onRenderLayer(TypeLayFade type, LivingRenderer<?> renderLivingBase, IRegeneration capability, PlayerEntity entityPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        renderOverlay(entityPlayer, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, null);
    }

    @Override
    public boolean onAnimateRegen(AnimationContext animationContext) {
        BipedModel BipedModel = animationContext.getBipedModel();

        BipedModel.bipedHead.rotateAngleX = (float) Math.toRadians(0);
        BipedModel.bipedHead.rotateAngleY = (float) Math.toRadians(0);
        BipedModel.bipedHead.rotateAngleZ = (float) Math.toRadians(0);

        BipedModel.bipedLeftLeg.rotateAngleZ = (float) -Math.toRadians(5);
        BipedModel.bipedRightLeg.rotateAngleZ = (float) Math.toRadians(5);

        BipedModel.bipedLeftArm.rotateAngleZ = (float) -Math.toRadians(5);
        BipedModel.bipedRightArm.rotateAngleZ = (float) Math.toRadians(5);
        return copyAndReturn(BipedModel, true);
    }

    @Override
    public void renderHand(PlayerEntity player, HandSide handSide, LivingRenderer<?> render) {

    }

    @Override
    public void onRenderCallBack(RenderCallbackEvent event) {
        GlStateManager.rotate(-90, 1, 0, 0);
        GlStateManager.translate(0, 1, 0);
    }
	
}
