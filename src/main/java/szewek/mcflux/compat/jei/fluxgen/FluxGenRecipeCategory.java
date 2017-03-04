package szewek.mcflux.compat.jei.fluxgen;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.util.text.TextComponentTranslation;
import szewek.mcflux.compat.jei.MCFluxJEIPlugin;
import szewek.mcflux.tileentities.TileEntityFluxGen;
import szewek.mcflux.util.MCFluxLocation;

public class FluxGenRecipeCategory extends BlankRecipeCategory<FluxGenRecipeJEI> {
	private final static MCFluxLocation bgLoc = new MCFluxLocation("textures/gui/fluxgen.png");
	private final IDrawable bg;
	private final String locName;
	public FluxGenRecipeCategory(IGuiHelper igh) {
		bg = igh.createDrawable(bgLoc, 46, 14, 130 - 46, 72 - 14);
		locName = new TextComponentTranslation("mcflux.container.fluxgen").getUnformattedText();
	}
	@Override public String getUid() {
		return MCFluxJEIPlugin.ID_FLUXGEN;
	}

	@Override public String getTitle() {
		return locName;
	}

	@Override public IDrawable getBackground() {
		return bg;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FluxGenRecipeJEI fgr, IIngredients ingredients) {
		if (fgr.inputFluid == null) {
			IGuiItemStackGroup ig = recipeLayout.getItemStacks();
			ig.init(1, true, 46, 20);
			ig.set(1, fgr.inputItem);
		} else {
			IGuiFluidStackGroup fg = recipeLayout.getFluidStacks();
			fg.init(0, true, 1, 1, 16, 56, TileEntityFluxGen.fluidCap, true, null);
			fg.init(1, true, 67, 1, 16, 56, TileEntityFluxGen.fluidCap, true, null);
			fg.set(fgr.slot, fgr.inputFluid);
		}
	}
}
