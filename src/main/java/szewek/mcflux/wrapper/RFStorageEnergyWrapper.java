package szewek.mcflux.wrapper;

import cofh.api.energy.IEnergyStorage;
import net.minecraft.nbt.NBTBase;
import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.IEnergyProducer;

public class RFStorageEnergyWrapper implements IEnergyProducer, IEnergyConsumer {
	private final IEnergyStorage storage;
	
	public RFStorageEnergyWrapper(IEnergyStorage storage) {
		this.storage = storage;
	}

	@Override
	public NBTBase serializeNBT() {
		return null;
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
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
