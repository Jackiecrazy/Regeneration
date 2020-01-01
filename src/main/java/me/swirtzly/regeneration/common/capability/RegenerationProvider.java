package me.swirtzly.regeneration.common.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Sub
 * on 16/09/2018.
 */
public class RegenerationProvider implements ICapabilitySerializable<CompoundNBT> {

    private IRegeneration capability;

    public RegenerationProvider(IRegeneration capability) {
        this.capability = capability;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
        return RegenCap.CAPABILITY != null && capability == RegenCap.CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return capability == RegenCap.CAPABILITY ? RegenCap.CAPABILITY.cast(this.capability) : null;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) RegenCap.CAPABILITY.getStorage().writeNBT(RegenCap.CAPABILITY, capability, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        RegenCap.CAPABILITY.getStorage().readNBT(RegenCap.CAPABILITY, capability, null, nbt);
    }

}
