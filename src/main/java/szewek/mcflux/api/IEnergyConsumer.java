package szewek.mcflux.api;

@Deprecated
public interface IEnergyConsumer extends IEnergyHolder {
	/**
	 * Adds energy to a consumer.
	 * 
	 * @param amount Energy amount available to be consumed.
	 * @param sim If {@code true}, then consumer doesn't change its energy value.
	 * @return Amount of energy consumed.
	 */
	int consumeEnergy(int amount, boolean sim);
}
