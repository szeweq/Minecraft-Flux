package szewek.mcflux.compat.jei.fluxgen

import mezz.jei.api.IGuiHelper
import mezz.jei.api.IJeiHelpers
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import szewek.mcflux.recipes.FluxGenRecipes
import szewek.mcflux.recipes.RecipeFluxGen
import java.util.*

object FluxGenRecipeMaker {
	fun getFluxGenRecipes(hlp: IJeiHelpers): List<FluxGenRecipeJEI> {
		val igh = hlp.guiHelper
		val catalysts = FluxGenRecipes.catalysts
		val hotFluids = FluxGenRecipes.hotFluids
		val cleanFluids = FluxGenRecipes.cleanFluids
		val recipes = ArrayList<FluxGenRecipeJEI>(catalysts.size + hotFluids.size + cleanFluids.size)
		for (e in catalysts.entries) {
			val ri = e.key
			val rfg = e.value
			val stk = ri.makeItemStack()
			if (rfg.usage > 0)
				stk.count = rfg.usage.toInt()
			recipes.add(FluxGenRecipeJEI(igh, stk, null, rfg.factor.toInt(), 1))
		}
		addFluids(igh, recipes, hotFluids, 0)
		addFluids(igh, recipes, cleanFluids, 1)
		return recipes
	}

	private fun addFluids(igh: IGuiHelper, l: MutableList<FluxGenRecipeJEI>, m: Map<FluidStack, RecipeFluxGen>, s: Int) {
		for ((key, rfg) in m) {
			val fs = key.copy()
			fs.amount = (if (rfg.usage > 0) rfg.usage else 1).toInt()
			l.add(FluxGenRecipeJEI(igh, ItemStack.EMPTY, fs, rfg.factor.toInt(), s))
		}
	}
}
