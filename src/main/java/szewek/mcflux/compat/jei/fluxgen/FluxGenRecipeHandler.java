package szewek.mcflux.compat.jei.fluxgen;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import szewek.mcflux.compat.jei.MCFluxJEIPlugin;

public class FluxGenRecipeHandler implements IRecipeHandler<FluxGenRecipeJEI> {
	@Override public Class<FluxGenRecipeJEI> getRecipeClass() {
		return FluxGenRecipeJEI.class;
	}

	@Override public String getRecipeCategoryUid(FluxGenRecipeJEI recipe) {
		return MCFluxJEIPlugin.ID_FLUXGEN;
	}

	@Override public IRecipeWrapper getRecipeWrapper(FluxGenRecipeJEI recipe) {
		return recipe;
	}

	@Override public boolean isRecipeValid(FluxGenRecipeJEI recipe) {
		return true;
	}
}
