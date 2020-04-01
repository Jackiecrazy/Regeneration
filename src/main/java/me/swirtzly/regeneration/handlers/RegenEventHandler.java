package me.swirtzly.regeneration.handlers;

import com.google.common.base.Predicate;
import me.swirtzly.regeneration.RegenConfig;
import me.swirtzly.regeneration.RegenerationMod;
import me.swirtzly.regeneration.common.advancements.RegenTriggers;
import me.swirtzly.regeneration.common.capability.CapabilityRegeneration;
import me.swirtzly.regeneration.common.capability.IRegeneration;
import me.swirtzly.regeneration.common.capability.RegenerationProvider;
import me.swirtzly.regeneration.common.item.ItemHand;
import me.swirtzly.regeneration.network.MessageRemovePlayer;
import me.swirtzly.regeneration.network.NetworkHandler;
import me.swirtzly.regeneration.util.PlayerUtil;
import me.swirtzly.regeneration.util.RegenUtil;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.HandSide;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

import static me.swirtzly.regeneration.util.PlayerUtil.RegenState.POST;

/**
 * Created by Sub on 16/09/2018.
 */
@Mod.EventBusSubscriber(modid = RegenerationMod.MODID)
public class RegenEventHandler {

    // =========== CAPABILITY HANDLING =============

    @SubscribeEvent
    public static void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            IRegeneration data = CapabilityRegeneration.getForPlayer(player);
            data.tick();

