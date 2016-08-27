package szewek.mcflux.api;

/**
 * Basic implementation of energy producer and consumer.
 */
public class EnergyBattery extends EnergyStorage implements IEnergyConsumer, IEnergyProducer {
	/**
	 * Creates a simple battery which can send energy in and out.
	 * 
	 * @param max Energy capacity
	 */
	public EnergyBattery(int max) {
		super(max);
	}

	public EnergyBattery() {
		super(40000);
	}

	@Override
	public int extractEnergy(int amount, boolean sim) {
		if (amount == 0)
			return 0;
		int r = energy;
		if (amount < r)
			r = amount;
		if (!sim)
			energy -= r;
		return r;
	}

	@Override
	public int consumeEnergy(int amount, boolean sim) {
		if (amount == 0)
			return 0;
		int r = maxEnergy - energy;
		if (amount < r)
			r = amount;
		if (!sim)
			energy += r;
		return r;
	}
}
