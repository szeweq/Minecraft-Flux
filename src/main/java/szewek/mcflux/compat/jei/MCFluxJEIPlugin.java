package szewek.mcflux.compat.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import szewek.mcflux.MCFluxResources;
import szewek.mcflux.compat.jei.fluxgen.FluxGenRecipeCategory;
import szewek.mcflux.compat.jei.fluxgen.FluxGenRecipeJEI;
import szewek.mcflux.compat.jei.fluxgen.FluxGenRecipeMaker;
import szewek.mcflux.gui.GuiFluxGen;

import javax.annotation.Nonnull;

import static szewek.mcflux.MCFluxResources.*;

@JEIPlugin
public final class MCFluxJEIPlugin extends BlankModPlugin {
	public static final String ID_FLUXGEN = "mcflux.fluxgen";

	@Override
	public void register(@Nonnull IModRegistry reg) {
		IJeiHelpers hlp = reg.getJeiHelpers();
		reg.handleRecipes(FluxGenRecipeJEI.class, (t) -> t, ID_FLUXGEN);
		reg.addRecipeClickArea(GuiFluxGen.class, 84, 34, 4, 18, ID_FLUXGEN);
		reg.addRecipes(FluxGenRecipeMaker.getFluxGenRecipes(hlp), ID_FLUXGEN);
		reg.addRecipeCatalyst(new ItemStack(MCFluxResources.FLUXGEN), ID_FLUXGEN);
		addItemDescriptions(reg, MFTOOL, UPCHIP, Item.getItemFromBlock(ECHARGER), Item.getItemFromBlock(WET));
		for (int i = 0; i < 4; i++) {
			Item it = Item.getItemFromBlock(ENERGY_MACHINE);
			ItemStack is = new ItemStack(it, 1, i);
			reg.addIngredientInfo(is, ItemStack.class, it.getUnlocalizedName(is) + ".jeidesc");
		}
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration reg) {
		reg.addRecipeCategories(new FluxGenRecipeCategory(reg.getJeiHelpers().getGuiHelper()));
	}

	private void addItemDescriptions(IModRegistry reg, Item... items) {
		for (Item it : items)
			reg.addIngredientInfo(new ItemStack(it), ItemStack.class, it.getUnlocalizedName() + ".jeidesc");
	}

}

