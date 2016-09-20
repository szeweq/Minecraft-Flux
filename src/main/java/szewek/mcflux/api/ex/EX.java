package szewek.mcflux.api.ex;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public enum EX {
	;
	@CapabilityInject(IEnergy.class) public static Capability<IEnergy> CAP_ENERGY = null;
}
