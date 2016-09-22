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

	protected abstract boolean canConnect(EnumFacing f);

	@Override public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing f) {
		return canConnect(f) && cap == EX.CAP_ENERGY && !broken;
	}

	@SuppressWarnings("unchecked")
	@Override public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing f) {
		return canConnect(f) && cap == EX.CAP_ENERGY && !broken ? (T) sides[f == null ? 6 : f.getIndex()]: null;
	}
}
