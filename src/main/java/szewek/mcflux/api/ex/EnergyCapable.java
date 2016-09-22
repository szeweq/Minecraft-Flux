package szewek.mcflux.api.ex;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public abstract class EnergyCapable implements IEnergy, ICapabilityProvider {
	@Override public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing f) {
		return cap == EX.CAP_ENERGY;
	}

	@SuppressWarnings("unchecked")
	@Override public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing f) {
		return cap == EX.CAP_ENERGY ? (T) this : null;
	}
}
