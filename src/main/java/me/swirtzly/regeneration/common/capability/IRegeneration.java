package me.swirtzly.regeneration.common.capability;

import me.swirtzly.regeneration.RegenConfig;
import me.swirtzly.regeneration.client.skinhandling.SkinChangingHandler;
import me.swirtzly.regeneration.client.skinhandling.SkinInfo;
import me.swirtzly.regeneration.common.types.TypeHandler;
import me.swirtzly.regeneration.compat.lucraft.PlayerCanRegenEvent;
import me.swirtzly.regeneration.util.PlayerUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by Sub
 * on 16/09/2018.
 */
public interface IRegeneration extends INBTSerializable<CompoundNBT> {

    PlayerEntity getPlayer();

    int getRegenerationsLeft();

    /**
     * Only for debug purposes!
     */
    @Deprecated
    void setRegenerationsLeft(int amount);

    void triggerRegeneration();

    void tick();

    void synchronise();

    CompoundNBT getStyle();

    void setStyle(CompoundNBT nbt);

    Vec3d getPrimaryColor();

    Vec3d getSecondaryColor();

    /**
     * Returns if the player is currently <i>able to</i> regenerate
     */
    default boolean canRegenerate() {
        return (RegenConfig.infiniteRegeneration || getRegenerationsLeft() > 0) && getPlayer().getPosition().getY() > 0 && !MinecraftForge.EVENT_BUS.post(new PlayerCanRegenEvent(getPlayer()));
    }

    void receiveRegenerations(int amount);

    void extractRegeneration(int amount);

    PlayerUtil.RegenState getState();

    TypeHandler.RegenType getType();

    void setType(TypeHandler.RegenType type);

    IRegenerationStateManager getStateManager();

    String getEncodedSkin();

    void setEncodedSkin(String string);

    SkinInfo.SkinType getSkinType();

    void setSkinType(String skinType);

    SkinChangingHandler.EnumChoices getPreferredModel();

    void setPreferredModel(String skinType);

    boolean areHandsGlowing();

    String getDeathSource();

    void setDeathSource(String source);

    ResourceLocation getDnaType();

    void setDnaType(ResourceLocation resgitryName);

    boolean isDnaActive();

    void setDnaActive(boolean alive);

    int getAnimationTicks();

    void setAnimationTicks(int ticks);

    void setSyncingFromJar(boolean syncing);

    boolean isSyncingToJar();

    String getNextSkin();

    void setNextSkin(String base64);

    SkinInfo.SkinType getNextSkinType();

    void setNextSkinType(SkinInfo.SkinType choices);

    boolean hasDroppedHand();

    void setDroppedHand(boolean droppedHand);

    HandSide getCutoffHand();

    void setCutOffHand(HandSide side);
}
