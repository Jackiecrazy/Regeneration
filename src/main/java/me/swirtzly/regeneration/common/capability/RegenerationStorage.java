package me.swirtzly.regeneration.common.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

/**
 * Created by Sub
 * on 16/09/2018.
 */
public class RegenerationStorage implements Capability.IStorage<IRegeneration> {

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IRegeneration> capability, IRegeneration instance, Direction side) {
        return instance.serializeNBT();
    }

    @Override
    public void readNBT(Capability<IRegeneration> capability, IRegeneration instance, Direction side, NBTBase nbt) {
        instance.deserializeNBT(nbt instanceof CompoundNBT ? (CompoundNBT) nbt : new CompoundNBT());
    }

}
