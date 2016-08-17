package szewek.mcflux.api;

public class EnergyBattery extends EnergyStorage implements IEnergyConsumer, IEnergyProducer {
	/**
	 * Creates a simple battery which can send energy in and out.
	 * @param max
	 */
	public EnergyBattery(int max) {
		super(max);
	}
	
	EnergyBattery() {
		super(40000);
	}

	@Override
	public int extractEnergy(int amount, boolean simulate) {
		if (amount == 0)
			return 0;
		int r = energy;
		if (amount < r)
			r = amount;
		if (!simulate)
			energy -= r;
		return r;
	}

	@Override
	public int consumeEnergy(int amount, boolean simulate) {
		if (amount == 0)
			return 0;
		int r = maxEnergy - energy;
		if (amount < r)
			r = amount;
		if (!simulate)
			energy += r;
		return r;
	}

}
