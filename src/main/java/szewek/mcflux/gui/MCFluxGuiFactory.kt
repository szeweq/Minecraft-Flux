package szewek.mcflux.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.IModGuiFactory

class MCFluxGuiFactory : IModGuiFactory {

	override fun initialize(mc: Minecraft) {}

	override fun createConfigGui(parentScreen: GuiScreen): GuiScreen {
		return MCFluxGuiConfig(parentScreen)
	}

	override fun hasConfigGui(): Boolean {
		return true
	}

	override fun runtimeGuiCategories(): Set<IModGuiFactory.RuntimeOptionCategoryElement>? {
		return null
	}
}
