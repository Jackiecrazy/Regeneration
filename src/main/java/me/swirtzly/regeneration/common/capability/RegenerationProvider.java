package me.swirtzly.regeneration.common.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Sub on 16/09/2018.
 */
public class RegenerationProvider implements ICapabilitySerializable<CompoundNBT> {

    private IRegeneration capability;

    public RegenerationProvider(IRegeneration capability) {
        this.capability = capability;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) CapabilityRegeneration.CAPABILITY.getStorage().writeNBT(CapabilityRegeneration.CAPABILITY, capability, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CapabilityRegeneration.CAPABILITY.getStorage().readNBT(CapabilityRegeneration.CAPABILITY, capability, null, nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CapabilityRegeneration.CAPABILITY != null && capability == CapabilityRegeneration.CAPABILITY;
    }
}
