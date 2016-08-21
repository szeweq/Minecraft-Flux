package szewek.mcflux.api;

import net.minecraft.util.EnumFacing;

public interface IEnergyConsumer extends IEnergyHolder {
	/**
	 * Sided version of consumeEnergy.
	 * 
	 * @param from Facing side of object where energy can be consumed.
	 * @param amount Energy amount available to be consumed.
	 * @param simulate If {@code true}, then consumer doesn't change its energy value.
	 * @return Amount of energy consumed.
	 */
	default int consumeEnergy(EnumFacing from, int amount, boolean simulate) {
		return consumeEnergy(amount, simulate);
	}

	/**
	 * Adds energy to a consumer.
	 * 
	 * @param amount Energy amount available to be consumed.
	 * @param simulate If {@code true}, then consumer doesn't change its energy value.
	 * @return Amount of energy consumed.
	 */
	int consumeEnergy(int amount, boolean simulate);
}
