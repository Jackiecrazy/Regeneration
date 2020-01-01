package me.swirtzly.regeneration.common.types;

import me.swirtzly.regeneration.client.rendering.types.ATypeRenderer;
import me.swirtzly.regeneration.common.capability.IRegeneration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;

/**
 * SUBCLASSES MUST HAVE A DEFAULT CONSTRUCTOR
 * <p>
 * Created by Sub
 * on 16/09/2018.
 */
public interface IRegenType<R extends ATypeRenderer<?>> {

    /**
     * @return in ticks
     */
    int getAnimationLength();

    R getRenderer();

    default void onStartRegeneration(PlayerEntity player, IRegeneration capability) {
    }

    default void onUpdateMidRegen(PlayerEntity player, IRegeneration capability) {
    }

    default void onFinishRegeneration(PlayerEntity player, IRegeneration capability) {
    }

    double getAnimationProgress(IRegeneration cap);

    TypeHandler.RegenType getTypeID();

    SoundEvent[] getRegeneratingSounds();

    Vec3d getDefaultPrimaryColor();

    Vec3d getDefaultSecondaryColor();


}