            if (data.hasDroppedHand() && !player.getHeldItemOffhand().isEmpty()) {
                player.dropItem(player.getHeldItemOffhand(), false);
                player.setItemStackToSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.AIR));
            }
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(CapabilityRegeneration.CAP_REGEN_ID, new RegenerationProvider(new CapabilityRegeneration((PlayerEntity) event.getObject())));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        IStorage<IRegeneration> storage = CapabilityRegeneration.CAPABILITY.getStorage();

        IRegeneration oldCap = CapabilityRegeneration.getForPlayer(event.getOriginal());
        IRegeneration newCap = CapabilityRegeneration.getForPlayer(event.getEntityPlayer());

        CompoundNBT nbt = (CompoundNBT) storage.writeNBT(CapabilityRegeneration.CAPABILITY, oldCap, null);
        storage.readNBT(CapabilityRegeneration.CAPABILITY, newCap, null, nbt);
        CapabilityRegeneration.getForPlayer(event.getEntityPlayer()).synchronise();
    }

    @SubscribeEvent
    public static void playerTracking(PlayerEvent.StartTracking event) {
        CapabilityRegeneration.getForPlayer(event.getEntityPlayer()).synchronise();
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!RegenConfig.firstStartGiftOnly)
            CapabilityRegeneration.getForPlayer(event.player).receiveRegenerations(RegenConfig.freeRegenerations);

        CapabilityRegeneration.getForPlayer(event.player).synchronise();
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
        CapabilityRegeneration.getForPlayer(event.player).synchronise();
    }

    @SubscribeEvent
    public static void onDeathEvent(LivingDeathEvent e) {
        if (e.getEntityLiving() instanceof PlayerEntity) {
            CapabilityRegeneration.getForPlayer((PlayerEntity) e.getEntityLiving()).synchronise();
        }
    }

    // ============ USER EVENTS ==========

    @SubscribeEvent
    public static void onPunchBlock(PlayerInteractEvent.LeftClickBlock e) {
        if (e.getEntityPlayer().world.isRemote) return;
        CapabilityRegeneration.getForPlayer(e.getEntityPlayer()).getStateManager().onPunchBlock(e);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onHurt(LivingHurtEvent event) {
        Entity trueSource = event.getSource().getTrueSource();

        if (trueSource instanceof PlayerEntity && event.getEntityLiving() instanceof MobEntity) {
            PlayerEntity player = (PlayerEntity) trueSource;
            CapabilityRegeneration.getForPlayer(player).getStateManager().onPunchEntity(event);
            return;
        }

        if (!(event.getEntity() instanceof PlayerEntity) || event.getSource() == RegenObjects.REGEN_DMG_CRITICAL || event.getSource() == RegenObjects.REGEN_DMG_KILLED)
            return;

        PlayerEntity player = (PlayerEntity) event.getEntity();
        IRegeneration cap = CapabilityRegeneration.getForPlayer(player);

        cap.setDeathSource(event.getSource().getDeathMessage(player).getUnformattedText());

        if (cap.getState() == POST && player.posY > 0) {
            if (event.getSource() == DamageSource.FALL) {
                PlayerUtil.applyPotionIfAbsent(player, Effects.NAUSEA, 200, 4, false, false);
                if (event.getAmount() > 8.0F) {
                    if (player.world.getGameRules().getBoolean("mobGriefing") && RegenConfig.postRegen.genGreator) {
                        RegenUtil.genCrater(player.world, player.getPosition(), 3);
                    }
                    event.setAmount(0.5F);
                    PlayerUtil.sendMessage(player, new TranslationTextComponent("regeneration.messages.fall_dmg"), true);
                    return;
                }
            } else {
                if (!player.world.isRemote) {
                    if (trueSource instanceof MobEntity) {
                        MobEntity living = (MobEntity) trueSource;
                        if (RegenUtil.isSharp(living.getHeldItemMainhand()) & player.world.rand.nextBoolean() && !cap.hasDroppedHand()) {
                            ItemStack hand = new ItemStack(RegenObjects.Items.HAND);
                            ItemHand.setTextureString(hand, cap.getEncodedSkin());
                            ItemHand.setSkinType(hand, cap.getSkinType().name());
                            ItemHand.setOwner(hand, player.getUniqueID());
                            ItemHand.setTimeCreated(hand, System.currentTimeMillis());
                            ItemHand.setTrait(hand, cap.getDnaType().toString());
                            cap.setDroppedHand(true);
                            RegenTriggers.HAND.trigger((ServerPlayerEntity) player);
                            if (player.getPrimaryHand() == HandSide.LEFT) {
                                cap.setCutOffHand(HandSide.RIGHT);
                            } else {
                                cap.setCutOffHand(HandSide.LEFT);
                            }
                            InventoryHelper.spawnItemStack(player.world, player.posX, player.posY, player.posZ, hand);
                        }
                    }
                }
                event.setAmount(0.5F);
                PlayerUtil.sendMessage(player, new TranslationTextComponent("regeneration.messages.reduced_dmg"), true);
            }
            return;
        }

        if (cap.getState() == PlayerUtil.RegenState.REGENERATING && RegenConfig.regenFireImmune && event.getSource().isFireDamage() || cap.getState() == PlayerUtil.RegenState.REGENERATING && event.getSource().isExplosion()) {
            event.setCanceled(true); // TODO still "hurts" the client view
        } else if (player.getHealth() + player.getAbsorptionAmount() - event.getAmount() <= 0) { // player has actually died
            boolean notDead = cap.getStateManager().onKilled(event.getSource());
            event.setCanceled(notDead);
        }
    }

    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            if (CapabilityRegeneration.getForPlayer((PlayerEntity) event.getEntityLiving()).getState() == PlayerUtil.RegenState.REGENERATING) {
                event.setCanceled(true);
            }
        }
    }

    // ================ OTHER ==============
    @SubscribeEvent
    public static void onLogin(PlayerLoggedInEvent event) {
        if (event.player.world.isRemote) return;

        CompoundNBT nbt = event.player.getEntityData(), persist = nbt.getCompoundTag(PlayerEntity.PERSISTED_NBT_TAG);
        if (!persist.getBoolean("loggedInBefore"))
            CapabilityRegeneration.getForPlayer(event.player).receiveRegenerations(RegenConfig.freeRegenerations);
        persist.setBoolean("loggedInBefore", true);
        nbt.setTag(PlayerEntity.PERSISTED_NBT_TAG, persist);
    }

    /**
     * Update checker thing, tells the player that the mods out of date if they're on a old build
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent e) {
        PlayerEntity player = e.player;
        if (!player.world.isRemote && RegenConfig.enableUpdateChecker) {
            ForgeVersion.CheckResult version = ForgeVersion.getResult(Loader.instance().activeModContainer());
            if (version.status.equals(ForgeVersion.Status.OUTDATED)) {
                StringTextComponent url = new StringTextComponent(TextFormatting.AQUA + TextFormatting.BOLD.toString() + "UPDATE");
                url.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minecraft.curseforge.com/projects/regeneration"));
                url.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Open URL")));

                player.sendMessage(new StringTextComponent(TextFormatting.GOLD + "[Regeneration] : ").appendSibling(url));
                String changes = version.changes.get(version.target);
                player.sendMessage(new StringTextComponent(TextFormatting.GOLD + "Changes: " + TextFormatting.BLUE + changes));
            }
        }
    }

    @SubscribeEvent
    public static void addRunAwayTask(EntityJoinWorldEvent e) {
        if (e.getEntity().world.isRemote) return;
        if (e.getEntity() instanceof CreatureEntity) {
            CreatureEntity living = (CreatureEntity) e.getEntity();
            Predicate<Entity> pred = entity -> {

                if (entity instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) entity;
                    IRegeneration data = CapabilityRegeneration.getForPlayer(player);
                    return data.getState() == PlayerUtil.RegenState.REGENERATING || data.areHandsGlowing();
                }
                return false;
            };

            living.tasks.addTask(0, new AvoidEntityGoal(living, PlayerEntity.class, pred, 6.0F, 1.0D, 1.2D));
        }

        if (e.getEntity() instanceof PlayerEntity) {
            NetworkHandler.INSTANCE.sendToAll(new MessageRemovePlayer(e.getEntity().getUniqueID()));
        }
    }

    @SubscribeEvent
    public static void onCut(PlayerInteractEvent.RightClickItem event) {
        if (RegenUtil.isSharp(event.getItemStack())) {
            PlayerEntity player = event.getEntityPlayer();
            IRegeneration cap = CapabilityRegeneration.getForPlayer(player);
            if (!player.world.isRemote && cap.getState() == POST && player.isSneaking() && !cap.hasDroppedHand()) {
                ItemStack hand = new ItemStack(RegenObjects.Items.HAND);
                ItemHand.setTextureString(hand, cap.getEncodedSkin());
                ItemHand.setSkinType(hand, cap.getSkinType().name());
                ItemHand.setOwner(hand, player.getUniqueID());
                ItemHand.setTimeCreated(hand, System.currentTimeMillis());
                ItemHand.setTrait(hand, cap.getDnaType().toString());
                cap.setDroppedHand(true);
                RegenTriggers.HAND.trigger((ServerPlayerEntity) player);
                if (player.getPrimaryHand() == HandSide.LEFT) {
                    cap.setCutOffHand(HandSide.RIGHT);
                } else {
                    cap.setCutOffHand(HandSide.LEFT);
                }
                InventoryHelper.spawnItemStack(player.world, player.posX, player.posY, player.posZ, hand);
            }
        }
    }
	
}
