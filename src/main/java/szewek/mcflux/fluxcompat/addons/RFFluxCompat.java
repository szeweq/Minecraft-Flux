package szewek.mcflux.fluxcompat.addons;

import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;
import szewek.fl.energy.IEnergy;
import szewek.mcflux.U;
import szewek.mcflux.fluxcompat.EnergyType;
import szewek.mcflux.fluxcompat.FluxCompat;
import szewek.mcflux.fluxcompat.LazyEnergyCapProvider;
import szewek.mcflux.network.CloudUtils;
import szewek.mcflux.util.InjectCond;

@FluxCompat.Addon(requires = InjectCond.MOD, args = {"redstoneflux"})
public class RFFluxCompat implements FluxCompat.Lookup {
	@Override
	public void lookFor(LazyEnergyCapProvider lecp, FluxCompat.Registry r) {
		final ICapabilityProvider cp = lecp.getObject();
		if (cp == null || !(cp instanceof IEnergyHandler)) return;
		r.register(EnergyType.RF, RFFluxCompat::tileFactorize);
	}

	private static void tileFactorize(LazyEnergyCapProvider lecp) {
		final IEnergyHandler eh = (IEnergyHandler) lecp.getObject();
		if (eh == null) return;
		final IEnergyProvider ep = eh instanceof IEnergyProvider ? (IEnergyProvider) eh : null;
		final IEnergyReceiver er = eh instanceof IEnergyReceiver ? (IEnergyReceiver) eh : null;
		final RFDelegate rfd = new RFDelegate(eh, ep, er);
		final EnergyTile[] ets = new EnergyTile[7];
		for (int i = 0; i < U.FANCY_FACING.length; i++) ets[i] = new EnergyTile(rfd, U.FANCY_FACING[i]);
		lecp.update(ets, new int[0], eh::canConnectEnergy, true);
		CloudUtils.reportEnergy(eh.getClass(), null, "rf");
	}

	private static final class RFDelegate {
		private final IEnergyHandler handler;
		private final IEnergyProvider provider;
		private final IEnergyReceiver receiver;

		private RFDelegate(IEnergyHandler h, IEnergyProvider p, IEnergyReceiver r) {
			handler = h;
			provider = p;
			receiver = r;
		}
	}

	private static final class EnergyTile implements IEnergy, FluxCompat.Convert, IEnergyStorage {
		private final EnumFacing face;
		private final RFDelegate delegate;

		EnergyTile(RFDelegate rfd, EnumFacing f) {
			face = f;
			delegate = rfd;
		}

		@Override
		public long getEnergy() {
			return delegate.handler.getEnergyStored(face);
		}

		@Override
		public long getEnergyCapacity() {
			return delegate.handler.getMaxEnergyStored(face);
		}

		@Override public boolean canInputEnergy() {
			return delegate.receiver != null;
		}

		@Override public boolean canOutputEnergy() {
			return delegate.provider != null;
		}

		@Override
		public long inputEnergy(long amount, boolean sim) {
			return delegate.receiver != null ? delegate.receiver.receiveEnergy(face, amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount, sim) : 0;
		}

		@Override
		public long outputEnergy(long amount, boolean sim) {
			return delegate.provider != null ? delegate.provider.extractEnergy(face, amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount, sim) : 0;
		}

		@Override public int receiveEnergy(int maxReceive, boolean simulate) {
			return delegate.receiver != null ? delegate.receiver.receiveEnergy(face, maxReceive, simulate) : 0;
		}

		@Override public int extractEnergy(int maxExtract, boolean simulate) {
			return delegate.provider != null ? delegate.provider.extractEnergy(face, maxExtract, simulate): 0;
		}

		@Override public int getEnergyStored() {
			return delegate.handler.getEnergyStored(face);
		}

		@Override public int getMaxEnergyStored() {
			return delegate.handler.getMaxEnergyStored(face);
		}

		@Override public boolean canExtract() {
			return delegate.provider != null;
		}

		@Override public boolean canReceive() {
			return delegate.receiver != null;
		}

		@Override public boolean hasNoEnergy() {
			return delegate.handler.getEnergyStored(face) == 0;
		}

		@Override public boolean hasFullEnergy() {
			return delegate.handler.getEnergyStored(face) == delegate.handler.getMaxEnergyStored(face);
		}

		@Override
		public EnergyType getEnergyType() {
			return EnergyType.RF;
		}
	}
}
