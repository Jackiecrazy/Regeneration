package me.swirtzly.regeneration.common.tiles;

import me.swirtzly.regeneration.common.capability.RegenCap;
import me.swirtzly.regeneration.common.capability.IRegeneration;
import me.swirtzly.regeneration.handlers.RegenObjects;
import me.swirtzly.regeneration.util.PlayerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class TileEntityHandInJar extends TileEntity implements ITickableTileEntity, IInventory {

    public int lindosAmont = 0;
    private NonNullList<ItemStack> handInv = NonNullList.withSize(7, ItemStack.EMPTY);

    public int getLindosAmont() {
        return lindosAmont;
    }

    public void setLindosAmont(int lindosAmont) {
        this.lindosAmont = lindosAmont;
    }

    @Override
    public void tick() {

        if (world.getGameTime() % 35 == 0 && hasHand()) {
            world.playSound(null, getPos().getX(), getPos().getY(), getPos().getZ(), RegenObjects.Sounds.JAR_BUBBLES, SoundCategory.PLAYERS, 0.4F, 0.3F);
        }

        PlayerEntity player = world.getClosestPlayer(getPos().getX(), getPos().getY(), getPos().getZ(), 56, false);
        if (player != null) {
            LazyOptional<IRegeneration> data = RegenCap.get(player);

            data.ifPresent((iRegeneration) -> {
                if (iRegeneration.getState() == PlayerUtil.RegenState.REGENERATING) {
                    if (world.rand.nextInt(90) < 10) {
                        lindosAmont = lindosAmont + 1;
                    }
                }
            });
        }
    }

    public ItemStack getHand() {
        return handInv.get(3);
    }

    public boolean hasHand() {
        return handInv.get(3).getItem() == RegenObjects.Items.HAND;
    }


    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putFloat("lindos", lindosAmont);
        compound.putBoolean("hasHand", hasHand());
        ItemStackHelper.saveAllItems(compound, this.handInv);
        return super.write(compound);
    }


    @Override
    public void read(CompoundNBT compound) {
        lindosAmont = compound.getInt("lindos");
        ItemStackHelper.loadAllItems(compound, this.handInv);
        super.read(compound);
    }


    @Override
    public int getSizeInventory() {
        return handInv.size();
    }

    @Override
    public boolean isEmpty() {
        return handInv.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return handInv.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = this.handInv.get(index);

        if (index == 2 && !itemstack.isEmpty()) {
            return ItemStackHelper.getAndSplit(this.handInv, index, itemstack.getCount());
        } else {
            ItemStack itemstack1 = ItemStackHelper.getAndSplit(this.handInv, index, count);
            return itemstack1;
        }
    }


    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(handInv, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.handInv.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    @Override
    public void openInventory(PlayerEntity player) {
        this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
    }

    @Override
    public void closeInventory(PlayerEntity player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        handInv.clear();
    }

    @Override
    public String getName() {
        return getDisplayName().getUnformattedText();
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(RegenObjects.Blocks.HAND_JAR.getLocalizedName());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 3, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(pkt.getNbtCompound());
    }

    public void sendUpdates() {
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        world.scheduleBlockUpdate(pos, getBlockType(), 0, 0);
        markDirty();
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
        return false;
    }
}
