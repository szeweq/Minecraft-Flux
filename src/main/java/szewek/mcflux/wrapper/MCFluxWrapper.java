package szewek.mcflux.wrapper;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.util.MCFluxLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public final class MCFluxWrapper implements ICapabilityProvider {
	static final MCFluxLocation MCFLUX_WRAPPER = new MCFluxLocation("wrapper");
	Object mainObject = null;
	private ICapabilityProvider[] providers = new ICapabilityProvider[0];

	MCFluxWrapper(Object o) {
		mainObject = o;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, @Nullable EnumFacing f) {
		for (ICapabilityProvider icp : providers) {
			if (icp.hasCapability(cap, f))
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing f) {
		for (ICapabilityProvider icp : providers) {
			T t = icp.getCapability(cap, f);
			if (t != null)
				return t;
		}
		return null;
	}

	void addWrappers(Map<String, ICapabilityProvider> icps) {
		final int size = icps.size();
		if (size > 0)
			providers = icps.values().toArray(new ICapabilityProvider[size]);
	}
}
