package me.swirtzly.regeneration.common.item;

import me.swirtzly.regeneration.common.item.arch.IDontStore;
import me.swirtzly.regeneration.common.item.arch.capability.CapabilityArch;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Swirtzly on 29/01/2020 @ 20:41
 */
public class ItemArchInterface extends Item implements IDontStore {


    public ItemArchInterface() {
        setCreativeTab(ItemGroup.MISC);
        setMaxStackSize(1);
    }

    /**
     * Called when the equipped item is right clicked.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        EquipmentSlotType entityequipmentslot = MobEntity.getSlotForItemStack(itemstack);
        ItemStack headSlot = playerIn.getItemStackFromSlot(entityequipmentslot);
        if (headSlot.isEmpty()) {
            playerIn.setItemStackToSlot(entityequipmentslot, itemstack.copy());
            itemstack.setCount(0);
            return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
        }
        return new ActionResult<>(ActionResultType.FAIL, itemstack);
    }

    @Nonnull
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT oldCapNbt) {
        return new InvProvider();
    }


    public static void sync(ItemStack stack) {
        if (stack.getTagCompound() != null) {
            stack.getTagCompound().merge(stack.getItem().getNBTShareTag(stack));
        }
    }

    public static void readSync(ItemStack stack) {
        if (stack.getTagCompound() != null) {
            stack.getItem().readNBTShareTag(stack, stack.getTagCompound());
        }
    }

    @Override
    public boolean getShareTag() {
        return super.getShareTag();
    }

    @Nullable
    @Override
    public CompoundNBT getNBTShareTag(ItemStack stack) {
        CompoundNBT tag = stack.getTagCompound();
        if (tag != null) {
            tag.setTag("arch_sync", CapabilityArch.getForStack(stack).serializeNBT());
            return tag;
        }
        return super.getNBTShareTag(stack);
    }

    @Override
    public void readNBTShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
        super.readNBTShareTag(stack, nbt);
        if (nbt != null && stack != null) {
            if (nbt.hasKey("cap_sync")) {
                CapabilityArch.getForStack(stack).deserializeNBT((CompoundNBT) nbt.getTag("arch_sync"));
            }
        }
    }


    @Override
    public boolean onEntitySwing(LivingEntity entityLiving, ItemStack stack) {
        return super.onEntitySwing(entityLiving, stack);
    }

    @Nullable
    @Override
    public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
        return EquipmentSlotType.HEAD;
    }

    private static class InvProvider implements ICapabilitySerializable<NBTBase> {

        private final IItemHandler inv = new ItemStackHandler(1) {
            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack toInsert, boolean simulate) {
                if (!toInsert.isEmpty()) {
                    boolean isUseAble = toInsert.hasCapability(CapabilityArch.CAPABILITY, null) && toInsert.getCount() == 1;
                    if (isUseAble)
                        return super.insertItem(slot, toInsert, simulate);
                }
                return toInsert;
            }
        };

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
            return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
        }

        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv);
            else return null;
        }

        @Override
        public NBTBase serializeNBT() {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inv, null);
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inv, null, nbt);
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        tooltip.add(new TranslationTextComponent("item.info.purpose").getUnformattedComponentText());

        if (InventoryScreen.isShiftKeyDown()) {
            tooltip.add(new TranslationTextComponent("item.info.arch_power").getUnformattedComponentText());
            tooltip.add(new TranslationTextComponent("item.info.arch_power2").getUnformattedComponentText());
            tooltip.add(new TranslationTextComponent("item.info.arch_use").getUnformattedComponentText());
        } else {
            tooltip.add(new TranslationTextComponent("item.info.shift").getUnformattedComponentText());
        }

    }
}
