package szewek.mcflux.compat.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import szewek.mcflux.MCFlux
import szewek.mcflux.compat.jei.fluxgen.FluxGenRecipeCategory
import szewek.mcflux.compat.jei.fluxgen.FluxGenRecipeJEI
import szewek.mcflux.compat.jei.fluxgen.FluxGenRecipeMaker
import szewek.mcflux.gui.GuiFluxGen

@JEIPlugin
class MCFluxJEIPlugin : IModPlugin {

	override fun register(reg: IModRegistry) {
		val hlp = reg.jeiHelpers
		reg.handleRecipes(FluxGenRecipeJEI::class.java, { t -> t }, ID_FLUXGEN)
		reg.addRecipeClickArea(GuiFluxGen::class.java, 84, 34, 4, 18, ID_FLUXGEN)
		reg.addRecipes(FluxGenRecipeMaker.getFluxGenRecipes(hlp), ID_FLUXGEN)
		reg.addRecipeCatalyst(ItemStack(MCFlux.Resources.FLUXGEN), ID_FLUXGEN)
		addItemDescriptions(reg, MCFlux.Resources.MFTOOL, MCFlux.Resources.UPCHIP, Item.getItemFromBlock(MCFlux.Resources.ECHARGER), Item.getItemFromBlock(MCFlux.Resources.WET))
		for (i in 0..3) {
			val it = Item.getItemFromBlock(MCFlux.Resources.ENERGY_MACHINE)
			val stk = ItemStack(it, 1, i)
			reg.addIngredientInfo(stk, ItemStack::class.java, it.getUnlocalizedName(stk) + ".jeidesc")
		}
	}

	override fun registerCategories(reg: IRecipeCategoryRegistration) {
		reg.addRecipeCategories(FluxGenRecipeCategory(reg.jeiHelpers.guiHelper))
	}

	private fun addItemDescriptions(reg: IModRegistry, vararg items: Item) {
		for (it in items)
			reg.addIngredientInfo(ItemStack(it), ItemStack::class.java, it.unlocalizedName + ".jeidesc")
	}

	companion object {
		val ID_FLUXGEN = "mcflux.fluxgen"
	}

}

