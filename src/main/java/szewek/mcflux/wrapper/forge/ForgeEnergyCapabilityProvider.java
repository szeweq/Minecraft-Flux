package szewek.mcflux.wrapper.forge;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.wrapper.EnergyCapabilityProvider;

public final class ForgeEnergyCapabilityProvider extends EnergyCapabilityProvider {

	ForgeEnergyCapabilityProvider(ICapabilityProvider icp) {
		broken = false;
		for (int i = 0; i < 6; i++) {
			sides[i] = new ForgeEnergySided(icp, EnumFacing.VALUES[i]);
		}
		sides[6] = new ForgeEnergySided(icp, null);
	}

	@Override protected boolean canConnect(EnumFacing f) {
		if (f == null) return false;
		IEnergy ie = sides[f.getIndex()];
		return ie.canOutputEnergy() || ie.canInputEnergy();
	}
}
