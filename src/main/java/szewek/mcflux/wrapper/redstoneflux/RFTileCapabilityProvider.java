package szewek.mcflux.wrapper.redstoneflux;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.IEnergyProducer;

public class RFTileCapabilityProvider implements ICapabilityProvider {
	private final IEnergyProvider provider;
	private final IEnergyReceiver receiver;
	private final IEnergyHandler handler;
	private final Sided[] sides = new Sided[7];

	public RFTileCapabilityProvider(IEnergyHandler ieh) {
		handler = ieh;
		provider = ieh instanceof IEnergyProvider ? (IEnergyProvider) ieh : null;
		receiver = ieh instanceof IEnergyReceiver ? (IEnergyReceiver) ieh : null;
		for (int i = 0; i < 6; i++)
			sides[i] = new Sided(EnumFacing.VALUES[i]);
		sides[6] = new Sided(null);
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
			return handler.getEnergyStored(face);
		}

		@Override
		public int getEnergyCapacity() {
			return handler.getMaxEnergyStored(face);
		}

		@Override
		public int extractEnergy(int amount, boolean sim) {
			return provider != null ? provider.extractEnergy(face, amount, sim) : 0;
		}

		@Override
		public int consumeEnergy(int amount, boolean sim) {
			return receiver != null ? receiver.receiveEnergy(face, amount, sim) : 0;
		}
	}
}
