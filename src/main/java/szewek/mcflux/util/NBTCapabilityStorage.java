package szewek.mcflux.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public final class NBTCapabilityStorage<T extends INBTSerializable<NBTBase>> implements Capability.IStorage<T> {

	@Override public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
		return instance.serializeNBT();
	}

	@Override public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
		instance.deserializeNBT(nbt);
	}
}
