package szewek.mcflux.api.ex;

public class SidedEnergyAccess implements IEnergy {
	private final IEnergy ie;
	private final boolean input, output;

	public SidedEnergyAccess(IEnergy parent, boolean in, boolean out) {
		ie = parent;
		input = in;
		output = out;
	}

	@Override
	public boolean canInputEnergy() {
		return input;
	}

	@Override
	public boolean canOutputEnergy() {
		return output;
	}

	@Override
	public long inputEnergy(long amount, boolean sim) {
		if (input)
			return ie.inputEnergy(amount, sim);
		return 0;
	}

	@Override
	public long outputEnergy(long amount, boolean sim) {
		if (output)
			return ie.outputEnergy(amount, sim);
		return 0;
	}

	@Override
	public long getEnergy() {
		return ie.getEnergy();
	}

	@Override
	public long getEnergyCapacity() {
		return ie.getEnergyCapacity();
	}
}
