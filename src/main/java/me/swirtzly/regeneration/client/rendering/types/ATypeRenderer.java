package me.swirtzly.regeneration.client.rendering.types;

import me.swirtzly.regeneration.client.animation.AnimationContext;
import me.swirtzly.regeneration.client.animation.RenderCallbackEvent;
import me.swirtzly.regeneration.common.capability.IRegeneration;
import me.swirtzly.regeneration.common.types.IRegenType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.HandSide;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;

public abstract class ATypeRenderer<T> {


    protected abstract void onRenderRegeneratingPre(T type, Pre event, IRegeneration capability);

    protected abstract void onRenderRegeneratingPost(T type, RenderPlayerEvent.Post event, IRegeneration capability);

    protected abstract void onRenderLayer(T type, LivingRenderer<?> renderLivingBase, IRegeneration capability, PlayerEntity entityPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale);

    // Generic casting convenience methods:

    @SuppressWarnings("unchecked")
    public final void onRenderRegeneratingPlayerPre(IRegenType<?> type, Pre event, IRegeneration capability) {
        try {
            onRenderRegeneratingPre((T) type, event, capability);
        } catch (ClassCastException e) {
            throw new IllegalStateException("RegenType <-> RegenRenderType mismatch", e);
        }
    }

    @SuppressWarnings("unchecked")
    public final void onRenderRegeneratingPlayerPost(IRegenType<?> type, RenderPlayerEvent.Post event, IRegeneration capability) {
        try {
            onRenderRegeneratingPost((T) type, event, capability);
        } catch (ClassCastException e) {
            throw new IllegalStateException("RegenType <-> RegenRenderType mismatch", e);
        }
    }

    @SuppressWarnings("unchecked")
    public final void onRenderRegenerationLayer(IRegenType<?> type, LivingRenderer<?> renderLivingBase, IRegeneration capability, PlayerEntity entityPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        try {
            onRenderLayer((T) type, renderLivingBase, capability, entityPlayer, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        } catch (ClassCastException e) {
            throw new IllegalStateException("RegenType <-> RegenRenderType mismatch", e);
        }
    }

    public abstract boolean onAnimateRegen(AnimationContext animationContext);

    public abstract void renderHand(PlayerEntity player, HandSide handSide, LivingRenderer<?> render);

    public abstract void onRenderCallBack(RenderCallbackEvent event);

}
