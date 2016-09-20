package szewek.mcflux.api.fe;

public interface IFlavorEnergy {
	/**
	 * Checks if object can accept Flavor Energy input.
	 *
	 * @param fl Flavor Energy data (amount is ignored).
	 * @return {@code true} if accepted, otherwise {@code false}.
	 */
	boolean canInputFlavorEnergy(Flavored fl);

	/**
	 * Checks if object can accept Flavor Energy output.
	 *
	 * @param fl Flavor Energy data (amount is ignored).
	 * @return {@code true} if accepted, otherwise {@code false}.
	 */
	boolean canOutputFlavorEnergy(Flavored fl);

	/**
	 * Flavor Energy input method
	 *
	 * @param fl  Flavor Energy data with specified amount available to input.
	 * @param sim Simulation switch. Set to {@code false} only when you need to check actual energy input value.
	 * @return Amount of Flavor Energy sent to object.
	 */
	long inputFlavorEnergy(Flavored fl, boolean sim);

	/**
	 * Flavor Energy output method (if energy data is known).
	 *
	 * @param fl  Flavor Energy data with specified maximum amount to be output.
	 * @param sim Simulation switch. Set to {@code false} only when you need to check actual energy input value.
	 * @return Amount of Flavor Energy received from object.
	 */
	long outputFlavorEnergy(Flavored fl, boolean sim);

	/**
	 * Flavor Energy output method (if energy data is unknown).
	 *
	 * @param amount Flavor Energy amount
	 * @param sim    Simulation switch. Set to {@code false} only when you need to check actual energy input value.
	 * @return
	 */
	Flavored outputAnyFlavorEnergy(long amount, boolean sim);

	/**
	 * Getter for Flavor Energy amount specified by data.
	 *
	 * @param fl Flavor Energy data (amount is ignored).
	 * @return Stored Flavor Energy amount.
	 */
	long getFlavorEnergyAmount(Flavored fl);

	/**
	 * Getter for Flavor Energy capacity specified by data.
	 *
	 * @param fl Flavor Energy data (amount is ignored).
	 * @return Amount of Flavor Energy capacity.
	 */
	long getFlavorEnergyCapacity(Flavored fl);

	/**
	 * Lists all flavors that object currently contains.
	 * @return Array of Flavor Energy data (amount is ignored).
	 */
	Flavored[] allFlavorsContained();

	/**
	 * Lists all flavors that object can accept.
	 * @return Array of Flavor Energy data (amount is ignored).
	 */
	Flavored[] allFlavorsAcceptable();
}
