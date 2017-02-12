package szewek.mcflux.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public final class EmptyCapabilityStorage<T> implements Capability.IStorage<T> {
	@Override
	public NBTBase writeNBT(Capability<T> cap, T t, EnumFacing side) {
		return null;
	}

	@Override
	public void readNBT(Capability<T> cap, T t, EnumFacing side, NBTBase nbt) {}
}
