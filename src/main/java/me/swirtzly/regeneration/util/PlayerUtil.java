package me.swirtzly.regeneration.util;

import me.swirtzly.regeneration.client.skinhandling.SkinChangingHandler;
import me.swirtzly.regeneration.network.MessageSetPerspective;
import me.swirtzly.regeneration.network.MessageUpdateModel;
import me.swirtzly.regeneration.network.NetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sub
 * on 20/09/2018.
 */
public class PlayerUtil {

    public static ArrayList<Effect> POTIONS = new ArrayList<>();

    public static void createPostList() {
        POTIONS.add(Effects.WEAKNESS);
        POTIONS.add(Effects.MINING_FATIGUE);
        POTIONS.add(Effects.RESISTANCE);
        POTIONS.add(Effects.HEALTH_BOOST);
        POTIONS.add(Effects.HUNGER);
        POTIONS.add(Effects.WATER_BREATHING);
        POTIONS.add(Effects.HASTE);
    }

    public static void sendMessage(PlayerEntity player, String message, boolean hotBar) {
        if (!player.world.isRemote) {
            player.sendStatusMessage(new TranslationTextComponent(message), hotBar);
        }
    }

    public static void sendMessage(PlayerEntity player, TranslationTextComponent translation, boolean hotBar) {
        if (!player.world.isRemote) {
            player.sendStatusMessage(translation, hotBar);
        }
    }

    public static void sendMessageToAll(TranslationTextComponent translation) {
        List<ServerPlayerEntity> players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
        players.forEach(playerMP -> sendMessage(playerMP, translation, false));
    }

    public static void setPerspective(ServerPlayerEntity player, boolean thirdperson, boolean resetPitch) {
        NetworkHandler.INSTANCE.sendTo(new MessageSetPerspective(thirdperson, resetPitch), player);
    }

    public static boolean canEntityAttack(Entity entity) { // NOTE unused
        if (entity instanceof MobEntity) {
            MobEntity ent = (MobEntity) entity;
            for (GoalSelector.EntityAITaskEntry task : ent.tasks.taskEntries) {
                if (task.action instanceof MeleeAttackGoal || task.action instanceof RangedAttackGoal || task.action instanceof RangedBowAttackGoal
                        || task.action instanceof NearestAttackableTargetGoal || task.action instanceof ZombieAttackGoal || task.action instanceof OwnerHurtByTargetGoal)
                    return true;
            }
        }
        return false;
    }

    public static void updateModel(SkinChangingHandler.EnumChoices choice) {
        NetworkHandler.INSTANCE.sendToServer(new MessageUpdateModel(choice.name()));
    }

    public static boolean applyPotionIfAbsent(PlayerEntity player, Effect potion, int length, int amplifier, boolean ambient, boolean showParticles) {
        if (potion == null) return false;
        if (player.getActivePotionEffect(potion) == null) {
            player.addPotionEffect(new EffectInstance(potion, length, amplifier, ambient, showParticles));
            return true;
        }
        return false;
    }

    public static void lookAt(double px, double py, double pz, PlayerEntity me) {
        double dirx = me.getPosition().getX() - px;
        double diry = me.getPosition().getY() - py;
        double dirz = me.getPosition().getZ() - pz;

        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

        dirx /= len;
        diry /= len;
        dirz /= len;

        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        //to degree
        pitch = pitch * 180.0 / Math.PI;
        yaw = yaw * 180.0 / Math.PI;

        yaw += 90f;
        me.rotationPitch = (float) pitch;
        me.rotationYaw = (float) yaw;
    }

    public enum RegenState {

        ALIVE,
        GRACE, GRACE_CRIT, POST,
        REGENERATING;

        public boolean isGraceful() {
            return this == GRACE || this == GRACE_CRIT;
        }

        public enum Transition {
            HAND_GLOW_START(Color.YELLOW.darker()), HAND_GLOW_TRIGGER(Color.ORANGE),
            ENTER_CRITICAL(Color.BLUE),
            CRITICAL_DEATH(Color.RED),
            FINISH_REGENERATION(Color.GREEN.darker()),
            END_POST(Color.PINK.darker());

            public final Color color;

            Transition(Color col) {
                this.color = col;
            }
        }

    }
}
