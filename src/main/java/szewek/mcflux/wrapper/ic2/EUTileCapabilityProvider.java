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
import net.minecraftforge.energy.CapabilityEnergy;
import szewek.mcflux.api.ex.EX;

import java.lang.reflect.Method;
import java.util.function.DoubleSupplier;

final class EUTileCapabilityProvider implements ICapabilityProvider {
	@CapabilityInject(EUTileCapabilityProvider.class)
	static Capability<EUTileCapabilityProvider> SELF_CAP = null;

	private boolean complete = true;
	private IEnergySource source = null;
	private IEnergySink sink = null;
	private EUSided[] sides = new EUSided[7];
	private DoubleSupplier capMethod = null, energyMethod = null;

	EUTileCapabilityProvider() {
		updateSides();
	}

	private void updateSides() {
		for (int i = 0; i < 6; i++) {
			sides[i] = new EUSided(capMethod, energyMethod, sink, source, EnumFacing.VALUES[i]);
		}
		sides[6] = new EUSided(capMethod, energyMethod, sink, source, null);
	}

	void updateEnergyTile(IEnergyTile iet) {
		source = iet instanceof IEnergySource ? (IEnergySource) iet : null;
		sink = iet instanceof IEnergySink ? (IEnergySink) iet : null;
		if (capMethod == null)
			capMethod = iet instanceof BasicSource ? ((BasicSource) iet)::getCapacity : iet instanceof BasicSink ? ((BasicSink) iet)::getCapacity : null;
		if (energyMethod == null)
			energyMethod = iet instanceof BasicSource ? ((BasicSource) iet)::getEnergyStored : iet instanceof BasicSink ? ((BasicSink) iet)::getEnergyStored : null;
		complete = source != null || sink != null;
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
		return cap == SELF_CAP || ((cap == EX.CAP_ENERGY || cap == CapabilityEnergy.ENERGY) && complete);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		if (cap == SELF_CAP)
			return (T) this;
		return (cap == EX.CAP_ENERGY || cap == CapabilityEnergy.ENERGY) && complete ? (T) sides[f == null ? 6 : f.getIndex()] : null;
	}
}
