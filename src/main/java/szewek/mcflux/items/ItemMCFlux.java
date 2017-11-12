package szewek.mcflux.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMCFlux extends Item {
	@Override
	public void addInformation(ItemStack stack, @Nullable World w, List<String> tooltip, ITooltipFlag flagIn) {
		final String key = getUnlocalizedName(stack) + ".desc";
		if (I18n.hasKey(key))
			tooltip.add(I18n.format(key));
		super.addInformation(stack, w, tooltip, flagIn);
	}
}
