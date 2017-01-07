package szewek.mcflux.compat.jei;

import mezz.jei.api.BlankModPlugin;
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
				new BuiltShapedRecipeHandler(reg.getJeiHelpers())
		);
		addItemDescriptions(reg, MFTOOL, UPCHIP, Item.getItemFromBlock(ECHARGER));
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

