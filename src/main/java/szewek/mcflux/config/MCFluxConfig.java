package szewek.mcflux.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class MCFluxConfig {
	public static int CFG_EU_VALUE = 4;
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
		Property euValue = config.get(Configuration.CATEGORY_GENERAL, "EUValue", 4, "Amount of MF when converted from 1 EU");
		if (fromCfg) {
			CFG_EU_VALUE = euValue.getInt(4);
		}
		if(config.hasChanged())
			config.save();
	}
}
