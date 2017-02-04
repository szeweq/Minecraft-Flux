package szewek.mcflux.wrapper;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import szewek.mcflux.api.ex.EX;
import szewek.mcflux.api.ex.IEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class EnergyCapabilityProvider implements ICapabilityProvider {
	protected boolean broken, forgeCompatible = false;
	protected final IEnergy[] sides = new IEnergy[7];

	protected abstract boolean canConnect(EnumFacing f);

	@Override public boolean hasCapability(@Nonnull Capability<?> cap, @Nullable EnumFacing f) {
		return canConnect(f) && (cap == EX.CAP_ENERGY || (forgeCompatible && cap == CapabilityEnergy.ENERGY)) && !broken;
	}

	@SuppressWarnings("unchecked")
	@Override public <T> T getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing f) {
		return hasCapability(cap, f) ? (T) sides[f == null ? 6 : f.getIndex()]: null;
	}
}
