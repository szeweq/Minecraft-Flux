package szewek.mcflux.compat.jei;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import szewek.mcflux.MCFluxResources;
import szewek.mcflux.compat.jei.crafting.BuiltShapedRecipeHandler;

@JEIPlugin
public class MCFluxJEIPlugin implements IModPlugin {

	@Override public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
	}

	@Override public void registerIngredients(IModIngredientRegistration registry) {
	}

	@Override
	public void register(IModRegistry reg) {
		reg.addRecipeHandlers(
			new BuiltShapedRecipeHandler()
		);
		addItemDescriptions(reg, MCFluxResources.MFTOOL, MCFluxResources.UPCHIP);
		for (int i = 0; i < 2; i++) {
			Item it = Item.getItemFromBlock(MCFluxResources.ENERGY_MACHINE);
			ItemStack is = new ItemStack(it, 1, i);
			reg.addDescription(is, it.getUnlocalizedName(is) + ".jeidesc");
		}
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRT) {
	}

	private void addItemDescriptions(IModRegistry reg, Item... items) {
		for (Item it : items)
			reg.addDescription(new ItemStack(it), it.getUnlocalizedName() + ".jeidesc");
	}

}

