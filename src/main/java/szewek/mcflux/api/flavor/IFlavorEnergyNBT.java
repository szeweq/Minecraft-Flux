package szewek.mcflux.api.flavor;

import net.minecraft.nbt.NBTBase;

public interface IFlavorEnergyNBT {
	/**
	 * Reads flavor energy info from NBT data.
	 * 
	 * @param nbt NBT with flavor energy info.
	 */
	void readFlavorEnergyNBT(NBTBase nbt);

	/**
	 * Writes flavor energy info as NBT data.
	 * 
	 * @return NBT with flavor energy info.
	 */
	NBTBase writeFlavorEnergyNBT();
}
