package szewek.mcflux.util;

import net.minecraftforge.energy.IEnergyStorage;
import szewek.mcflux.api.ex.IEnergy;

import static java.lang.Integer.MAX_VALUE;

public final class ForgeEnergyCompat implements IEnergyStorage {
	private final IEnergy iEnergy;

	public ForgeEnergyCompat(IEnergy ie) {
		iEnergy = ie;
	}

	@Override public int receiveEnergy(int maxReceive, boolean sim) {
		return (int) iEnergy.inputEnergy(maxReceive, sim);
	}

	@Override public int extractEnergy(int maxExtract, boolean sim) {
		return (int) iEnergy.outputEnergy(maxExtract, sim);
	}

	@Override public int getEnergyStored() {
		long l = iEnergy.getEnergy();
		return l > MAX_VALUE ? MAX_VALUE : (int) l;
	}

	@Override public int getMaxEnergyStored() {
		long l = iEnergy.getEnergyCapacity();
		return l > MAX_VALUE ? MAX_VALUE : (int) l;
	}

	@Override public boolean canExtract() {
		return iEnergy.canOutputEnergy();
	}

	@Override public boolean canReceive() {
		return iEnergy.canInputEnergy();
	}
}
