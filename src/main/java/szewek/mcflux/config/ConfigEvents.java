package szewek.mcflux.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import szewek.mcflux.R;

@SuppressWarnings("unused")
public final class ConfigEvents {
	@SubscribeEvent
	public void cfgChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
		if (R.MF_NAME.equals(e.getModID()) && !e.isWorldRunning()) {
			final String cfgid = e.getConfigID();
			if (cfgid != null && cfgid.equals(Configuration.CATEGORY_GENERAL))
				MCFluxConfig.syncConfig(false);
		}
	}
}
