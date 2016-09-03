package szewek.mcflux.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import szewek.mcflux.R;
import szewek.mcflux.config.MCFluxConfig;

public class MCFluxGuiConfig extends GuiConfig {
	public MCFluxGuiConfig(GuiScreen screen) {
		super(screen, new ConfigElement(MCFluxConfig.getConfig().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), R.MF_NAME, false, false, I18n.format("mcflux.guiConfig.title"));
	}
}
