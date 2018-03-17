package szewek.mcflux.compat.jei.fluxgen

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategory
import net.minecraft.util.text.TextComponentTranslation
import szewek.mcflux.R
import szewek.mcflux.compat.jei.MCFluxJEIPlugin
import szewek.mcflux.tileentities.TileEntityFluxGen
import szewek.mcflux.util.MCFluxLocation

class FluxGenRecipeCategory(igh: IGuiHelper) : IRecipeCategory<FluxGenRecipeJEI> {
	private val bg: IDrawable
	private val locName: String

	init {
		bg = igh.createDrawable(bgLoc, 46, 14, 130 - 46, 72 - 14)
		locName = TextComponentTranslation("tile.fluxgen.name").unformattedComponentText
	}

	override fun getUid() = MCFluxJEIPlugin.ID_FLUXGEN
	override fun getTitle() = locName
	override fun getModName() = R.MF_FULL_NAME
	override fun getBackground() = bg

	override fun setRecipe(recipeLayout: IRecipeLayout, fgr: FluxGenRecipeJEI, ingredients: IIngredients) {
		if (fgr.inputFluid == null) {
			val ig = recipeLayout.itemStacks
			ig.init(1, true, 46, 20)
			ig.set(1, fgr.inputItem)
		} else {
			val fg = recipeLayout.fluidStacks
			fg.init(0, true, 1, 1, 16, 56, TileEntityFluxGen.fluidCap, true, null)
			fg.init(1, true, 67, 1, 16, 56, TileEntityFluxGen.fluidCap, true, null)
			fg.set(fgr.slot, fgr.inputFluid)
		}
	}

	companion object {
		private val bgLoc = MCFluxLocation("textures/gui/fluxgen.png")
	}
}
