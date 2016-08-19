package szewek.mcflux.api.flavor;

public interface IFlavorEnergyHolder {
	/**
	 * Get information about all flavored energy storage.
	 * 
	 * @return Array of flavored energy storage.
	 */
	IFlavorEnergyStorage[] getAllFlavors();

	/**
	 * Get information about single flavored energy storage.
	 * 
	 * @param fe Flavored energy (amount is ignored).
	 * @return Flavored energy storage.
	 */
	IFlavorEnergyStorage getFlavor(FlavorEnergy fe);
}
