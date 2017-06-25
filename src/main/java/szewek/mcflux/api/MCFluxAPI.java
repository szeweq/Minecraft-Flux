package szewek.mcflux.api;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.fl.FL;
import szewek.fl.energy.IEnergy;

public enum MCFluxAPI {
	;

	public static IEnergy getEnergySafely(ICapabilityProvider icp, EnumFacing f) {
		try {
			return icp.getCapability(FL.ENERGY_CAP, f);
		} catch (Exception ignored) {
		}
		return null;
	}
}
