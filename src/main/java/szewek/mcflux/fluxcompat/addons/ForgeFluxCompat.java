package szewek.mcflux.fluxcompat.addons;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import szewek.fl.energy.IEnergy;
import szewek.mcflux.U;
import szewek.mcflux.fluxcompat.EnergyType;
import szewek.mcflux.fluxcompat.FluxCompat;
import szewek.mcflux.fluxcompat.LazyEnergyCapProvider;
import szewek.mcflux.network.CloudUtils;
import szewek.mcflux.tileentities.TileEntityFluxGen;
import szewek.mcflux.util.ErrMsg;
import szewek.mcflux.util.InjectCond;
import szewek.mcflux.util.MCFluxReport;

@FluxCompat.Addon(requires = InjectCond.CLASS, args = "net.minecraftforge.energy.IEnergyStorage")
public final class ForgeFluxCompat implements FluxCompat.Lookup {
	@Override
	public void lookFor(LazyEnergyCapProvider lecp, FluxCompat.Registry r) {
		final ICapabilityProvider icp = lecp.getObject();
		if (icp == null || icp instanceof TileEntityFluxGen || blacklist(icp)) return;
		EnumFacing f = null;
		try {
			for (int i = 0; i < U.FANCY_FACING.length; i++) {
				f = U.FANCY_FACING[i];
				if (icp.hasCapability(CapabilityEnergy.ENERGY, f)) {
					r.register(EnergyType.FORGE_ENERGY, ForgeFluxCompat::forgeFactorize);
				}
			}
		} catch (Exception e) {
			MCFluxReport.addErrMsg(new ErrMsg.BadImplementation("Forge Energy", icp.getClass(), e, f));
		}
	}

	private static void forgeFactorize(LazyEnergyCapProvider lecp) {
		final ICapabilityProvider icp = lecp.getObject();
		Energy[] es = new Energy[7];
		EnumFacing f;
		int[] s = new int[7];
		int x = 0, i;
		M:
		for (i = 0; i < U.FANCY_FACING.length; i++) {
			f = U.FANCY_FACING[i];
			IEnergyStorage ies = icp.getCapability(CapabilityEnergy.ENERGY, f);
			for (int j = 0; j < x; j++) {
				if (es[j].storage == ies) {
					s[i] = j;
					continue M;
				}
			}
			es[x] = new Energy(ies);
			s[i] = x++;
		}
		lecp.update(es, s, null, false);
		if (es[0] != null && es[0].storage != null)
			CloudUtils.reportEnergy(icp.getClass(), es[0].storage.getClass(), "forge");
	}

	private static boolean blacklist(Object o) {
		return o.getClass().getName().startsWith("ic2.core");
	}

	private static final class Energy implements IEnergy, FluxCompat.Convert {
		private final IEnergyStorage storage;

		private Energy(IEnergyStorage ies) {
			storage = ies;
		}

		@Override public boolean canInputEnergy() {
			return storage.canReceive();
		}

		@Override public boolean canOutputEnergy() {
			return storage.canExtract();
		}

		@Override public long inputEnergy(long amount, boolean sim) {
			return storage.receiveEnergy(amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount, sim);
		}

		@Override public long outputEnergy(long amount, boolean sim) {
			return storage.extractEnergy(amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount, sim);
		}

		@Override public long getEnergy() {
			return storage.getEnergyStored();
		}

		@Override public long getEnergyCapacity() {
			return storage.getMaxEnergyStored();
		}

		@Override public boolean hasNoEnergy() {
			return storage.getEnergyStored() == 0;
		}

		@Override public boolean hasFullEnergy() {
			return storage.getEnergyStored() == storage.getMaxEnergyStored();
		}

		@Override
		public EnergyType getEnergyType() {
			return EnergyType.FORGE_ENERGY;
		}
	}
}
