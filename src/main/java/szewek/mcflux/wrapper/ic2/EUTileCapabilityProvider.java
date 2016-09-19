package szewek.mcflux.wrapper.ic2;

import ic2.api.energy.prefab.BasicSink;
import ic2.api.energy.prefab.BasicSource;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.ex.EX;
import szewek.mcflux.wrapper.CompatEnergyWrapper;

import java.lang.reflect.Method;
import java.util.function.DoubleSupplier;

class EUTileCapabilityProvider implements ICapabilityProvider {
	@CapabilityInject(EUTileCapabilityProvider.class)
	static Capability<EUTileCapabilityProvider> SELF_CAP = null;

	private IEnergySource source = null;
	private IEnergySink sink = null;
	private EUSided[] sides = new EUSided[7];
	private CompatEnergyWrapper[] compatSides = new CompatEnergyWrapper[7];
	private DoubleSupplier capMethod = null, energyMethod = null;

	EUTileCapabilityProvider() {
		updateSides();
	}

	private void updateSides() {
		for (int i = 0; i < 6; i++) {
			sides[i] = new EUSided(capMethod, energyMethod, sink, source, EnumFacing.VALUES[i]);
			compatSides[i] = new CompatEnergyWrapper(sides[i]);
		}
		sides[6] = new EUSided(capMethod, energyMethod, sink, source, null);
		compatSides[6] = new CompatEnergyWrapper(sides[6]);
	}

	void updateEnergyTile(IEnergyTile iet) {
		source = iet instanceof IEnergySource ? (IEnergySource) iet : null;
		sink = iet instanceof IEnergySink ? (IEnergySink) iet : null;
		if (capMethod == null)
			capMethod = iet instanceof BasicSource ? ((BasicSource) iet)::getCapacity : iet instanceof BasicSink ? ((BasicSink) iet)::getCapacity : null;
		if (energyMethod == null)
			energyMethod = iet instanceof BasicSource ? ((BasicSource) iet)::getEnergyStored : iet instanceof BasicSink ? ((BasicSink) iet)::getEnergyStored : null;
		updateSides();
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
		updateSides();
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		if (cap == EX.CAP_ENERGY)
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
		if (cap == EX.CAP_ENERGY)
			return source != null || sink != null ? (T) sides[g] : null;
		CompatEnergyWrapper cew = compatSides[g];
		return cew.isCompatInputSuitable(cap) || cew.isCompatOutputSuitable(cap) ? (T) cew : null;
	}
}
