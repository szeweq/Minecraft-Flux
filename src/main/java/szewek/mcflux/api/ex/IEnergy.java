package szewek.mcflux.api.ex;

/**
 * NEW Energy Implementation
 * 
 * @author Szewek
 */
public interface IEnergy {
	/**
	 * Checks if object can accept energy input.
	 * 
	 * @return {@code true} if accepted, otherwise {@code false}.
	 */
	boolean canInputEnergy();

	/**
	 * Checks if object can accept energy output.
	 * 
	 * @return {@code true} if accepted, otherwise {@code false}.
	 */
	boolean canOutputEnergy();

	/**
	 * Energy input method
	 * 
	 * @param amount Energy amount available for input.
	 * @param sim Simulation switch. Set to {@code false} only when you need to check actual energy input value.
	 * @return Amount of energy sent to object.
	 */
	long inputEnergy(long amount, boolean sim);

	/**
	 * Energy output method
	 * 
	 * @param amount Maximum energy amount to be output.
	 * @param sim Simulation switch. Set to {@code false} only when you need to check actual energy output value.
	 * @return Amount of energy received from object.
	 */
	long outputEnergy(long amount, boolean sim);

	/**
	 * Getter of stored energy.
	 * 
	 * @return Amount of stored energy.
	 */
	long getEnergy();

	/**
	 * Getter for energy capacity.
	 * 
	 * @return Amount of energy capacity.
	 */
	long getEnergyCapacity();

	/**
	 * Use this only if energy value is being read from external data (like NBT).
	 * 
	 * @param amount Energy amount
	 */
	default void setEnergy(long amount) {
	}
}
