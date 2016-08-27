package szewek.mcflux.api.flavor;

public interface IFlavorEnergyConsumer {
	/**
	 * Adds flavored energy to a consumer.
	 * 
	 * @param fe Flavored energy with specified amount available to be consumed.
	 * @param sim If {@code true}, then consumer doesn't change its energy value.
	 * @return Amount of flavored energy consumed.
	 */
	long consumeFlavorEnergy(FlavorEnergy fe, boolean sim);
}
