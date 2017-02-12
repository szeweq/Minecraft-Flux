package szewek.mcflux.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public final class MCFluxGuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft mc) {

	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return MCFluxGuiConfig.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}

}
