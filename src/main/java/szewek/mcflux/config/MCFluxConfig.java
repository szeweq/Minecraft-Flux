package szewek.mcflux.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public final class MCFluxConfig {
	public static int CFG_EU_VALUE = 4, WORLDCHUNK_CAP = 20000000, ENERGY_DIST_TRANS = 1000000, CHUNK_CHARGER_TRANS = 2000000, WET_TRANS = 4096, FURNACE_CAP = 50000, MOB_SPAWNER_USE = 500;
	public static boolean ONLINE_ERROR_REPORT = true, UPDATE_CHECK = true, WRAP_ITEM_STACKS = false;
	private static Configuration config;

	public static void makeConfig(File file) {
		config = new Configuration(file);
		syncConfig(true);
	}

	public static Configuration getConfig() {
		return config;
	}

	static void syncConfig(boolean fromFile) {
		if (fromFile)
			config.load();
		CFG_EU_VALUE = cfgInt("EUValue", 4, 1, 400000, "Amount of MF when converted from 1 EU");
		WORLDCHUNK_CAP = cfgInt("worldChunkCapacity", 20000000, 1, Integer.MAX_VALUE, "World Chunk Energy capacity");
		ENERGY_DIST_TRANS = cfgInt("energyDistTransfer", 1000000, 1, Integer.MAX_VALUE, "Energy Distributor transfer (MF/t)");
		CHUNK_CHARGER_TRANS = cfgInt("chunkChargerTransfer", 2000000, 1, Integer.MAX_VALUE, "Chunk Charger transfer (MF/t)");
		WET_TRANS = cfgInt("WETTransfer", 4096, 1, Integer.MAX_VALUE, "Wireless Energy Transferrer transfer (MF/t)");
		FURNACE_CAP = cfgInt("furnaceCapacity", 50000, 1000, Integer.MAX_VALUE, "Energy capacity for Vanilla Furnace");
		MOB_SPAWNER_USE = cfgInt("mobSpawnerEnergyUse", 500, 100, Integer.MAX_VALUE, "Energy needed for Vanilla Monster Spawner to speed up");
		ONLINE_ERROR_REPORT = cfgBool("onlineErrorReport", true, "Reports all capability-related crashes made by Minedraft-Flux online");
		UPDATE_CHECK = cfgBool("updateCheck", true, "Checks if a newer Minecraft-Flux version is available");
		WRAP_ITEM_STACKS = cfgBool("wrapItemStacks", false, "Wraps Item Stacks so they can provide MF or Flavor Energy");
		if (config.hasChanged())
			config.save();
	}

	private static int cfgInt(String name, int def, int min, int max, String comment) {
		final Property p = config.get(Configuration.CATEGORY_GENERAL, name, def, comment + " [default: " + def + "; " + min + " ~ " + max + ']', min, max);
		p.setLanguageKey("mcflux.config." + name);
		final int pv = p.getInt(def);
		return pv < min ? min : pv > max ? max : pv;
	}

	private static boolean cfgBool(String name, boolean def, String comment) {
		final Property p = config.get(Configuration.CATEGORY_GENERAL, name, def, comment + " [default: " + def + "]");
		p.setLanguageKey("mcflux.config." + name);
		return p.getBoolean(def);
	}

	private MCFluxConfig() {}
}
