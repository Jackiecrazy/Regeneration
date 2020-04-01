package me.swirtzly.regeneration.common.item.arch.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

/**
 * Created by Swirtzly on 01/03/2020 @ 11:42
 */
public class ArchStorage implements Capability.IStorage<IArch> {

	@Nullable
	@Override
	public NBTBase writeNBT(Capability<IArch> capability, IArch instance, Direction side) {
		return instance.serializeNBT();
	}

	@Override
	public void readNBT(Capability<IArch> capability, IArch instance, Direction side, NBTBase nbt) {
		instance.deserializeNBT(nbt instanceof CompoundNBT ? (CompoundNBT) nbt : new CompoundNBT());
	}
}
