package szewek.mcflux.blocks.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemMultiTexture;
import szewek.mcflux.blocks.BlockEnergyMachine;

public class ItemBlockEnergyMachine extends ItemMultiTexture {

	public ItemBlockEnergyMachine(Block block) {
		super(block, block, BlockEnergyMachine.Variant::nameFromStack);
	}
	
	@Override
	public int getMetadata(int damage) {
		return damage % BlockEnergyMachine.Variant.ALL_VARIANTS.length;
	}
}
