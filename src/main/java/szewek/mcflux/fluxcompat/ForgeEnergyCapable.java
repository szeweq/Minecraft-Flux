package szewek.mcflux.fluxcompat;

import szewek.fl.energy.IEnergy;

public abstract class ForgeEnergyCapable implements IEnergy, net.minecraftforge.energy.IEnergyStorage {
	@Override public int receiveEnergy(int maxReceive, boolean simulate) {
		return (int) inputEnergy(maxReceive, simulate);
	}

	@Override public int extractEnergy(int maxExtract, boolean simulate) {
		return (int) outputEnergy(maxExtract, simulate);
	}

	@Override public int getEnergyStored() {
		long e = getEnergy();
		return e > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) e;
	}

	@Override public int getMaxEnergyStored() {
		long e = getEnergyCapacity();
		return e > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) e;
	}

	@Override public boolean canExtract() {
		return canOutputEnergy();
	}

	@Override public boolean canReceive() {
		return canInputEnergy();
	}
}
