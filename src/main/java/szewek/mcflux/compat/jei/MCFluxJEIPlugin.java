package szewek.mcflux.compat.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import szewek.mcflux.compat.jei.crafting.BuiltShapedRecipeHandler;

import javax.annotation.Nonnull;

import static szewek.mcflux.MCFluxResources.*;

@JEIPlugin
public class MCFluxJEIPlugin extends BlankModPlugin {

	@Override
	public void register(@Nonnull IModRegistry reg) {
		reg.addRecipeHandlers(
			new BuiltShapedRecipeHandler()
		);
		addItemDescriptions(reg, MFTOOL, UPCHIP, Item.getItemFromBlock(ECHARGER), Item.getItemFromBlock(WET));
		for (int i = 0; i < 2; i++) {
			Item it = Item.getItemFromBlock(ENERGY_MACHINE);
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

