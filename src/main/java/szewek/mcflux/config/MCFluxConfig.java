package szewek.mcflux.config;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class MCFluxConfig {
	public static int CFG_EU_VALUE = 4, WORLDCHUNK_CAP = 20000000;
	private static Configuration config;
	
	public static void makeConfig(File file) {
		config = new Configuration(file);
		syncConfig(true, true);
	}
	
	public static Configuration getConfig() {
		return config;
	}
	
	static void syncConfig(boolean fromFile, boolean fromCfg) {
		if(fromFile)
			config.load();
		if (fromCfg) {
			CFG_EU_VALUE = cfgInt("EUValue", 4, 1, 400000, "Amount of MF when converted from 1 EU");
			WORLDCHUNK_CAP = cfgInt("worldChunkCapacity", 20000000, 1, Integer.MAX_VALUE, "World Chunk Energy capacity");
		}
		if(config.hasChanged())
			config.save();
	}
	
	private static int cfgInt(String name, int def, int min, int max, String comment) {
		Property p = config.get(Configuration.CATEGORY_GENERAL, name, def, comment, min, max);
		p.setLanguageKey("mcflux.config." + name);
		int pv = p.getInt(def);
		return pv < min ? min : pv > max ? max : pv;
	}
}
