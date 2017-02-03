package szewek.mcflux.compat.jei.crafting;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import szewek.mcflux.util.recipe.BuiltShapedRecipe;

import javax.annotation.Nonnull;

public class BuiltShapedRecipeHandler implements IRecipeHandler<BuiltShapedRecipe>{
	@Nonnull @Override public Class<BuiltShapedRecipe> getRecipeClass() {
		return BuiltShapedRecipe.class;
	}
	@Nonnull @Override public String getRecipeCategoryUid() {
		return VanillaRecipeCategoryUid.CRAFTING;
	}
	@Nonnull @Override public IRecipeWrapper getRecipeWrapper(@Nonnull BuiltShapedRecipe recipe) {
		return new BuiltShapedRecipeWrapper(recipe);
	}
	@Override public boolean isRecipeValid(@Nonnull BuiltShapedRecipe recipe) {
		return recipe.getRecipeOutput() != null;
	}
	@Override
	public String getRecipeCategoryUid(BuiltShapedRecipe recipe) {
		return VanillaRecipeCategoryUid.CRAFTING;
	}
}
