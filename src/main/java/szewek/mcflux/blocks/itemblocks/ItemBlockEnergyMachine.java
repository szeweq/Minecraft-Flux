package szewek.mcflux.blocks.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import szewek.mcflux.blocks.BlockEnergyMachine;

import java.util.List;

@SuppressWarnings("deprecation")
public class ItemBlockEnergyMachine extends ItemBlock {

	public ItemBlockEnergyMachine(Block block) {
		super(block);
	}
	
	@Override
	public int getMetadata(int damage) {
		return damage % BlockEnergyMachine.Variant.ALL_VARIANTS.length;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "tile." + BlockEnergyMachine.Variant.nameFromStack(stack);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		tooltip.add(I18n.translateToLocal(getUnlocalizedName(stack) + ".desc"));
		super.addInformation(stack, player, tooltip, advanced);
	}
}
