package szewek.mcflux.api.fe;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public class FlavorNBTStorage implements Capability.IStorage<IFlavorEnergy> {
	@Override public NBTBase writeNBT(Capability<IFlavorEnergy> capability, IFlavorEnergy t, EnumFacing side) {
		return t instanceof INBTSerializable ? ((INBTSerializable) t).serializeNBT() : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readNBT(Capability<IFlavorEnergy> capability, IFlavorEnergy t, EnumFacing side, NBTBase nbt) {
		if (t instanceof INBTSerializable)
			((INBTSerializable<NBTBase>) t).deserializeNBT(nbt);
	}
}
