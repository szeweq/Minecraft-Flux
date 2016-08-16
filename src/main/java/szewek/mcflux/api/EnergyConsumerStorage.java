package szewek.mcflux.api;

public class EnergyConsumerStorage extends EnergyStorage implements IEnergyConsumer {
	public static EnergyConsumerStorage createDefault() {
		return new EnergyConsumerStorage(40000);
	}

	public EnergyConsumerStorage(int max) {
		super(max);
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
