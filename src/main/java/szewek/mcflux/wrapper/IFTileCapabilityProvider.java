package szewek.mcflux.wrapper;

import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxConnection;
import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxProvider;
import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxReceiver;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.IEnergyProducer;

public class IFTileCapabilityProvider implements IEnergyProducer, IEnergyConsumer, ICapabilityProvider {
	private final IFluxProvider provider;
	private final IFluxReceiver receiver;
	private final IFluxConnection conn;

	IFTileCapabilityProvider(IFluxConnection ifc) {
		conn = ifc;
		provider = ifc instanceof IFluxProvider ? (IFluxProvider) ifc : null;
		receiver = ifc instanceof IFluxReceiver ? (IFluxReceiver) ifc : null;
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
				return receiver != null ? (T) this : null;
			if (cap == CapabilityEnergy.ENERGY_PRODUCER)
				return provider != null ? (T) this : null;
		}
		return null;
	}
	
	@Override
	public int getEnergy() {
		return provider != null ? provider.getEnergyStored(null) : receiver != null ? receiver.getEnergyStored(null): 0;
	}

	@Override
	public int getEnergyCapacity() {
		return provider != null ? provider.getMaxEnergyStored(null) : receiver != null ? receiver.getMaxEnergyStored(null): 0;
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
