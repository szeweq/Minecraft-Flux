package szewek.mcflux.compat.jei.crafting;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import szewek.mcflux.util.recipe.BuiltShapedRecipe;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class BuiltShapedRecipeWrapper extends mezz.jei.api.recipe.BlankRecipeWrapper implements mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper {
	private final IJeiHelpers jeiHelpers;
	private final BuiltShapedRecipe recipe;
	private final int width, height;

	public BuiltShapedRecipeWrapper(IJeiHelpers helpers, BuiltShapedRecipe bsr) {
		jeiHelpers = helpers;
		recipe = bsr;
		width = bsr.getWidth();
		height = bsr.getHeight();
	}

	@Override public int getWidth() {
		return width;
	}

	@Override public int getHeight() {
		return height;
	}

	@Override public void getIngredients(IIngredients ingredients) {
		IStackHelper stackHelper = jeiHelpers.getStackHelper();
		List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(Arrays.asList(recipe.getCached()));
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutput(ItemStack.class, recipe.getRecipeOutput());
	}

	@Override public boolean handleClick(@Nonnull Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}
}
