package szewek.mcflux.compat.jei.crafting;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import szewek.mcflux.util.RecipeBuilder;
import szewek.mcflux.util.RecipeBuilder.BuiltShapedRecipe;

import javax.annotation.Nonnull;

public class BuiltShapedRecipeHandler implements IRecipeHandler<RecipeBuilder.BuiltShapedRecipe>{
	@Nonnull @Override public Class<RecipeBuilder.BuiltShapedRecipe> getRecipeClass() {
		return RecipeBuilder.BuiltShapedRecipe.class;
	}
	@Nonnull @Override public IRecipeWrapper getRecipeWrapper(@Nonnull RecipeBuilder.BuiltShapedRecipe recipe) {
		return new BuiltShapedRecipeWrapper(recipe);
	}
	@Override public boolean isRecipeValid(@Nonnull RecipeBuilder.BuiltShapedRecipe recipe) {
		return recipe.getRecipeOutput() != null;
	}
	@Override
	public String getRecipeCategoryUid(BuiltShapedRecipe recipe) {
		return VanillaRecipeCategoryUid.CRAFTING;
	}
}
