package szewek.mcflux.compat.jei.fluxgen;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import szewek.mcflux.recipes.FluxGenRecipes;
import szewek.mcflux.recipes.RecipeFluxGen;
import szewek.mcflux.util.recipe.RecipeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum FluxGenRecipeMaker {
	;
	public static List<FluxGenRecipeJEI> getFluxGenRecipes(IJeiHelpers hlp) {
		IGuiHelper igh = hlp.getGuiHelper();
		Map<RecipeItem, RecipeFluxGen> catalysts = FluxGenRecipes.getCatalysts();
		Map<FluidStack, RecipeFluxGen> hotFluids = FluxGenRecipes.getHotFluids();
		Map<FluidStack, RecipeFluxGen> cleanFluids = FluxGenRecipes.getCleanFluids();
		List<FluxGenRecipeJEI> recipes = new ArrayList<>(catalysts.size() + hotFluids.size() + cleanFluids.size());
		for (Map.Entry<RecipeItem, RecipeFluxGen> e : catalysts.entrySet()) {
			RecipeItem ri = e.getKey();
			RecipeFluxGen rfg = e.getValue();
			ItemStack is = ri.makeItemStack();
			if (rfg.usage > 0)
				is.setCount(rfg.usage);
			recipes.add(new FluxGenRecipeJEI(igh, is, null, rfg.factor, 1));
		}
		addFluids(igh, recipes, hotFluids, 0);
		addFluids(igh, recipes, cleanFluids, 1);
		return recipes;
	}

	private static void addFluids(IGuiHelper igh, List<FluxGenRecipeJEI> l, Map<FluidStack, RecipeFluxGen> m, int s) {
		for (Map.Entry<FluidStack, RecipeFluxGen> e : m.entrySet()) {
			FluidStack fs = e.getKey().copy();
			RecipeFluxGen rfg = e.getValue();
			fs.amount = rfg.usage > 0 ? rfg.usage : 1;
			l.add(new FluxGenRecipeJEI(igh, ItemStack.EMPTY, fs, rfg.factor, s));
		}
	}
}
