package szewek.mcflux.wrapper;

import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxConnection;
import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxProvider;
import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxReceiver;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.CapabilityEnergy;

public class IFTileCapabilityProvider implements ICapabilityProvider {
	private final IFTileProviderWrapper provider;
	private final IFTileReceiverWrapper receiver;
	private final IFluxConnection conn;

	IFTileCapabilityProvider(IFluxConnection ifc) {
		conn = ifc;
		provider = ifc instanceof IFluxProvider ? new IFTileProviderWrapper((IFluxProvider) ifc) : null;
		receiver = ifc instanceof IFluxReceiver ? new IFTileReceiverWrapper((IFluxReceiver) ifc) : null;

	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		if (conn.canConnectEnergy(f)) {
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
		if (conn.canConnectEnergy(f)) {
			if (cap == CapabilityEnergy.ENERGY_CONSUMER)
				return (T) receiver;
			if (cap == CapabilityEnergy.ENERGY_PRODUCER)
				return (T) provider;
		}
		return null;
	}
}
