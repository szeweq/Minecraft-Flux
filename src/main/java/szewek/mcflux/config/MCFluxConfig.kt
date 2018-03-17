package szewek.mcflux.config

import net.minecraftforge.common.config.Configuration
import java.io.File

object MCFluxConfig {
	@JvmField
	var CFG_EU_VALUE = 4
	@JvmField
	var WORLDCHUNK_CAP = 20000000
	@JvmField
	var ENERGY_DIST_TRANS = 1000000
	@JvmField
	var CHUNK_CHARGER_TRANS = 2000000
	@JvmField
	var WET_TRANS = 4096
	@JvmField
	var FURNACE_CAP = 50000
	@JvmField
	var MOB_SPAWNER_USE = 500
	@JvmField
	var ONLINE_ERROR_REPORT = true
	@JvmField
	var UPDATE_CHECK = true
	@JvmField
	var WRAP_ITEM_STACKS = false
	@JvmField
	var SHOW_FLUXCOMPAT = true
	lateinit var config: Configuration
		private set

	fun makeConfig(file: File) {
		config = Configuration(file)
		syncConfig(true)
	}

	internal fun syncConfig(fromFile: Boolean) {
		if (fromFile)
			config.load()
		CFG_EU_VALUE = cfgInt("EUValue", 4, 1, 400000, "Amount of MF when converted from 1 EU")
		WORLDCHUNK_CAP = cfgInt("worldChunkCapacity", 20000000, 1, Integer.MAX_VALUE, "World Chunk Energy capacity")
		ENERGY_DIST_TRANS = cfgInt("energyDistTransfer", 1000000, 1, Integer.MAX_VALUE, "Energy Distributor transfer (MF/t)")
		CHUNK_CHARGER_TRANS = cfgInt("chunkChargerTransfer", 2000000, 1, Integer.MAX_VALUE, "Chunk Charger transfer (MF/t)")
		WET_TRANS = cfgInt("WETTransfer", 4096, 1, Integer.MAX_VALUE, "Wireless Energy Transferrer transfer (MF/t)")
		FURNACE_CAP = cfgInt("furnaceCapacity", 50000, 1000, Integer.MAX_VALUE, "Energy capacity for Vanilla Furnace")
		MOB_SPAWNER_USE = cfgInt("mobSpawnerEnergyUse", 500, 100, Integer.MAX_VALUE, "Energy needed for Vanilla Monster Spawner to speed up")
		ONLINE_ERROR_REPORT = cfgBool("onlineErrorReport", true, "Reports all capability-related crashes made by Minedraft-Flux online")
		UPDATE_CHECK = cfgBool("updateCheck", true, "Checks if a newer Minecraft-Flux version is available")
		WRAP_ITEM_STACKS = cfgBool("wrapItemStacks", false, "Wraps Item Stacks so they can provide MF or Flavor Energy")
		SHOW_FLUXCOMPAT = cfgBool("showFluxCompat", false, "Shows FluxCompat Energy bar in The One Probe")
		if (config.hasChanged())
			config.save()
	}

	private fun cfgInt(name: String, def: Int, min: Int, max: Int, comment: String): Int {
		val p = config.get(Configuration.CATEGORY_GENERAL, name, def, comment + " [default: " + def + "; " + min + " ~ " + max + ']'.toString(), min, max)
		p.languageKey = "mcflux.config.$name"
		val pv = p.getInt(def)
		return if (pv < min) min else if (pv > max) max else pv
	}

	private fun cfgBool(name: String, def: Boolean, comment: String): Boolean {
		val p = config.get(Configuration.CATEGORY_GENERAL, name, def, "$comment [default: $def]")
		p.languageKey = "mcflux.config.$name"
		return p.getBoolean(def)
	}
}
