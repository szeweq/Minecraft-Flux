package szewek.mcflux.api.flavor;

public interface IFlavorEnergyHolder {
	IFlavorEnergyStorage[] getAllFlavors();
	IFlavorEnergyStorage getFlavor(FlavorEnergy fe);
}
