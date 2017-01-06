package szewek.mcflux.api.assistant;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityAssist {
	@CapabilityInject(IAssist.class) public static Capability<IAssist> ASSIST = null;
}
