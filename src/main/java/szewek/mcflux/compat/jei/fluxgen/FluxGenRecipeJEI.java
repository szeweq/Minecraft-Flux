package szewek.mcflux.compat.jei.fluxgen;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class FluxGenRecipeJEI extends BlankRecipeWrapper {
	final ItemStack inputItem;
	final FluidStack inputFluid;
	private final String type, factor;
	final int slot;
	private int typeX = -1, factorX = -1;

	FluxGenRecipeJEI(IGuiHelper igh, ItemStack is, @Nullable FluidStack fs, int f, int s) {
		inputItem = is;
		inputFluid = fs;
		String t = fs == null ? "speed" : s == 0 ? "time" : "length";
		type = I18n.format("mcflux.fluxgen." + t);
		factor = (fs != null && s == 1 ? "-" : "×") + f;
		slot = s;
	}

	@Override public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(ItemStack.class, inputItem);
		ingredients.setInput(FluidStack.class, inputFluid);
	}

	@Override public void drawInfo(Minecraft mc, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		if (factorX == -1 && typeX == -1) {
			factorX = (recipeWidth - mc.fontRenderer.getStringWidth(factor)) / 2;
			typeX = (recipeWidth - mc.fontRenderer.getStringWidth(type)) / 2;
		}
		mc.fontRenderer.drawString(type, typeX, 1, 0x404040, false);
		mc.fontRenderer.drawString(factor, factorX, 51, 0x404040, false);
	}
}
