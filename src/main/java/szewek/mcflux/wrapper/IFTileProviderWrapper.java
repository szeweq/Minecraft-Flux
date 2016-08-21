package szewek.mcflux.wrapper;

import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxProvider;
import szewek.mcflux.api.IEnergyProducer;

public class IFTileProviderWrapper implements IEnergyProducer {
	private final IFluxProvider provider;
	
	public IFTileProviderWrapper(IFluxProvider provider) {
		this.provider = provider;
	}

	@Override
	public int getEnergy() {
		return provider.getEnergyStored(null);
	}

	@Override
	public int getEnergyCapacity() {
		return provider.getMaxEnergyStored(null);
	}

	@Override
	public int extractEnergy(int amount, boolean simulate) {
		return provider.extractEnergy(null, amount, simulate);
	}
}
