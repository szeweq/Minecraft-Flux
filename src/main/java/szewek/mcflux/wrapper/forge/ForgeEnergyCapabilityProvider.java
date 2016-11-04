package szewek.mcflux.wrapper.forge;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.wrapper.EnergyCapabilityProvider;

public class ForgeEnergyCapabilityProvider extends EnergyCapabilityProvider {
	ICapabilityProvider capProvider;

	ForgeEnergyCapabilityProvider(ICapabilityProvider icp) {
		capProvider = icp;
		broken = false;
		for (int i = 0; i < 6; i++) {
			sides[i] = new ForgeEnergySided(capProvider, EnumFacing.VALUES[i]);
		}
		sides[6] = new ForgeEnergySided(capProvider, null);
	}

	@Override protected boolean canConnect(EnumFacing f) {
		return false;
	}
}
