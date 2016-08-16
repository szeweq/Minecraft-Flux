package szewek.mcflux.api;

import net.minecraft.nbt.NBTBase;

public interface IEnergyHandler {
	NBTBase saveEnergyNBT();
	void loadEnergyNBT(NBTBase nbt);
}
