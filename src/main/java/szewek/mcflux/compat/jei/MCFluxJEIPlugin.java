package szewek.mcflux.compat.jei;

import mezz.jei.api.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import szewek.mcflux.MCFluxResources;
import szewek.mcflux.compat.jei.crafting.BuiltShapedRecipeHandler;
import szewek.mcflux.compat.jei.fluxgen.FluxGenRecipeCategory;
import szewek.mcflux.compat.jei.fluxgen.FluxGenRecipeHandler;
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
		IGuiHelper igh = hlp.getGuiHelper();
		reg.addRecipeCategories(
				new FluxGenRecipeCategory(igh)
		);
		reg.addRecipeHandlers(
				new BuiltShapedRecipeHandler(hlp),
				new FluxGenRecipeHandler()
		);
		reg.addRecipeClickArea(GuiFluxGen.class, 84, 34, 4, 18, ID_FLUXGEN);
		reg.addRecipes(FluxGenRecipeMaker.getFluxGenRecipes(hlp));
		reg.addRecipeCategoryCraftingItem(new ItemStack(MCFluxResources.FLUXGEN), ID_FLUXGEN);
		addItemDescriptions(reg, MFTOOL, UPCHIP, Item.getItemFromBlock(ECHARGER), Item.getItemFromBlock(WET));
		for (int i = 0; i < 4; i++) {
			Item it = Item.getItemFromBlock(ENERGY_MACHINE);
			ItemStack is = new ItemStack(it, 1, i);
			reg.addDescription(is, it.getUnlocalizedName(is) + ".jeidesc");
		}
	}

	private void addItemDescriptions(IModRegistry reg, Item... items) {
		for (Item it : items)
			reg.addDescription(new ItemStack(it), it.getUnlocalizedName() + ".jeidesc");
	}

}

