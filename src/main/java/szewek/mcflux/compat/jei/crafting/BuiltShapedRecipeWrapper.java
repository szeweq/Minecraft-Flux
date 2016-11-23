package szewek.mcflux.compat.jei.crafting;

import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import szewek.mcflux.util.RecipeBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BuiltShapedRecipeWrapper extends mezz.jei.api.recipe.BlankRecipeWrapper implements mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper {
	private RecipeBuilder.BuiltShapedRecipe recipe;
	public BuiltShapedRecipeWrapper(RecipeBuilder.BuiltShapedRecipe bsr) {
		recipe = bsr;
	}
	@Override public int getWidth() {
		return recipe.getWidth();
	}
	@Override public int getHeight() {
		return recipe.getHeight();
	}

	@Override public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, recipe.getCached());
		ingredients.setOutput(ItemStack.class, recipe.getRecipeOutput());
	}
	@Override
	public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

	}
	@Nullable @Override public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return null;
	}
	@Override public boolean handleClick(@Nonnull Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}
}
