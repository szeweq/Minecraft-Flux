package szewek.mcflux.compat.top;

import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import szewek.mcflux.L;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectCond;
import szewek.mcflux.util.InjectRegistry;

@InjectRegistry(requires = InjectCond.MOD, args = "theoneprobe")
public final class TOPRegistry implements IInjectRegistry {
	//private static ITheOneProbe theOneProbe;
	@Override public void registerInjects() {
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", GetTOP.class.getName());
	}

	public static final class GetTOP implements com.google.common.base.Function<ITheOneProbe, Void> {

		@Override public Void apply(ITheOneProbe probe) {
			//theOneProbe = probe;
			L.info("Minecraft-flux prepares integration with The One Probe...");
			MCFluxTOPProvider mftop = new MCFluxTOPProvider();
			probe.registerProvider(mftop);
			probe.registerEntityProvider(mftop);
			return null;
		}
	}
}
