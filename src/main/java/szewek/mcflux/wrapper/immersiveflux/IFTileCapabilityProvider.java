package szewek.mcflux.wrapper.immersiveflux;

import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxConnection;
import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxProvider;
import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxReceiver;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.IEnergyProducer;

public class IFTileCapabilityProvider implements ICapabilityProvider {
	private final IFluxProvider provider;
	private final IFluxReceiver receiver;
	private final IFluxConnection conn;
	private final Sided[] sides = new Sided[7];

	public IFTileCapabilityProvider(IFluxConnection ifc) {
		conn = ifc;
		provider = ifc instanceof IFluxProvider ? (IFluxProvider) ifc : null;
		receiver = ifc instanceof IFluxReceiver ? (IFluxReceiver) ifc : null;
		for (int i = 0; i < 6; i++)
			sides[i] = new Sided(EnumFacing.VALUES[i]);
		sides[6] = new Sided(null);
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
			int g = f == null ? 6 : f.getIndex();
			if (cap == CapabilityEnergy.ENERGY_CONSUMER)
				return receiver != null ? (T) sides[g] : null;
			if (cap == CapabilityEnergy.ENERGY_PRODUCER)
				return provider != null ? (T) sides[g] : null;
		}
		return null;
	}

	public class Sided implements IEnergyProducer, IEnergyConsumer {
		private final EnumFacing face;

		private Sided(EnumFacing f) {
			face = f;
		}

		@Override
		public int getEnergy() {
			return provider != null ? provider.getEnergyStored(null) : receiver != null ? receiver.getEnergyStored(null) : 0;
		}

		@Override
		public int getEnergyCapacity() {
			return provider != null ? provider.getMaxEnergyStored(face) : receiver != null ? receiver.getMaxEnergyStored(face) : 0;
		}

		@Override
		public int consumeEnergy(int amount, boolean sim) {
			return receiver != null ? receiver.receiveEnergy(face, amount, sim) : 0;
		}

		@Override
		public int extractEnergy(int amount, boolean sim) {
			return provider != null ? provider.extractEnergy(face, amount, sim) : 0;
		}
	}
}
