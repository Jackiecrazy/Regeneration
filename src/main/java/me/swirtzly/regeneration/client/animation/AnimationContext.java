package me.swirtzly.regeneration.client.animation;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.PlayerEntity;

public class AnimationContext {

    private float limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch;
    private ModelBiped modelBiped;
    private PlayerEntity entityPlayer;

    public AnimationContext(ModelBiped model, PlayerEntity player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.modelBiped = model;
        this.entityPlayer = player;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
    }

    public PlayerEntity getEntityPlayer() {
        return entityPlayer;
    }

    public ModelBiped getModelBiped() {
        return modelBiped;
    }

    public float getAgeInTicks() {
        return ageInTicks;
    }

    public float getHeadPitch() {
        return headPitch;
    }

    public float getLimbSwing() {
        return limbSwing;
    }

    public float getLimbSwingAmount() {
        return limbSwingAmount;
    }

    public float getNetHeadYaw() {
        return netHeadYaw;
    }

}
