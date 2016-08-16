package szewek.mcflux.api;

import net.minecraft.util.EnumFacing;

public interface IEnergyProducer extends IEnergyHandler {
	int extractEnergy(EnumFacing from, int amount, boolean simulate);
}
