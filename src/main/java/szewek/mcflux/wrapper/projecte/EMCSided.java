package szewek.mcflux.wrapper.projecte;

import moze_intel.projecte.api.tile.IEmcAcceptor;
import moze_intel.projecte.api.tile.IEmcProvider;
import moze_intel.projecte.api.tile.IEmcStorage;
import net.minecraft.util.EnumFacing;
import szewek.mcflux.api.fe.Flavored;
import szewek.mcflux.api.fe.FlavoredImmutable;
import szewek.mcflux.api.fe.IFlavorEnergy;

import static szewek.mcflux.wrapper.projecte.ProjectEInjectRegistry.EMC;

public class EMCSided implements IFlavorEnergy {
	private static final Flavored[] emcFill = new Flavored[]{new FlavoredImmutable(EMC, null)};
	private final EnumFacing face;
	private final IEmcStorage storage;

	EMCSided(IEmcStorage emcs, EnumFacing f) {
		face = f;
		storage = emcs;
	}

	@Override public boolean canInputFlavorEnergy(Flavored fl) {
		return EMC.equals(fl.name) && storage.getStoredEmc() < storage.getMaximumEmc();
	}

	@Override public boolean canOutputFlavorEnergy(Flavored fl) {
		return EMC.equals(fl.name) && storage.getStoredEmc() > 0;
	}

	@Override public long inputFlavorEnergy(Flavored fl, boolean sim) {
		if (!EMC.equals(fl.name) || !(storage instanceof IEmcAcceptor))
			return 0;
		double c = storage.getMaximumEmc() - storage.getStoredEmc();
		long r = fl.getAmount();
		if (r > c)
			r = (long) c;
		if (!sim)
			r = (long) ((IEmcAcceptor) storage).acceptEMC(face, r);
		return r;
	}

	@Override public long outputFlavorEnergy(Flavored fl, boolean sim) {
		if (!EMC.equals(fl.name) || !(storage instanceof IEmcProvider))
			return 0;
		double c = storage.getStoredEmc();
		long r = fl.getAmount();
		if (r > c)
			r = (long) c;
		if (!sim)
			r = (long) ((IEmcProvider) storage).provideEMC(face, r);
		return r;
	}

	@Override public Flavored outputAnyFlavorEnergy(long amount, boolean sim) {
		double c = storage.getStoredEmc();
		if (c == 0)
			return null;
		long r = amount > c ? (long) c : amount;
		if (!sim)
			r = (long) ((IEmcProvider) storage).provideEMC(face, r);
		return new FlavoredImmutable(EMC, r, null);
	}

	@Override public long getFlavorEnergyAmount(Flavored fl) {
		return EMC.equals(fl.name) ? (long) storage.getStoredEmc() : 0;
	}

	@Override public long getFlavorEnergyCapacity(Flavored fl) {
		return EMC.equals(fl.name) ? (long) storage.getMaximumEmc() : 0;
	}

	@Override public Flavored[] allFlavorsContained() {
		return emcFill;
	}

	@Override public Flavored[] allFlavorsAcceptable() {
		return emcFill;
	}
}
