package szewek.mcflux.api.flavor;

public interface IFlavorEnergyProducer {
	/**
	 * Grabs specified kind of flavored energy.
	 * @param fe Flavored energy with specified amount available to store and transfer.
	 * @param sim If {@code true}, then producer doesn't change its energy value.
	 * @return Amount of extracted energy. You should put that value into FlavorEnergy.
	 */
	long extractFlavorEnergy(FlavorEnergy fe, boolean sim);

	/**
	 * Grabs any kind of flavored energy.
	 * @param amount Flavored energy amount available to store and transfer.
	 * @param sim If {@code true}, then producer doesn't change its energy value.
	 * @return Flavored energy with extracted amount.
	 */
	FlavorEnergy extractAnyFlavorEnergy(long amount, boolean sim);
}
