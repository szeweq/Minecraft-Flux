package szewek.mcflux.api;

@Deprecated
public interface IEnergyProducer extends IEnergyHolder {
	/**
	 * Grabs energy from a producer.
	 * 
	 * @param amount Energy amount available to store and transfer.
	 * @param sim If {@code true}, then producer doesn't change its energy value.
	 * @return Amount of extracted energy.
	 */
	int extractEnergy(int amount, boolean sim);
}
