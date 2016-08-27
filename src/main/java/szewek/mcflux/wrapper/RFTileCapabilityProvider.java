package szewek.mcflux.wrapper;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.IEnergyProducer;

public class RFTileCapabilityProvider implements IEnergyProducer, IEnergyConsumer, ICapabilityProvider {
	private final IEnergyProvider provider;
	private final IEnergyReceiver receiver;
	private final IEnergyHandler handler;

	RFTileCapabilityProvider(IEnergyHandler ieh) {
		handler = ieh;
		provider = ieh instanceof IEnergyProvider ? (IEnergyProvider) ieh : null;
		receiver = ieh instanceof IEnergyReceiver ? (IEnergyReceiver) ieh : null;

	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		if (handler.canConnectEnergy(f)) {
			if (cap == CapabilityEnergy.ENERGY_CONSUMER)
				return receiver != null;
			if (cap == CapabilityEnergy.ENERGY_PRODUCER)
				return provider != null;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		if (handler.canConnectEnergy(f)) {
			if (cap == CapabilityEnergy.ENERGY_CONSUMER)
				return receiver != null ? (T) this : null;
			if (cap == CapabilityEnergy.ENERGY_PRODUCER)
				return provider != null ? (T) this : null;
		}
		return null;
	}

	@Override
	public int getEnergy() {
		return handler.getEnergyStored(null);
	}

	@Override
	public int getEnergyCapacity() {
		return handler.getMaxEnergyStored(null);
	}

	@Override
	public int extractEnergy(int amount, boolean sim) {
		return provider != null ? provider.extractEnergy(null, amount, sim) : 0;
	}

	@Override
	public int consumeEnergy(int amount, boolean sim) {
		return receiver != null ? receiver.receiveEnergy(null, amount, sim) : 0;
	}
}
