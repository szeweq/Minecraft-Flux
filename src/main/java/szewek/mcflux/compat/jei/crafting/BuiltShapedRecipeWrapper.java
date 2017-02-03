package szewek.mcflux.compat.jei.crafting;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import szewek.mcflux.util.recipe.BuiltShapedRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BuiltShapedRecipeWrapper extends mezz.jei.api.recipe.BlankRecipeWrapper implements mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper {
	private BuiltShapedRecipe recipe;
	public BuiltShapedRecipeWrapper(BuiltShapedRecipe bsr) {
		recipe = bsr;
	}
	@Override public int getWidth() {
		return recipe.getWidth();
	}
	@Override public int getHeight() {
		return recipe.getHeight();
	}
	@Nonnull @Override public List<Object> getInputs() {
		return Arrays.asList(recipe.getCached());
	}
	@Nonnull @Override public List<ItemStack> getOutputs() {
		return Collections.singletonList(recipe.getRecipeOutput());
	}
	@Override
	public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

	}
	@Override public void drawAnimations(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight) {

	}
	@Nullable @Override public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return null;
	}
	@Override public boolean handleClick(@Nonnull Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}
}
