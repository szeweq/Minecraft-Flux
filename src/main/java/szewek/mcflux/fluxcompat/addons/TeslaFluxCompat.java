package szewek.mcflux.fluxcompat.addons;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.lib.TeslaUtils;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.U;
import szewek.mcflux.fluxcompat.EnergyType;
import szewek.mcflux.fluxcompat.FluxCompat;
import szewek.mcflux.fluxcompat.ForgeEnergyCapable;
import szewek.mcflux.fluxcompat.LazyEnergyCapProvider;
import szewek.mcflux.util.ErrMsg;
import szewek.mcflux.util.InjectCond;
import szewek.mcflux.util.MCFluxReport;

@FluxCompat.Addon(requires = InjectCond.MOD, args = {"tesla", "TESLA"})
public class TeslaFluxCompat implements FluxCompat.Lookup {
	@Override
	public void lookFor(LazyEnergyCapProvider lecp, FluxCompat.Registry r) {
		final ICapabilityProvider icp = lecp.getObject();
		if (icp == null) return;
		EnumFacing f = null;
		try {
			for (int i = 0; i < U.FANCY_FACING.length; i++) {
				f = U.FANCY_FACING[i];
				if (TeslaUtils.hasTeslaSupport(icp, f)) {
					r.register(EnergyType.TESLA, TeslaFluxCompat::factorize);
				}
			}
		} catch (Exception e) {
			MCFluxReport.addErrMsg(new ErrMsg.BadImplementation("TESLA", icp.getClass(), e, f));
		}
	}

	private static void factorize(LazyEnergyCapProvider lecp) {
		final ICapabilityProvider icp = lecp.getObject();
		Energy[] es = new Energy[7];
		EnumFacing f;
		for (int i = 0; i < U.FANCY_FACING.length; i++) {
			f = U.FANCY_FACING[i];
			es[i] = new Energy(icp, f);
		}
		lecp.update(es, new int[0], null, true);
	}

	private static final class Energy extends ForgeEnergyCapable implements FluxCompat.Convert {
		private final ITeslaHolder holder;
		private final ITeslaConsumer consumer;
		private final ITeslaProducer producer;

		Energy(ICapabilityProvider provider, EnumFacing f) {
			holder = TeslaUtils.getTeslaHolder(provider, f);
			consumer = TeslaUtils.getTeslaConsumer(provider, f);
			producer = TeslaUtils.getTeslaProducer(provider, f);
		}

		@Override public boolean canInputEnergy() {
			return consumer != null;
		}

		@Override public boolean canOutputEnergy() {
			return producer != null;
		}

		@Override public long inputEnergy(long amount, boolean sim) {
			return consumer != null ? consumer.givePower(amount, sim) : 0;
		}

		@Override public long outputEnergy(long amount, boolean sim) {
			return producer != null ? producer.takePower(amount, sim) : 0;
		}

		@Override public long getEnergy() {
			return holder != null ? holder.getStoredPower() : 0;
		}

		@Override public long getEnergyCapacity() {
			return holder != null ? holder.getCapacity() : 0;
		}

		@Override public boolean hasNoEnergy() {
			return holder != null && holder.getStoredPower() == 0;
		}

		@Override public boolean hasFullEnergy() {
			return holder != null && holder.getStoredPower() == holder.getCapacity();
		}

		@Override
		public EnergyType getEnergyType() {
			return EnergyType.TESLA;
		}
	}
}