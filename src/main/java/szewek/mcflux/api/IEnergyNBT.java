package szewek.mcflux.api;

import net.minecraft.nbt.NBTBase;

public interface IEnergyNBT {
	/**
	 * Reads energy value from NBT data.
	 * 
	 * @param nbt NBT with energy value.
	 */
	void readEnergyNBT(NBTBase nbt);

	/**
	 * Writes energy value as NBT data.
	 * 
	 * @return NBT with energy value.
	 */
	NBTBase writeEnergyNBT();
}
