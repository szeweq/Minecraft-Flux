package szewek.mcflux.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import szewek.mcflux.api.wrench.IWrenched;

public final class MCFluxCapabilities {
	@CapabilityInject(IWrenched.class) public static Capability<IWrenched> WRENCHED = null;
}
