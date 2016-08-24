package szewek.mcflux.fluxable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityFluxable {
	private static boolean ONCE = true;

	@CapabilityInject(WorldChunkEnergy.class)
	public static Capability<WorldChunkEnergy> FLUXABLE_WORLD_CHUNK = null;

	public static void register() {
		if (!ONCE)
			return;
		ONCE = false;
		CapabilityManager.INSTANCE.register(WorldChunkEnergy.class, new WorldChunkEnergy.ChunkStorage(), WorldChunkEnergy::new);
	}
}
