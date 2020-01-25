package me.swirtzly.animateme;

import me.swirtzly.regeneration.util.ClientUtil;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;

public class AMHooks {

    public static void renderBipedPre(BipedModel model, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw) {
        for (AnimationManager.IAnimate animation : AnimationManager.getAnimations()) {
            if (animation.useVanilla()) {
                model.setAngles(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw);
            } else {
                animation.preAnimation(model, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw);
            }
        }
    }

    public static void renderBipedPost(BipedModel model, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw) {
        for (AnimationManager.IAnimate animation : AnimationManager.getAnimations()) {
            animation.postAnimation(model, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw);
        }

        if (model instanceof PlayerModel) {
            ClientUtil.copyAnglesToWear((PlayerModel) model);
        }
    }


    public static void preRenderCallBack(LivingRenderer renderer, LivingEntity entity) {
        if (entity == null)
            return;
        for (AnimationManager.IAnimate animation : AnimationManager.getAnimations()) {
            animation.preRenderCallBack(renderer, entity);
        }
    }


}
