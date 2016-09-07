package szewek.mcflux.api;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;

@Deprecated
public class EnergyStorage implements IEnergyHolder, IEnergyNBT {
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
	public NBTBase writeEnergyNBT() {
		return new NBTTagInt(energy);
	}

	@Override
	public void readEnergyNBT(NBTBase nbt) {
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
	
	@Override
	public void setEnergy(int amount) {
		energy = amount > maxEnergy ? maxEnergy : amount;
	}
}
