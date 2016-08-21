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
	
	/**
	 * Use this only if energy value is being read from external data (like NBT).
	 * @param amount Energy amount
	 */
	default void setEnergy(int amount) {}
}
