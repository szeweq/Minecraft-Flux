package szewek.mcflux.api.flavor;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;

public interface IFlavorEnergyConsumer extends INBTSerializable<NBTBase> {
	/**
	 * Sided version of consumeFlavorEnergy.
	 * 
	 * @param from Facing side of object where flavored energy can be consumed.
	 * @param fe Flavored energy with specified amount available to be consumed.
	 * @param simulate If {@code true}, then consumer doesn't change its energy value.
	 * @return Amount of flavored energy consumed.
	 */
	default long consumeFlavorEnergy(EnumFacing from, FlavorEnergy fe, boolean simulate) {
		return consumeFlavorEnergy(fe, simulate);
	}

	/**
	 * Adds flavored energy to a consumer.
	 * 
	 * @param fe Flavored energy with specified amount available to be consumed.
	 * @param simulate If {@code true}, then consumer doesn't change its energy value.
	 * @return Amount of flavored energy consumed.
	 */
	long consumeFlavorEnergy(FlavorEnergy fe, boolean simulate);
}
