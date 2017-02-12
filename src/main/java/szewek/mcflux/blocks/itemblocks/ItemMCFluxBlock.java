package szewek.mcflux.blocks.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public final class ItemMCFluxBlock extends ItemBlock {
	public ItemMCFluxBlock(Block block) {
		super(block);
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull List<String> tooltip, boolean advanced) {
		tooltip.add(I18n.format(getUnlocalizedName(stack) + ".desc"));
		super.addInformation(stack, player, tooltip, advanced);
	}
}
