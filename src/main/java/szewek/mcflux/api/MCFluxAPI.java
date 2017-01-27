package szewek.mcflux.api;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.ex.EX;
import szewek.mcflux.api.ex.IEnergy;

public enum MCFluxAPI {
	;

	public static IEnergy getEnergySafely(ICapabilityProvider icp, EnumFacing f) {
		try {
			return icp.getCapability(EX.CAP_ENERGY, f);
		} catch (Exception ignored) {
		}
		return null;
	}
}
