package szewek.mcflux.api.ex;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public class EnergyNBTStorage implements Capability.IStorage<IEnergy> {
	@Override
	public NBTBase writeNBT(Capability<IEnergy> capability, IEnergy t, EnumFacing side) {
		return t instanceof INBTSerializable ? ((INBTSerializable) t).serializeNBT() : new NBTTagLong(t.getEnergy());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readNBT(Capability<IEnergy> capability, IEnergy t, EnumFacing side, NBTBase nbt) {
		if (t instanceof INBTSerializable)
			((INBTSerializable<NBTBase>) t).deserializeNBT(nbt);
		else if (t instanceof NBTPrimitive)
			t.setEnergy(((NBTPrimitive) nbt).getLong());
	}
}
