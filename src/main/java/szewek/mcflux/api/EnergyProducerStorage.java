package szewek.mcflux.api;

public class EnergyProducerStorage extends EnergyStorage implements IEnergyProducer {
	public static EnergyProducerStorage createDefault() {
		return new EnergyProducerStorage(40000);
	}

	public EnergyProducerStorage(int max) {
		super(max);
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

}
