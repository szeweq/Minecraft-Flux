package szewek.mcflux.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import szewek.mcflux.R;

public class ConfigEvents {
	public void cfgChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
		if (R.MF_NAME.equals(e.getModID()) && !e.isWorldRunning())
			if (e.getConfigID().equals(Configuration.CATEGORY_GENERAL))
				MCFluxConfig.syncConfig(false, true);
	}
}
