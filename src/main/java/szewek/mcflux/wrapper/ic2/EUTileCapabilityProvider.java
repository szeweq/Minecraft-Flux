package szewek.mcflux.wrapper.ic2;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.prefab.BasicSink;
import ic2.api.energy.prefab.BasicSource;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.wrapper.CompatEnergyWrapper;

import java.lang.reflect.Method;
import java.util.function.DoubleSupplier;

import static szewek.mcflux.config.MCFluxConfig.CFG_EU_VALUE;

class EUTileCapabilityProvider implements ICapabilityProvider {
	@CapabilityInject(EUTileCapabilityProvider.class)
	static Capability<EUTileCapabilityProvider> SELF_CAP = null;

	private IEnergySource source = null;
	private IEnergySink sink = null;
	private Sided[] sides = new Sided[7];
	private CompatEnergyWrapper[] compatSides = new CompatEnergyWrapper[7];
	private DoubleSupplier capMethod = null, energyMethod = null;

	EUTileCapabilityProvider() {
		for (int i = 0; i < 6; i++) {
			sides[i] = new Sided(EnumFacing.VALUES[i]);
			compatSides[i] = new CompatEnergyWrapper(sides[i]);
		}
		sides[6] = new Sided(null);
		compatSides[6] = new CompatEnergyWrapper(sides[6]);
	}

	void updateEnergyTile(IEnergyTile iet) {
		source = iet instanceof IEnergySource ? (IEnergySource) iet : null;
		sink = iet instanceof IEnergySink ? (IEnergySink) iet : null;
		if (capMethod == null)
			capMethod = iet instanceof BasicSource ? ((BasicSource) iet)::getCapacity : iet instanceof BasicSink ? ((BasicSink) iet)::getCapacity : null;
		if (energyMethod == null)
			energyMethod = iet instanceof BasicSource ? ((BasicSource) iet)::getEnergyStored : iet instanceof BasicSink ? ((BasicSink) iet)::getEnergyStored : null;
	}

	void updateEnergyMethods(Object o, Method cm, Method em) {
		capMethod = () -> {
			try {
				return (double) cm.invoke(o);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		};
		energyMethod = () -> {
			try {
				return (double) em.invoke(o);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		};
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		if (cap == IEnergy.CAP_ENERGY)
			return source != null || sink != null;
		if (cap == SELF_CAP)
			return true;
		CompatEnergyWrapper cew = compatSides[f == null ? 6 : f.getIndex()];
		return cew.isCompatInputSuitable(cap) || cew.isCompatOutputSuitable(cap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		if (cap == SELF_CAP)
			return (T) this;
		int g = f == null ? 6 : f.getIndex();
		if (cap == IEnergy.CAP_ENERGY)
			return source != null || sink != null ? (T) sides[g] : null;
		CompatEnergyWrapper cew = compatSides[g];
		return cew.isCompatInputSuitable(cap) || cew.isCompatOutputSuitable(cap) ? (T) cew : null;
	}

	private class Sided implements IEnergy {
		private final EnumFacing face;

		private Sided(EnumFacing f) {
			face = f;
		}

		@Override
		public long getEnergy() {
			double dc = 0;
			if (energyMethod != null)
				dc = energyMethod.getAsDouble();
			return (long) (dc * CFG_EU_VALUE);
		}

		@Override
		public long getEnergyCapacity() {
			double dc = 0;
			if (capMethod != null)
				dc = capMethod.getAsDouble();
			return (long) (dc * CFG_EU_VALUE);
		}

		@Override public boolean canInputEnergy() {
			return sink != null && sink.acceptsEnergyFrom(null, face);
		}

		@Override public boolean canOutputEnergy() {
			return source != null && source.emitsEnergyTo(null, face);
		}

		@Override
		public long inputEnergy(long amount, boolean sim) {
			if (amount < CFG_EU_VALUE)
				return 0;
			if (sink != null) {
				long e = (long) sink.getDemandedEnergy() * CFG_EU_VALUE;
				long r = amount - (amount % CFG_EU_VALUE);
				if (r > e)
					r = e;
				if (!sim) {
					sink.injectEnergy(face, r / CFG_EU_VALUE, EnergyNet.instance.getPowerFromTier(sink.getSinkTier()));
				}
				return r;
			}
			return 0;
		}

		@Override
		public long outputEnergy(long amount, boolean sim) {
			if (amount < CFG_EU_VALUE)
				return 0;
			if (source != null) {
				long e = (long) source.getOfferedEnergy() * CFG_EU_VALUE;
				long r = amount - (amount % CFG_EU_VALUE);
				if (r > e)
					r = e;
				if (!sim) {
					source.drawEnergy(r / CFG_EU_VALUE);
				}
				return r;
			}
			return 0;
		}
	}
}
