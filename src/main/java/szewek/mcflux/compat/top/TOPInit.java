package szewek.mcflux.compat.top;

import mcjty.theoneprobe.api.ITheOneProbe;
import szewek.mcflux.L;

public final class TOPInit implements com.google.common.base.Function<ITheOneProbe, Void> {

	@Override public Void apply(ITheOneProbe probe) {
		L.info("Minecraft-flux prepares integration with The One Probe...");
		MCFluxTOPProvider mftop = new MCFluxTOPProvider();
		probe.registerProvider(mftop);
		probe.registerEntityProvider(mftop);
		return null;
	}
}
