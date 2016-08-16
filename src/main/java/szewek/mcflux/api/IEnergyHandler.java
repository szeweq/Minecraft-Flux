package szewek.mcflux.api;

import net.minecraft.nbt.NBTBase;

public interface IEnergyHandler {
	/**
	 * Saves energy value to a NBT-formatted data.
	 * 
	 * @return NBT data with energy value.
	 */
	NBTBase saveEnergyNBT();

	/**
	 * Loads energy value from a NBT-formatted data.
	 * 
	 * @param nbt NBT data with energy value.
	 */
	void loadEnergyNBT(NBTBase nbt);
}
