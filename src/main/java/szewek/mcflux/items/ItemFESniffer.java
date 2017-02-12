package szewek.mcflux.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import szewek.mcflux.api.fe.FE;
import szewek.mcflux.api.fe.IFlavorEnergy;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;

import javax.annotation.Nonnull;

public final class ItemFESniffer extends ItemMCFlux {
	private final TextComponentTranslation
			textBlock = new TextComponentTranslation("mcflux.blockcompat.start"),
			textEntity = new TextComponentTranslation("mcflux.entitycompat.start"),
			textIsCompat = new TextComponentTranslation("mcflux.iscompat"),
			textNoCompat = new TextComponentTranslation("mcflux.nocompat");

	public ItemFESniffer() {
		setMaxStackSize(1);
		textIsCompat.getStyle().setColor(TextFormatting.GREEN).setBold(true);
		textNoCompat.getStyle().setColor(TextFormatting.RED).setBold(true);
	}

	@Nonnull @Override
	public EnumActionResult onItemUse(ItemStack is, EntityPlayer p, World w, BlockPos pos, EnumHand h, EnumFacing f, float x, float y, float z) {
		if (!w.isRemote) {
			TileEntity te = w.getTileEntity(pos);
			if (te != null) {
				if (te instanceof TileEntityEnergyMachine) {
					return EnumActionResult.SUCCESS;
				}
				IFlavorEnergy ife = te.getCapability(FE.CAP_FLAVOR_ENERGY, f);
				TextComponentTranslation tct = textBlock.createCopy();
				tct.appendSibling(ife != null ? textIsCompat : textNoCompat).appendSibling(new TextComponentTranslation("mcflux.blockcompat.feend", f));
				p.addChatComponentMessage(tct);
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack is, EntityPlayer p, EntityLivingBase elb, EnumHand h) {
		if (!elb.worldObj.isRemote) {
			IFlavorEnergy ife = elb.getCapability(FE.CAP_FLAVOR_ENERGY, null);
			TextComponentTranslation tct = textEntity.createCopy();
			tct.appendSibling(ife != null ? textIsCompat : textNoCompat).appendSibling(new TextComponentTranslation("mcflux.entitycompat.feend"));
			p.addChatComponentMessage(tct);
			return true;
		}
		return false;
	}
}
