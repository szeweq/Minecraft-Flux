package szewek.mcflux.util;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.fl.FL;

import javax.annotation.Nullable;

public abstract class EnergyCapable implements szewek.fl.energy.IEnergy, ICapabilityProvider {
	@Override public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing f) {
		return cap == FL.ENERGY_CAP;
	}

	@SuppressWarnings("unchecked")
	@Override public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing f) {
		return cap == FL.ENERGY_CAP ? (T) this : null;
	}
}
