package szewek.mcflux.api;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.common.util.INBTSerializable;

public class EnergyStorage implements IEnergyHolder, INBTSerializable<NBTBase> {
	protected int energy = 0;
	protected final int maxEnergy;

	/**
	 * Creates a simple energy storage.
	 * 
	 * @param max Energy capacity
	 */
	public EnergyStorage(int max) {
		maxEnergy = max;
	}

	@Override
	public NBTBase serializeNBT() {
		return new NBTTagInt(energy);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		if (!(nbt instanceof NBTTagInt))
			return;
		energy = ((NBTTagInt) nbt).getInt();
	}

	@Override
	public int getEnergy() {
		return energy;
	}

	@Override
	public int getEnergyCapacity() {
		return maxEnergy;
	}
}
