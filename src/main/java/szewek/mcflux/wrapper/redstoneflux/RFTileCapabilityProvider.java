package szewek.mcflux.wrapper.redstoneflux;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.wrapper.CompatEnergyWrapper;

class RFTileCapabilityProvider implements ICapabilityProvider {
	private final boolean broken;
	private final IEnergyProvider provider;
	private final IEnergyReceiver receiver;
	private final IEnergyHandler handler;
	private final Sided[] sides = new Sided[7];
	private final CompatEnergyWrapper[] compatSides = new CompatEnergyWrapper[7];

	RFTileCapabilityProvider(IEnergyHandler ieh) {
		handler = ieh;
		provider = ieh instanceof IEnergyProvider ? (IEnergyProvider) ieh : null;
		receiver = ieh instanceof IEnergyReceiver ? (IEnergyReceiver) ieh : null;
		broken = provider == null && receiver == null;
		for (int i = 0; i < 6; i++) {
			sides[i] = new Sided(EnumFacing.VALUES[i]);
			compatSides[i] = new CompatEnergyWrapper(sides[i]);
		}
		sides[6] = new Sided(null);
		compatSides[6] = new CompatEnergyWrapper(sides[6]);
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		if (handler.canConnectEnergy(f)) {
			if (cap == IEnergy.CAP_ENERGY)
				return !broken;
			CompatEnergyWrapper cew = compatSides[f == null ? 6 : f.getIndex()];
			return cew.isCompatInputSuitable(cap) || cew.isCompatOutputSuitable(cap);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		if (handler.canConnectEnergy(f)) {
			int g = f == null ? 6 : f.getIndex();
			if (cap == IEnergy.CAP_ENERGY)
				return broken ? null : (T) sides[g];
			CompatEnergyWrapper cew = compatSides[g];
			if (cew.isCompatInputSuitable(cap) || cew.isCompatOutputSuitable(cap))
				return (T) cew;
		}
		return null;
	}

	private class Sided implements IEnergy {
		private final EnumFacing face;

		private Sided(EnumFacing f) {
			face = f;
		}

		@Override
		public long getEnergy() {
			return handler.getEnergyStored(face);
		}

		@Override
		public long getEnergyCapacity() {
			return handler.getMaxEnergyStored(face);
		}

		@Override public boolean canInputEnergy() {
			return receiver != null;
		}

		@Override public boolean canOutputEnergy() {
			return provider != null;
		}

		@Override
		public long inputEnergy(long amount, boolean sim) {
			return receiver != null ? receiver.receiveEnergy(face, amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount, sim) : 0;
		}

		@Override
		public long outputEnergy(long amount, boolean sim) {
			return provider != null ? provider.extractEnergy(face, amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount, sim) : 0;
		}
	}
}
