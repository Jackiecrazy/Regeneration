package me.swirtzly.regeneration.common.types;

import me.swirtzly.regeneration.client.rendering.types.TypeLayFadeRenderer;
import me.swirtzly.regeneration.common.capability.IRegeneration;
import me.swirtzly.regeneration.handlers.RegenObjects;
import me.swirtzly.regeneration.util.PlayerUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;

public class TypeLayFade implements IRegenType<TypeLayFadeRenderer> {

    private SoundEvent[] soundEvents = new SoundEvent[]{RegenObjects.Sounds.HAND_GLOW};

    @Override
    public int getAnimationLength() {
        return 350;
    }

    @Override
    public TypeLayFadeRenderer getRenderer() {
        return TypeLayFadeRenderer.INSTANCE;
    }

    @Override
    public void onStartRegeneration(PlayerEntity player, IRegeneration capability) {

    }

    @Override
    public void onUpdateMidRegen(PlayerEntity player, IRegeneration capability) {
        if (!player.world.isRemote) {
            PlayerUtil.setPerspective((ServerPlayerEntity) player, true, false);
        }
    }

    @Override
    public void onFinishRegeneration(PlayerEntity player, IRegeneration capability) {
        PlayerUtil.setPerspective((ServerPlayerEntity) player, false, true);
    }

    @Override
    public double getAnimationProgress(IRegeneration cap) {
        return Math.min(1, cap.getAnimationTicks() / (double) getAnimationLength());
    }

    @Override
    public TypeHandler.RegenType getTypeID() {
        return TypeHandler.RegenType.LAY_FADE;
    }

    @Override
    public SoundEvent[] getRegeneratingSounds() {
        return soundEvents;
    }

    @Override
    public Vec3d getDefaultPrimaryColor() {
        return new Vec3d(1, 1, 1);
    }

    @Override
    public Vec3d getDefaultSecondaryColor() {
        return new Vec3d(1, 1, 1);
    }

}
