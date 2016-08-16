package szewek.mcflux.api;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;

public class EnergyStorage implements IEnergyHandler {
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
	public NBTBase saveEnergyNBT() {
		return new NBTTagInt(energy);
	}

	@Override
	public void loadEnergyNBT(NBTBase nbt) {
		if (!(nbt instanceof NBTTagInt))
			return;
		energy = ((NBTTagInt) nbt).getInt();
	}
}
