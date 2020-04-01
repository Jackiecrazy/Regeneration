package me.swirtzly.regeneration.compat.lucraft;

import lucraft.mods.lucraftcore.materials.potions.PotionRadiation;
import lucraft.mods.lucraftcore.superpowers.SuperpowerHandler;
import lucraft.mods.lucraftcore.util.abilitybar.AbilityBarHandler;
import lucraft.mods.lucraftcore.util.abilitybar.AbilityBarKeys;
import lucraft.mods.lucraftcore.util.events.RenderModelEvent;
import me.swirtzly.regeneration.RegenConfig;
import me.swirtzly.regeneration.client.animation.AnimationContext;
import me.swirtzly.regeneration.client.animation.AnimationHandler;
import me.swirtzly.regeneration.common.capability.CapabilityRegeneration;
import me.swirtzly.regeneration.common.capability.IRegeneration;
import me.swirtzly.regeneration.common.types.IRegenType;
import me.swirtzly.regeneration.common.types.TypeHandler;
import me.swirtzly.regeneration.handlers.IActingHandler;
import me.swirtzly.regeneration.util.ClientUtil;
import me.swirtzly.regeneration.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputtickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static me.swirtzly.regeneration.util.PlayerUtil.RegenState.REGENERATING;

public class LucraftCoreHandler implements IActingHandler {

    public static void registerEntry() {
        AbilityBarHandler.registerProvider(new LCCoreBarEntry());
    }

    public static void registerEventBus() {
        MinecraftForge.EVENT_BUS.register(new LucraftCoreHandler());
    }

    public static String getKeyBindDisplayName() {
        for (int i = 0; i < AbilityBarHandler.ENTRY_SHOW_AMOUNT; i++) {
            if (AbilityBarHandler.getEntryFromKey(i) instanceof LCCoreBarEntry) {
                return AbilityBarKeys.KEYS.get(i).getDisplayName();
            }
        }
        return "???";
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onInput(InputtickEvent tickEvent) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.currentScreen == null && minecraft.player != null) {
            ClientUtil.keyBind = getKeyBindDisplayName();
        }
    }

    @Override
    public void onRegenTick(IRegeneration cap) {

    }

    @Override
    public void onEnterGrace(IRegeneration cap) {

    }

    @Override
    public void onHandsStartGlowing(IRegeneration cap) {

    }

    @Override
    public void onRegenFinish(IRegeneration cap) {

    }

    @Override
    public void onStartPost(IRegeneration cap) {

    }

    @Override
    public void onProcessDone(IRegeneration cap) {

    }

    @Override
    public void onRegenTrigger(IRegeneration cap) {
    }

    @Override
    public void onGoCritical(IRegeneration cap) {

    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent e) {
        if (e.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) e.getEntityLiving();
            IRegeneration data = CapabilityRegeneration.getForPlayer(player);
            boolean flag = data.canRegenerate() && e.getSource() == PotionRadiation.RADIATION && RegenConfig.modIntegrations.lucraftcore.immuneToRadiation;
            e.setCanceled(flag);
        }
    }

    @SubscribeEvent
    public void onCanRegen(PlayerCanRegenEvent e) {
        boolean flag = RegenConfig.modIntegrations.lucraftcore.superpowerDisable && SuperpowerHandler.hasSuperpower(e.getEntityPlayer());
        if (flag) {
            e.setCanceled(true);
            PlayerUtil.sendMessage(e.getEntityPlayer(), "You cannot Regenerate with a superpower", true);
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    @OnlyIn(Dist.CLIENT)
    public void onAnimation(RenderModelEvent.SetRotationAngels ev) {
        if (ev.getEntity() instanceof PlayerEntity) {
            AnimationContext context = new AnimationContext(ev.model, (PlayerEntity) ev.getEntity(), ev.limbSwing, ev.limbSwingAmount, ev.ageInTicks, ev.netHeadYaw, ev.headPitch);
            IRegeneration data = CapabilityRegeneration.getForPlayer((PlayerEntity) ev.getEntity());
            if (data.getState() == REGENERATING) {
                IRegenType type = TypeHandler.getTypeInstance(data.getType());
                ev.setCanceled(type.getRenderer().onAnimateRegen(context));
            } else {
                AnimationHandler.animate(context);
            }
        }
    }
}
