package szewek.mcflux.wrapper;

import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.ex.IEnergy;

public class EnergyWrapperConsumer implements IEnergy {
	private final IEnergyConsumer consumer;

	public EnergyWrapperConsumer(IEnergyConsumer iec) {
		if (iec instanceof CompatEnergyWrapper) {
			consumer = null;
			return;
		}
		consumer = iec;
	}

	@Override public boolean canInputEnergy() {
		return consumer != null;
	}

	@Override public boolean canOutputEnergy() {
		return false;
	}

	@Override public long inputEnergy(long amount, boolean sim) {
		return consumer != null ? consumer.consumeEnergy(amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount, sim) : 0;
	}

	@Override public long outputEnergy(long amount, boolean sim) {
		return 0;
	}

	@Override public long getEnergy() {
		return consumer.getEnergy();
	}

	@Override public long getEnergyCapacity() {
		return consumer.getEnergyCapacity();
	}
}
