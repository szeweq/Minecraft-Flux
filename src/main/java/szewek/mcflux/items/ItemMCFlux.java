package szewek.mcflux.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemMCFlux extends Item {
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		tooltip.add(I18n.format(getUnlocalizedName(stack) + ".desc"));
		super.addInformation(stack, player, tooltip, advanced);
	}
}
