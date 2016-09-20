package szewek.mcflux.wrapper.tesla;

import net.darkhax.tesla.lib.TeslaUtils;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.wrapper.EnergyCapabilityProvider;

public class TeslaCapabilityProvider extends EnergyCapabilityProvider {
	ICapabilityProvider capabilityProvider;
	TeslaCapabilityProvider(ICapabilityProvider icp) {
		capabilityProvider = icp;
		broken = false;
		for (int i = 0; i < 6; i++) {
			sides[i] = new TeslaSided(capabilityProvider, EnumFacing.VALUES[i]);
		}
	}
	@Override protected boolean canConnect(EnumFacing f) {
		return TeslaUtils.hasTeslaSupport(capabilityProvider, f);
	}
}
