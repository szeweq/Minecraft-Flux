package szewek.mcflux.compat.jei.fluxgen

import mezz.jei.api.IGuiHelper
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class FluxGenRecipeJEI internal constructor(igh: IGuiHelper, internal val inputItem: ItemStack, internal val inputFluid: FluidStack?, f: Int, internal val slot: Int) : IRecipeWrapper {
	private val type: String
	private val factor: String
	private var typeX = -1
	private var factorX = -1

	init {
		val t = if (inputFluid == null) "speed" else if (slot == 0) "time" else "length"
		type = I18n.format("mcflux.fluxgen.$t")
		factor = (if (inputFluid != null && slot == 1) "-" else "×") + f
	}

	override fun getIngredients(ingredients: IIngredients) {
		ingredients.setInput(ItemStack::class.java, inputItem)
		ingredients.setInput(FluidStack::class.java, inputFluid)
	}

	override fun drawInfo(mc: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
		if (factorX == -1 && typeX == -1) {
			factorX = (recipeWidth - mc.fontRenderer.getStringWidth(factor)) / 2
			typeX = (recipeWidth - mc.fontRenderer.getStringWidth(type)) / 2
		}
		mc.fontRenderer.drawString(type, typeX.toFloat(), 1f, 0x404040, false)
		mc.fontRenderer.drawString(factor, factorX.toFloat(), 51f, 0x404040, false)
	}
}
