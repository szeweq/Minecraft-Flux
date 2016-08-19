package szewek.mcflux.api;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;

public interface IEnergyProducer extends IEnergyHolder, INBTSerializable<NBTBase> {
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
	 * @param amount Energy amount available to store and transfer.
	 * @param simulate If {@code true}, then producer doesn't change its energy value.
	 * @return Amount of extracted energy.
	 */
	int extractEnergy(int amount, boolean simulate);
}
