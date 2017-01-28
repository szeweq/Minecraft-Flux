package szewek.mcflux.blocks.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import szewek.mcflux.blocks.BlockEnergyMachine;

import javax.annotation.Nonnull;
import java.util.List;

@SuppressWarnings("deprecation")
public class ItemBlockEnergyMachine extends ItemBlock {

	public ItemBlockEnergyMachine(Block block) {
		super(block);
		setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(int damage) {
		return damage % BlockEnergyMachine.Variant.ALL_VARIANTS.length;
	}
	
	@Nonnull @Override
	public String getUnlocalizedName(ItemStack stack) {
		return "tile." + BlockEnergyMachine.Variant.nameFromStack(stack);
	}
	
	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull List<String> tooltip, boolean advanced) {
		tooltip.add(I18n.format(getUnlocalizedName(stack) + ".desc"));
		super.addInformation(stack, player, tooltip, advanced);
	}
}
