package szewek.mcflux.wrapper;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.ex.EX;
import szewek.mcflux.api.ex.IEnergy;

import javax.annotation.Nullable;

public abstract class EnergyCapabilityProvider implements ICapabilityProvider {
	protected boolean broken;
	protected final IEnergy[] sides = new IEnergy[7];
	protected final CompatEnergyWrapper[] compatSides = new CompatEnergyWrapper[7];

	protected abstract boolean canConnect(EnumFacing f);

	@Override public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing f) {
		if (canConnect(f)) {
			if (cap == EX.CAP_ENERGY)
				return !broken;
			CompatEnergyWrapper cew = compatSides[f == null ? 6 : f.getIndex()];
			return cew.isCompatInputSuitable(cap) || cew.isCompatOutputSuitable(cap);
		}
		return false;
	}

	@Override public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing f) {
		if (canConnect(f)) {
			int g = f == null ? 6 : f.getIndex();
			if (cap == EX.CAP_ENERGY)
				return broken ? null : (T) sides[g];
			CompatEnergyWrapper cew = compatSides[g];
			if (cew.isCompatInputSuitable(cap) || cew.isCompatOutputSuitable(cap))
				return (T) cew;
		}
		return null;
	}
}
