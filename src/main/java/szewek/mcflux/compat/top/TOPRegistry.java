package szewek.mcflux.compat.top;

import net.minecraftforge.fml.common.event.FMLInterModComms;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectCond;
import szewek.mcflux.util.InjectRegistry;

@InjectRegistry(requires = InjectCond.MOD, args = "theoneprobe")
public final class TOPRegistry implements IInjectRegistry {
	@Override public void registerInjects() {
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", TOPInit.class.getName());
	}

}
