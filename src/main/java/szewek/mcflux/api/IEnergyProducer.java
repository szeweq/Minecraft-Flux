package szewek.mcflux.api;

import net.minecraft.util.EnumFacing;

public interface IEnergyProducer extends IEnergyHandler {
	/**
	 * Sided version of extractEnergy.
	 * 
	 * @param from Facing side of object where energy can be extracted.
	 * @param amount Energy amount available to store and transfer.
	 * @param simulate If {@code true}, then producer doesn't change its energy value.
	 * @return Amount of extracted energy.
	 */
	default int extractEnergy(EnumFacing from, int amount, boolean simulate) {
		return extractEnergy(amount, simulate);
	}

	/**
	 * Grabs energy from a producer.
	 * 
	 * @param from Facing side of object where energy can be extracted.
	 * @param amount Energy amount available to store and transfer.
	 * @param simulate If {@code true}, then producer doesn't change its energy value.
	 * @return Amount of extracted energy.
	 */
	int extractEnergy(int amount, boolean simulate);
}
