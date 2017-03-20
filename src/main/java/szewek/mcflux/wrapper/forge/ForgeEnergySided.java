package szewek.mcflux.wrapper.forge;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.wrapper.EnergyType;

final class ForgeEnergySided implements IEnergy, EnergyType.Converter {
	private final IEnergyStorage storage;
	private final boolean notEmpty;

	ForgeEnergySided(ICapabilityProvider icp, EnumFacing f) {
		storage = icp.getCapability(CapabilityEnergy.ENERGY, f);
		notEmpty = storage != null;
	}

	@Override public boolean canInputEnergy() {
		return notEmpty && storage.canReceive();
	}

	@Override public boolean canOutputEnergy() {
		return notEmpty && storage.canExtract();
	}

	@Override public long inputEnergy(long amount, boolean sim) {
		return notEmpty ? storage.receiveEnergy(amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount, sim) : 0;
	}

	@Override public long outputEnergy(long amount, boolean sim) {
		return notEmpty ? storage.extractEnergy(amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount, sim) : 0;
	}

	@Override public long getEnergy() {
		return storage.getEnergyStored();
	}

	@Override public long getEnergyCapacity() {
		return storage.getMaxEnergyStored();
	}

	@Override public boolean hasNoEnergy() {
		return notEmpty && storage.getEnergyStored() == 0;
	}

	@Override public boolean hasFullEnergy() {
		return notEmpty && storage.getEnergyStored() == storage.getMaxEnergyStored();
	}

	@Override public EnergyType getEnergyType() {
		return EnergyType.FORGE_ENERGY;
	}
}
