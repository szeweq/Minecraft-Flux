package szewek.mcflux.fluxcompat;

import szewek.fl.energy.IEnergy;

public final class LazyEnergy extends ForgeEnergyCapable implements FluxCompat.Convert {
	boolean notEnergy = false;
	IEnergy ie;

	@Override
	public boolean canInputEnergy() {
		return ie != null && ie.canInputEnergy();
	}

	@Override
	public boolean canOutputEnergy() {
		return ie != null && ie.canOutputEnergy();
	}

	@Override
	public long inputEnergy(long amount, boolean sim) {
		return ie != null ? ie.inputEnergy(amount, sim) : 0;
	}

	@Override
	public long outputEnergy(long amount, boolean sim) {
		return ie != null ? ie.outputEnergy(amount, sim) : 0;
	}

	@Override
	public long getEnergy() {
		return ie != null ? ie.getEnergy() : 0;
	}

	@Override
	public long getEnergyCapacity() {
		return ie != null ? ie.getEnergyCapacity() : 0;
	}

	public boolean isReady() {
		return ie != null;
	}

	@Override
	public EnergyType getEnergyType() {
		return notEnergy ? EnergyType.NONE : EnergyType.LAZY;
	}
}
