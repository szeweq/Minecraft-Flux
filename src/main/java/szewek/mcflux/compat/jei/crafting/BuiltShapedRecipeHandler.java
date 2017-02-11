package szewek.mcflux.compat.jei.crafting;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import szewek.mcflux.U;
import szewek.mcflux.util.recipe.BuiltShapedRecipe;

import javax.annotation.Nonnull;

public final class BuiltShapedRecipeHandler implements IRecipeHandler<BuiltShapedRecipe> {
	private final IJeiHelpers jeiHelpers;

	public BuiltShapedRecipeHandler(IJeiHelpers helpers) {jeiHelpers = helpers;}

	@Nonnull @Override public Class<BuiltShapedRecipe> getRecipeClass() {
		return BuiltShapedRecipe.class;
	}

	@Nonnull @Override public IRecipeWrapper getRecipeWrapper(@Nonnull BuiltShapedRecipe recipe) {
		return new BuiltShapedRecipeWrapper(jeiHelpers, recipe);
	}

	@Override public boolean isRecipeValid(@Nonnull BuiltShapedRecipe recipe) {
		return !U.isItemEmpty(recipe.getRecipeOutput());
	}

	@Nonnull @Override
	public String getRecipeCategoryUid(@Nonnull BuiltShapedRecipe recipe) {
		return VanillaRecipeCategoryUid.CRAFTING;
	}
}
