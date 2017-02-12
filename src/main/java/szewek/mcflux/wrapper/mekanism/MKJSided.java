package szewek.mcflux.wrapper.mekanism;

import mekanism.api.energy.IStrictEnergyAcceptor;
import net.minecraft.util.EnumFacing;
import szewek.mcflux.api.ex.IEnergy;

import static szewek.mcflux.config.MCFluxConfig.CFG_MKJ_VALUE;

final class MKJSided implements IEnergy {
	private final EnumFacing face;
	private final IStrictEnergyAcceptor acceptor;

	MKJSided(IStrictEnergyAcceptor isea, EnumFacing f) {
		face = f;
		acceptor = isea;
	}

	@Override public boolean canInputEnergy() {
		return acceptor.canReceiveEnergy(face);
	}

	@Override public boolean canOutputEnergy() {
		return acceptor.canReceiveEnergy(face);
	}

	@Override public long inputEnergy(long amount, boolean sim) {
		if (!sim)
			return (long) (acceptor.transferEnergyToAcceptor(face, amount / CFG_MKJ_VALUE) * CFG_MKJ_VALUE);
		long r = (long) ((acceptor.getMaxEnergy() - acceptor.getEnergy()) / CFG_MKJ_VALUE);
		return amount > r ? r : amount;
	}

	@Override public long outputEnergy(long amount, boolean sim) {
		if (!sim)
			return (long) (acceptor.transferEnergyToAcceptor(face, -amount / CFG_MKJ_VALUE) * CFG_MKJ_VALUE);
		long r = (long) (acceptor.getEnergy() / CFG_MKJ_VALUE);
		return amount > r ? r : amount;
	}

	@Override public long getEnergy() {
		return (long) (acceptor.getEnergy() * CFG_MKJ_VALUE);
	}

	@Override public long getEnergyCapacity() {
		return (long) (acceptor.getMaxEnergy() * CFG_MKJ_VALUE);
	}
}
