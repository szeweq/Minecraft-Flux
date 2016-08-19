package szewek.mcflux.api.flavor;

public interface IFlavorEnergyStorage {
	/**
	 * Getter for flavored energy amount. Compatible with {@link szewek.mcflux.api.flavor.FlavorEnergy}
	 * 
	 * @return Flavored energy amount.
	 */
	long getAmount();

	/**
	 * Getter for flavored energy capacity.
	 * 
	 * @return Flavored energy capacity.
	 */
	long getCapacity();
}
