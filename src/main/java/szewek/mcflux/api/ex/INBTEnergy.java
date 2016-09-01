package szewek.mcflux.api.ex;

import net.minecraft.nbt.NBTBase;

public interface INBTEnergy {
	/**
	 * Reads energy value from NBT data.
	 * 
	 * @param nbt NBT with energy value.
	 */
	void readNBTEnergy(NBTBase nbt);

	/**
	 * Writes energy value as NBT data.
	 * 
	 * @return NBT with energy value.
	 */
	NBTBase writeNBTEnergy();
}
