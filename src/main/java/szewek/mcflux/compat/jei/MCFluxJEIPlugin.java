package szewek.mcflux.compat.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import szewek.mcflux.MCFlux;
import szewek.mcflux.compat.jei.crafting.BuiltShapedRecipeHandler;

@JEIPlugin
public class MCFluxJEIPlugin implements IModPlugin {

	@Override
	public void register(IModRegistry reg) {
		reg.addRecipeHandlers(
			new BuiltShapedRecipeHandler()
		);
		addItemDescriptions(reg, MCFlux.MFTOOL, MCFlux.UPCHIP);
		for (int i = 0; i < 2; i++) {
			Item it = Item.getItemFromBlock(MCFlux.ENERGY_MACHINE);
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

