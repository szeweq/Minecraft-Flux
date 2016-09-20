package szewek.mcflux.api.fe;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public enum FE {
	;
	@CapabilityInject(IFlavorEnergy.class) public static Capability<IFlavorEnergy> CAP_FLAVOR_ENERGY = null;
}
