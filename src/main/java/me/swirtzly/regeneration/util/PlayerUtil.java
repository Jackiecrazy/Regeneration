package me.swirtzly.regeneration.util;

import me.swirtzly.regeneration.RegenerationMod;
import me.swirtzly.regeneration.client.skinhandling.SkinChangingHandler;
import me.swirtzly.regeneration.network.MessageSetPerspective;
import me.swirtzly.regeneration.network.MessageUpdateModel;
import me.swirtzly.regeneration.network.NetworkHandler;
import net.minecraft.block.*;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sub on 20/09/2018.
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
        List<ServerPlayerEntity> players = FMLCommonHandler.instance().getInstanceServerInstance().getPlayerList().getPlayers();
        players.forEach(playerMP -> sendMessage(playerMP, translation, false));
    }

    public static void setPerspective(ServerPlayerEntity player, boolean thirdperson, boolean resetPitch) {
        NetworkHandler.INSTANCE.sendTo(new MessageSetPerspective(thirdperson, resetPitch), player);
    }

    public static boolean canEntityAttack(Entity entity) { // NOTE unused
        if (entity instanceof MobEntity) {
            MobEntity ent = (MobEntity) entity;
            for (GoalSelector.EntityAITaskEntry task : ent.tasks.taskEntries) {
                if (task.action instanceof MeleeAttackGoal || task.action instanceof RangedAttackGoal || task.action instanceof RangedBowAttackGoal || task.action instanceof NearestAttackableTargetGoal || task.action instanceof ZombieAttackGoal || task.action instanceof OwnerHurtByTargetGoal)
                    return true;
            }
        }
        return false;
    }

    public static void updateModel(SkinChangingHandler.EnumChoices choice) {
        NetworkHandler.INSTANCE.sendToServer(new MessageUpdateModel(choice.name()));
    }

    public static void openDoors(PlayerEntity player) {
        if (player.world.isRemote) return;
        AxisAlignedBB box = player.getEntityBoundingBox().grow(20);
        for (BlockPos pos : BlockPos.getAllInBox(new BlockPos(box.minX, box.minY, box.minZ), new BlockPos(box.maxX, box.maxY, box.maxZ))) {
            BlockState blockState = player.world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (player.getPosition().getY() < pos.getY()) {
                if (block instanceof TrapDoorBlock) {
                    BlockState newState = blockState.withProperty(TrapDoorBlock.OPEN, true);
                    markUpdate(player.world, pos, newState);
                    int j = blockState.getMaterial() == Material.IRON ? 1036 : 1013;
                    player.world.playEvent(player, j, pos, 0);
                    return;
                }
            } else if (block instanceof DoorBlock) {
                BlockState down = player.world.getBlockState(pos);
                BlockState newState = down.withProperty(DoorBlock.OPEN, true);
                markUpdate(player.world, pos, newState);
                player.world.playEvent(player, blockState.getValue(DoorBlock.OPEN) ? blockState.getMaterial() == Material.IRON ? 1005 : 1006 : blockState.getMaterial() == Material.IRON ? 1011 : 1012, pos, 0);
                return;
            }
        }
    }

    private static void markUpdate(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state, 10);
        world.markBlockRangeForRenderUpdate(pos, pos);
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

        // to degree
        pitch = pitch * 180.0 / Math.PI;
        yaw = yaw * 180.0 / Math.PI;

        yaw += 90f;
        me.rotationPitch = (float) pitch;
        me.rotationYaw = (float) yaw;
    }

    public static boolean isInHand(Hand hand, LivingEntity holder, Item item) {
        ItemStack heldItem = holder.getHeldItem(hand);
        return heldItem.getItem() == item;
    }

    public static boolean isInMainHand(LivingEntity holder, Item item) {
        return isInHand(Hand.MAIN_HAND, holder, item);
    }

    /**
     * Checks if player has item in offhand
     */
    public static boolean isInOffHand(LivingEntity holder, Item item) {
        return isInHand(Hand.OFF_HAND, holder, item);
    }

    /**
     * Checks if player has item in either hand
     */
    public static boolean isInEitherHand(LivingEntity holder, Item item) {
        return isInMainHand(holder, item) || isInOffHand(holder, item);
    }

    // MAIN_HAND xor OFF_HAND
    public static boolean isInOneHand(LivingEntity holder, Item item) {
        boolean mainHand = (isInMainHand(holder, item) && !isInOffHand(holder, item));
        boolean offHand = (isInOffHand(holder, item) && !isInMainHand(holder, item));
        return mainHand || offHand;
    }

    public enum RegenState {

        ALIVE, GRACE, GRACE_CRIT, POST, REGENERATING;

        public boolean isGraceful() {
            return this == GRACE || this == GRACE_CRIT;
        }

        public TranslationTextComponent getText() {
            return new TranslationTextComponent("transition." + RegenerationMod.MODID + "." + name().toLowerCase());
        }

        public enum Transition {
            HAND_GLOW_START, HAND_GLOW_TRIGGER, ENTER_CRITICAL, CRITICAL_DEATH, FINISH_REGENERATION, END_POST
        }

    }
}
