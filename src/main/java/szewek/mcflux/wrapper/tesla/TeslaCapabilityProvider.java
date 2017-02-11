package szewek.mcflux.wrapper.tesla;

import net.darkhax.tesla.lib.TeslaUtils;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.wrapper.EnergyCapabilityProvider;

public final class TeslaCapabilityProvider extends EnergyCapabilityProvider {
	private ICapabilityProvider capProvider;

	TeslaCapabilityProvider(ICapabilityProvider icp) {
		capProvider = icp;
		broken = false;
		for (int i = 0; i < 6; i++) {
			sides[i] = new TeslaSided(capProvider, EnumFacing.VALUES[i]);
		}
		sides[6] = new TeslaSided(capProvider, null);
		forgeCompatible = true;
	}

	@Override protected boolean canConnect(EnumFacing f) {
		return TeslaUtils.hasTeslaSupport(capProvider, f);
	}
}
