package szewek.mcflux.wrapper;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTBase;
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
		// TODO Auto-generated method stub
		return receiver.getMaxEnergyStored(null);
	}

	@Override
	public NBTBase serializeNBT() {
		return null;
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
	}

	@Override
	public int consumeEnergy(int amount, boolean simulate) {
		return receiver.receiveEnergy(null, amount, simulate);
	}
}
