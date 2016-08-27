package szewek.mcflux.fluxable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityFluxable {
	@CapabilityInject(WorldChunkEnergy.class)
	public static Capability<WorldChunkEnergy> FLUXABLE_WORLD_CHUNK = null;
}
