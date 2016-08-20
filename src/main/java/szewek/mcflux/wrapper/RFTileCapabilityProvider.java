package szewek.mcflux.wrapper;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.CapabilityEnergy;

public class RFTileCapabilityProvider implements ICapabilityProvider {
	private final RFTileProviderWrapper provider;
	private final RFTileReceiverWrapper receiver;
	private final IEnergyHandler handler;

	RFTileCapabilityProvider(IEnergyHandler ieh) {
		handler = ieh;
		provider = ieh instanceof IEnergyProvider ? new RFTileProviderWrapper((IEnergyProvider) ieh) : null;
		receiver = ieh instanceof IEnergyReceiver ? new RFTileReceiverWrapper((IEnergyReceiver) ieh) : null;

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
				return (T) receiver;
			if (cap == CapabilityEnergy.ENERGY_PRODUCER)
				return (T) provider;
		}
		return null;
	}
}
