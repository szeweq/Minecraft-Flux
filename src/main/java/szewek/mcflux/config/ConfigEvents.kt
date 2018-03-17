package szewek.mcflux.config

import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import szewek.mcflux.R

class ConfigEvents {
	@SubscribeEvent
	fun cfgChanged(e: ConfigChangedEvent.OnConfigChangedEvent) {
		if (R.MF_NAME == e.modID && !e.isWorldRunning) {
			val cfgid = e.configID
			if (cfgid != null && cfgid == Configuration.CATEGORY_GENERAL)
				MCFluxConfig.syncConfig(false)
		}
	}
}
