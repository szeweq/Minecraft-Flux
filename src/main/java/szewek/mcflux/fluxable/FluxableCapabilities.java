package szewek.mcflux.fluxable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class FluxableCapabilities {
	@CapabilityInject(WorldChunkEnergy.class)
	public static Capability<WorldChunkEnergy> CAP_WCE = null;

	@CapabilityInject(PlayerEnergy.class)
	public static Capability<PlayerEnergy> CAP_PE = null;
}
