package szewek.mcflux.wrapper;

import cofh.api.energy.IEnergyReceiver;
import szewek.mcflux.api.IEnergyConsumer;

public class RFTileReceiverWrapper implements IEnergyConsumer {
	private final IEnergyReceiver receiver;
	
	public RFTileReceiverWrapper(IEnergyReceiver receiver) {
		this.receiver = receiver;
	}

	@Override
	public int getEnergy() {
		return receiver.getEnergyStored(null);
	}

	@Override
	public int getEnergyCapacity() {
		return receiver.getMaxEnergyStored(null);
	}

	@Override
	public int consumeEnergy(int amount, boolean simulate) {
		return receiver.receiveEnergy(null, amount, simulate);
	}
}
