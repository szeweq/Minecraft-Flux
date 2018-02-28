package szewek.mcflux.fluxcompat.addons;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.prefab.BasicSink;
import ic2.api.energy.prefab.BasicSource;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.fl.util.JavaUtils;
import szewek.mcflux.U;
import szewek.mcflux.fluxcompat.EnergyType;
import szewek.mcflux.fluxcompat.FluxCompat;
import szewek.mcflux.fluxcompat.ForgeEnergyCapable;
import szewek.mcflux.fluxcompat.LazyEnergyCapProvider;
import szewek.mcflux.network.CloudUtils;
import szewek.mcflux.util.InjectCond;
import szewek.mcflux.util.MCFluxReport;

import java.lang.reflect.Method;
import java.util.function.DoubleSupplier;

import static szewek.mcflux.MCFlux.L;
import static szewek.mcflux.config.MCFluxConfig.CFG_EU_VALUE;

@FluxCompat.Addon(requires = InjectCond.MOD, args = {"IC2", "IndustrialCraft 2"})
public class IC2FluxCompat implements FluxCompat.Lookup {
	private final Class<?> IC2_TEB, IC2_ENERGY;
	private final Method COMPONENT, CAPACITY, ENERGY;
	private final boolean broken;

	public IC2FluxCompat() {
		IC2_TEB = JavaUtils.getClassSafely("ic2.core.block.TileEntityBlock");
		IC2_ENERGY = JavaUtils.getClassSafely("ic2.core.block.comp.Energy");
		if (IC2_TEB == null || IC2_ENERGY == null) {
			COMPONENT = CAPACITY = ENERGY = null;
		} else {
			COMPONENT = JavaUtils.getMethodSafely(IC2_TEB, "getComponent", Class.class);
			CAPACITY = JavaUtils.getMethodSafely(IC2_ENERGY, "getCapacity");
			ENERGY = JavaUtils.getMethodSafely(IC2_ENERGY, "getEnergy");
		}
		broken = COMPONENT == null || CAPACITY == null || ENERGY == null;
		if (broken)
			L.warn("IC2FluxCompat is broken");
	}

	@Override
	public void lookFor(LazyEnergyCapProvider lecp, FluxCompat.Registry r) {
		final ICapabilityProvider cp = lecp.getObject();
		final String cn = cp.getClass().getName();
		if (cn == null) {
			L.warn("IC2FluxCompat: An object doesn't have a class name: " + cp);
			return;
		}
		if (cn.startsWith("ic2.core") || cn.startsWith("cpw.mods.compactsolars"))
			r.register(EnergyType.EU, this::tileFactorize);
	}

	private void tileFactorize(LazyEnergyCapProvider lecp) {
		if (broken) return;
		final ICapabilityProvider cp = lecp.getObject();
		if (cp == null || !(cp instanceof TileEntity)) return;
		final TileEntity te = (TileEntity) cp;
		final IEnergyTile et = EnergyNet.instance.getTile(te.getWorld(), te.getPos());
		final IEnergySource esrc = et instanceof IEnergySource ? (IEnergySource) et : null;
		final IEnergySink esnk = et instanceof IEnergySink ? (IEnergySink) et : null;
		DoubleSupplier cfunc = et instanceof BasicSource ? ((BasicSource) et)::getCapacity : et instanceof BasicSink ? ((BasicSink) et)::getCapacity : null;
		DoubleSupplier efunc = et instanceof BasicSource ? ((BasicSource) et)::getEnergyStored : et instanceof BasicSink ? ((BasicSink) et)::getEnergyStored : null;
		if (IC2_TEB.isInstance(cp)) {
			Object o = null;
			try {
				o = COMPONENT.invoke(cp, IC2_ENERGY);
			} catch (Exception e) {
				MCFluxReport.sendException(e, "[IC2] FluxCompat factorize");
			}
			cfunc = doubleFunc(CAPACITY, o);
			efunc = doubleFunc(ENERGY, o);
		}
		final EUDelegate eud = new EUDelegate(cfunc, efunc, esnk, esrc);
		final EnergyTile[] es = new EnergyTile[7];
		for (int i = 0; i < U.FANCY_FACING.length; i++) {
			EnumFacing f = U.FANCY_FACING[i];
			es[i] = new EnergyTile(eud, f);
		}
		lecp.update(es, new int[0], null, true);
		CloudUtils.reportEnergy(cp.getClass(), et.getClass(), "ic2");
	}

	private static DoubleSupplier doubleFunc(final Method m, final Object o) {
		return () -> {
			try {
				return (double) m.invoke(o);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		};
	}

	private static final class EUDelegate {
		private final DoubleSupplier capMethod, energyMethod;
		private final IEnergySink sink;
		private final IEnergySource source;

		private EUDelegate(DoubleSupplier cfunc, DoubleSupplier efunc, IEnergySink snk, IEnergySource src) {
			capMethod = cfunc;
			energyMethod = efunc;
			sink = snk;
			source = src;
		}
	}

	private static final class EnergyTile extends ForgeEnergyCapable implements FluxCompat.Convert {
		private final EnumFacing face;
		private final EUDelegate delegate;

		EnergyTile(EUDelegate eud, EnumFacing f) {
			face = f;
			delegate = eud;
		}

		@Override
		public long getEnergy() {
			double dc = 0;
			if (delegate.energyMethod != null)
				dc = delegate.energyMethod.getAsDouble();
			return (long) (dc * CFG_EU_VALUE);
		}

		@Override
		public long getEnergyCapacity() {
			double dc = 0;
			if (delegate.capMethod != null)
				dc = delegate.capMethod.getAsDouble();
			return (long) (dc * CFG_EU_VALUE);
		}

		@Override public boolean canInputEnergy() {
			return delegate.sink != null && delegate.sink.acceptsEnergyFrom(null, face);
		}

		@Override public boolean canOutputEnergy() {
			return delegate.source != null && delegate.source.emitsEnergyTo(null, face);
		}

		@Override
		public long inputEnergy(long amount, boolean sim) {
			if (amount < CFG_EU_VALUE) return 0;
			if (delegate.sink != null) {
				final long e = (long) delegate.sink.getDemandedEnergy() * CFG_EU_VALUE;
				long r = amount - (amount % CFG_EU_VALUE);
				if (r > e)
					r = e;
				if (!sim) {
					delegate.sink.injectEnergy(face, r / CFG_EU_VALUE, EnergyNet.instance.getPowerFromTier(delegate.sink.getSinkTier()));
				}
				return r;
			}
			return 0;
		}

		@Override
		public long outputEnergy(long amount, boolean sim) {
			if (amount < CFG_EU_VALUE) return 0;
			if (delegate.source != null) {
				final long e = (long) delegate.source.getOfferedEnergy() * CFG_EU_VALUE;
				long r = amount - (amount % CFG_EU_VALUE);
				if (r > e)
					r = e;
				if (!sim) {
					delegate.source.drawEnergy(r / CFG_EU_VALUE);
				}
				return r;
			}
			return 0;
		}

		@Override public boolean hasNoEnergy() {
			return delegate.energyMethod != null && delegate.energyMethod.getAsDouble() == 0;
		}

		@Override public boolean hasFullEnergy() {
			return delegate.energyMethod != null && delegate.capMethod != null && delegate.energyMethod.getAsDouble() == delegate.capMethod.getAsDouble();
		}

		@Override
		public EnergyType getEnergyType() {
			return EnergyType.EU;
		}
	}
}
