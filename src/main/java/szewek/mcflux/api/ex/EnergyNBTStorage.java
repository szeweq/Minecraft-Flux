package szewek.mcflux.api.ex;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class EnergyNBTStorage implements Capability.IStorage<IEnergy> {
	@Override
	public NBTBase writeNBT(Capability<IEnergy> capability, IEnergy t, EnumFacing side) {
		return t instanceof INBTEnergy ? ((INBTEnergy) t).writeNBTEnergy() : new NBTTagLong(t.getEnergy());
	}

	@Override
	public void readNBT(Capability<IEnergy> capability, IEnergy t, EnumFacing side, NBTBase nbt) {
		if (t instanceof INBTEnergy)
			((INBTEnergy) t).readNBTEnergy(nbt);
		else if (t instanceof NBTPrimitive)
			t.setEnergy(((NBTPrimitive) nbt).getLong());
	}
}
