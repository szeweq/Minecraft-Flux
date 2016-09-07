package szewek.mcflux.wrapper;

import szewek.mcflux.api.IEnergyProducer;
import szewek.mcflux.api.ex.IEnergy;

public class EnergyWrapperProducer implements IEnergy {
	private final IEnergyProducer producer;

	public EnergyWrapperProducer(IEnergyProducer iep) {
		if (iep instanceof CompatEnergyWrapper) {
			producer = null;
			return;
		}
		producer = iep;
	}

	@Override public boolean canInputEnergy() {
		return false;
	}

	@Override public boolean canOutputEnergy() {
		return producer != null;
	}

	@Override public long inputEnergy(long amount, boolean sim) {
		return 0;
	}

	@Override public long outputEnergy(long amount, boolean sim) {
		return producer != null? producer.extractEnergy(amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount, sim) : 0;
	}

	@Override public long getEnergy() {
		return producer.getEnergy();
	}

	@Override public long getEnergyCapacity() {
		return producer.getEnergyCapacity();
	}
}
