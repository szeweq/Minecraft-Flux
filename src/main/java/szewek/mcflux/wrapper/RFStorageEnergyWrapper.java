package szewek.mcflux.wrapper;

import cofh.api.energy.IEnergyStorage;
import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.IEnergyProducer;

public class RFStorageEnergyWrapper implements IEnergyProducer, IEnergyConsumer {
	private final IEnergyStorage storage;
	
	public RFStorageEnergyWrapper(IEnergyStorage storage) {
		this.storage = storage;
	}

	@Override
	public int extractEnergy(int amount, boolean simulate) {
		return storage.extractEnergy(amount, simulate);
	}

	@Override
	public int consumeEnergy(int amount, boolean simulate) {
		return storage.receiveEnergy(amount, simulate);
	}

	@Override
	public int getEnergy() {
		return storage.getEnergyStored();
	}

	@Override
	public int getEnergyCapacity() {
		return storage.getMaxEnergyStored();
	}

}
