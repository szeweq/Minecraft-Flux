package szewek.mcflux.api;

import net.minecraft.util.EnumFacing;

public interface IEnergyConsumer extends IEnergyHandler {
	int consumeEnergy(EnumFacing from, int amount, boolean simulate);
}
