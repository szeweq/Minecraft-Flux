package szewek.mcflux.wrapper;

import net.minecraftforge.common.capabilities.Capability;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.IEnergyProducer;
import szewek.mcflux.api.ex.IEnergy;

public class CompatEnergyWrapper implements IEnergyConsumer, IEnergyProducer {
	private final IEnergy ie;

	public CompatEnergyWrapper(IEnergy ie) {
		this.ie = ie;
	}

	public boolean isCompatInputSuitable(Capability<?> cap) {
		return cap == CapabilityEnergy.ENERGY_CONSUMER && ie.canInputEnergy();
	}
	public boolean isCompatOutputSuitable(Capability<?> cap) {
		return cap == CapabilityEnergy.ENERGY_PRODUCER && ie.canOutputEnergy();
	}

	@Override public int consumeEnergy(int amount, boolean sim) {
		return ie.canInputEnergy() ? (int) ie.inputEnergy(amount, sim) : 0;
	}

	@Override public int extractEnergy(int amount, boolean sim) {
		return ie.canOutputEnergy() ? (int) ie.outputEnergy(amount, sim) : 0;
	}

	@Override public int getEnergy() {
		long e = ie.getEnergy();
		return e > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) e;
	}

	@Override public int getEnergyCapacity() {
		long c = ie.getEnergyCapacity();
		return c > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) c;
	}
}
