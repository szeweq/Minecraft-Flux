package szewek.mcflux.api;

public interface IEnergyHolder {
	/**
	 * Getter of stored energy.
	 * @return Amount of stored energy.
	 */
	int getEnergy();

	/**
	 * Getter for energy capacity.
	 * @return Amount of energy capacity.
	 */
	int getEnergyCapacity();
}
