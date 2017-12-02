package szewek.mcflux.blocks.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import szewek.mcflux.blocks.BlockEnergyMachine;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public final class ItemBlockEnergyMachine extends ItemBlock {

	public ItemBlockEnergyMachine(Block block, CreativeTabs ct) {
		super(block);
		setHasSubtypes(true);
		setCreativeTab(ct);
		setUnlocalizedName("mcflux:energy_machine");
		setRegistryName("mcflux:energy_machine");
		GameRegistry.findRegistry(Item.class).register(this);
	}
	
	@Override
	public int getMetadata(int damage) {
		return damage % BlockEnergyMachine.Variant.ALL_VARIANTS.length;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "tile.mcflux:" + BlockEnergyMachine.Variant.nameFromStack(stack);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format(getUnlocalizedName(stack) + ".desc"));
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
}
