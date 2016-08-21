package szewek.mcflux.wrapper;

import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxReceiver;
import szewek.mcflux.api.IEnergyConsumer;

public class IFTileReceiverWrapper implements IEnergyConsumer {
	private final IFluxReceiver receiver;
	
	public IFTileReceiverWrapper(IFluxReceiver receiver) {
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
