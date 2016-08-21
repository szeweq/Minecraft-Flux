package szewek.mcflux.api.flavor;

import net.minecraft.util.EnumFacing;

public interface IFlavorEnergyProducer {
	/**
	 * Sided version of extractFlavorEnergy.
	 * @param from Facing side of object where flavored energy can be extracted.
	 * @param fe Flavored energy with specified amount available to store and transfer.
	 * @param simulate If {@code true}, then producer doesn't change its energy value.
	 * @return Amount of extracted energy. You should put that value into FlavorEnergy.
	 */
	default long extractFlavorEnergy(EnumFacing from, FlavorEnergy fe, boolean simulate) {
		return extractFlavorEnergy(fe, simulate);
	}

	/**
	 * Grabs specified kind of flavored energy.
	 * @param fe Flavored energy with specified amount available to store and transfer.
	 * @param simulate If {@code true}, then producer doesn't change its energy value.
	 * @return Amount of extracted energy. You should put that value into FlavorEnergy.
	 */
	long extractFlavorEnergy(FlavorEnergy fe, boolean simulate);

	/**
	 * Sided version of extractAnyFlavorEnergy.
	 * @param from Facing side of object where flavored energy can be extracted.
	 * @param amount Flavored energy amount available to store and transfer.
	 * @param simulate If {@code true}, then producer doesn't change its energy value.
	 * @return Flavored energy with extracted amount.
	 */
	default FlavorEnergy extractAnyFlavorEnergy(EnumFacing from, long amount, boolean simulate) {
		return extractAnyFlavorEnergy(amount, simulate);
	}

	/**
	 * Grabs any kind of flavored energy.
	 * @param amount Flavored energy amount available to store and transfer.
	 * @param simulate If {@code true}, then producer doesn't change its energy value.
	 * @return Flavored energy with extracted amount.
	 */
	FlavorEnergy extractAnyFlavorEnergy(long amount, boolean simulate);
}
