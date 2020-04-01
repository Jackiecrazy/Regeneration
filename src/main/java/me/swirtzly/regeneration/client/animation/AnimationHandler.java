package me.swirtzly.regeneration.client.animation;

import me.swirtzly.regeneration.common.capability.CapabilityRegeneration;
import me.swirtzly.regeneration.common.capability.IRegeneration;
import me.swirtzly.regeneration.common.item.ItemArchInterface;
import me.swirtzly.regeneration.common.item.ItemFobWatch;
import me.swirtzly.regeneration.util.ClientUtil;
import me.swirtzly.regeneration.util.PlayerUtil;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class AnimationHandler {

    public static boolean animate(AnimationContext animationContext) {
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) animationContext.getEntityPlayer();
        ItemStack stack = player.getHeldItemMainhand();
        ItemStack offStack = player.getHeldItemOffhand();
        BipedModel BipedModel = animationContext.getBipedModel();
        IRegeneration data = CapabilityRegeneration.getForPlayer(player);

        // ==============FOB WATCH & JAR START==============
        boolean isOpen;

        // MAINHAND
        if (stack.getItem() instanceof ItemFobWatch) {
            isOpen = ItemFobWatch.getOpen(stack) == 1;
            if (isOpen) {
                makeZombieArms(BipedModel);
                return copyAndReturn(BipedModel, true);
            }
        }

        // OFFHAND
        if (offStack.getItem() instanceof ItemFobWatch) {
            isOpen = ItemFobWatch.getOpen(stack) == 1;
            if (isOpen) {
                makeZombieArms(BipedModel);
                return copyAndReturn(BipedModel, true);
            }
        }
        // ==============FOB WATCH END==============

        // JAR SYNCING
        if (data.isSyncingToJar()) {

            double animationProgress = data.getAnimationTicks();
            float armRot = (float) animationProgress * 1.5F;

            if (armRot > 90) {
                armRot = 90;
            }

            BipedModel.bipedLeftArm.rotateAngleX = (float) -Math.toRadians(armRot);
            BipedModel.bipedRightArm.rotateAngleX = (float) -Math.toRadians(armRot);

            BipedModel.bipedBody.rotateAngleX = 0;
            BipedModel.bipedBody.rotateAngleY = 0;
            BipedModel.bipedBody.rotateAngleZ = 0;

            // Legs
            BipedModel.bipedLeftLeg.rotateAngleY = 0;
            BipedModel.bipedRightLeg.rotateAngleY = 0;
            BipedModel.bipedLeftLeg.rotateAngleX = 0;
            BipedModel.bipedRightLeg.rotateAngleX = 0;
            BipedModel.bipedLeftLeg.rotateAngleZ = (float) -Math.toRadians(5);
            BipedModel.bipedRightLeg.rotateAngleZ = (float) Math.toRadians(5);

            return copyAndReturn(BipedModel, true);
        }

        // STRUGGLE IN CRITICAL
        if (CapabilityRegeneration.getForPlayer(player).getState() == PlayerUtil.RegenState.GRACE_CRIT) {
            BipedModel.bipedBody.rotateAngleX = 0.5F;
            BipedModel.bipedRightArm.rotateAngleX = (float) Math.toRadians(-25);
            BipedModel.bipedRightArm.rotateAngleY = (float) Math.toRadians(-55);
            BipedModel.bipedLeftArm.rotateAngleX += 0.4F;
            BipedModel.bipedRightLeg.rotationPointZ = 4.0F;
            BipedModel.bipedLeftLeg.rotationPointZ = 4.0F;
            BipedModel.bipedRightLeg.rotationPointY = 9.0F;
            BipedModel.bipedLeftLeg.rotationPointY = 9.0F;
            BipedModel.bipedHead.rotationPointY = 1.0F;
            BipedModel.bipedHead.rotateAngleX = (float) Math.toRadians(45);
            return copyAndReturn(BipedModel, true);
        }

        if (player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() instanceof ItemArchInterface && player.world.isBlockPowered(player.getPosition())) {
            BipedModel.bipedHead.rotateAngleX = (float) Math.toRadians(55);
            BipedModel.bipedRightArm.rotateAngleX = (float) Math.toRadians(-100);
            BipedModel.bipedRightArm.rotateAngleY = (float) Math.toRadians(-5);
            BipedModel.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-100);
            BipedModel.bipedLeftArm.rotateAngleY = (float) Math.toRadians(5);
            BipedModel.bipedLeftLeg.rotateAngleZ = (float) -Math.toRadians(5);
            BipedModel.bipedRightLeg.rotateAngleZ = (float) Math.toRadians(5);
            return copyAndReturn(BipedModel, true);
        }

        return copyAndReturn(BipedModel, false);
    }

    public static boolean copyAndReturn(BipedModel BipedModel, boolean cancel) {
        if (BipedModel instanceof PlayerModel) {
            PlayerModel playerModel = (PlayerModel) BipedModel;
            ClientUtil.copyAnglesToWear(playerModel);
        }
        return cancel;
    }

    public static boolean makeZombieArms(BipedModel BipedModel) {
        BipedModel.bipedRightArm.rotateAngleY = -0.1F + BipedModel.bipedHead.rotateAngleY - 0.4F;
        BipedModel.bipedLeftArm.rotateAngleY = 0.1F + BipedModel.bipedHead.rotateAngleY;
        BipedModel.bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + BipedModel.bipedHead.rotateAngleX;
        BipedModel.bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + BipedModel.bipedHead.rotateAngleX;
        return copyAndReturn(BipedModel, true);
    }
	
}
