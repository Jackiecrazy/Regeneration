package me.swirtzly.regeneration.common.item.arch.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Swirtzly on 01/03/2020 @ 11:31
 */
public class ArchProvider implements ICapabilitySerializable<CompoundNBT> {

    private IArch capability;

    public ArchProvider(IArch capability) {
        this.capability = capability;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
        return CapabilityArch.CAPABILITY != null && capability == CapabilityArch.CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return capability == CapabilityArch.CAPABILITY ? CapabilityArch.CAPABILITY.cast(this.capability) : null;
    }

    @Override
    public CompoundNBT serializeNBT() {
        if (CapabilityArch.CAPABILITY != null) {
            Capability.IStorage<IArch> storage = CapabilityArch.CAPABILITY.getStorage();
            if (storage != null) {
                return (CompoundNBT) CapabilityArch.CAPABILITY.getStorage().writeNBT(CapabilityArch.CAPABILITY, capability, null);
            }
        }
        return new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

        if (CapabilityArch.CAPABILITY != null) {
            Capability.IStorage<IArch> storage = CapabilityArch.CAPABILITY.getStorage();
            if (storage != null) {
                CapabilityArch.CAPABILITY.getStorage().readNBT(CapabilityArch.CAPABILITY, capability, null, nbt);
            }
        }
    }
}
