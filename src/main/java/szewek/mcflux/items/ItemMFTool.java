package szewek.mcflux.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import szewek.mcflux.U;
import szewek.mcflux.api.ex.Battery;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.fluxable.WorldChunkEnergy;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;

public class ItemMFTool extends Item {
	private final TextComponentTranslation
		textBlock = new TextComponentTranslation("mcflux.blockcompat.start"),
		textEntity = new TextComponentTranslation("mcflux.entitycompat.start"),
		textMFCompat = new TextComponentTranslation("mcflux.mfcompat"),
		textNoCompat = new TextComponentTranslation("mcflux.nocompat"),
		textEnergyUnknown = new TextComponentTranslation("mcflux.energystatunknown"),
		textWorldChunk = new TextComponentTranslation("mcflux.worldchunk");

	public ItemMFTool() {
		setMaxStackSize(1);
		textMFCompat.getStyle().setColor(TextFormatting.GREEN).setBold(true);
		textNoCompat.getStyle().setColor(TextFormatting.RED).setBold(true);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack is, EntityPlayer p, World w, BlockPos pos, EnumHand h, EnumFacing f, float x, float y, float z) {
		if (!w.isRemote) {
			TileEntity te = w.getTileEntity(pos);
			if (te != null) {
				if (te instanceof TileEntityEnergyMachine) {
					p.addChatComponentMessage(new TextComponentTranslation("mcflux.transfer", ((TileEntityEnergyMachine) te).getTransferSide(f)));
					return EnumActionResult.SUCCESS;
				}
				IEnergy ie = te.getCapability(IEnergy.CAP_ENERGY, f);
				TextComponentTranslation tcb = textBlock.createCopy();
				tcb.appendSibling(ie != null ? textMFCompat : textNoCompat).appendSibling(new TextComponentTranslation("mcflux.blockcompat.end", f));
				p.addChatComponentMessage(tcb);
				if (ie != null)
					p.addChatComponentMessage(new TextComponentTranslation("mcflux.energystat", U.formatMF(ie.getEnergy(), ie.getEnergyCapacity())));
			} else {
				WorldChunkEnergy wce = w.getCapability(WorldChunkEnergy.CAP_WCE, null);
				Battery bat = wce.getEnergyChunk((int) p.posX, (int) (p.posY + 0.5), (int) p.posZ);
				TextComponentTranslation tcb = textWorldChunk.createCopy();
				tcb.appendSibling(new TextComponentTranslation("mcflux.energystat", U.formatMF(bat.getEnergy(), bat.getEnergyCapacity())));
				p.addChatComponentMessage(tcb);
			}
			return EnumActionResult.SUCCESS;
		}
		return super.onItemUse(is, p, w, pos, h, f, x, y, z);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack is, EntityPlayer p, EntityLivingBase elb, EnumHand h) {
		if (!elb.worldObj.isRemote) {
			IEnergy ie = elb.getCapability(IEnergy.CAP_ENERGY, null);
			TextComponentTranslation tcb = textEntity.createCopy();
			tcb.appendSibling(ie != null ? textMFCompat : textNoCompat);
			tcb.appendSibling(new TextComponentTranslation("mcflux.entitycompat.end"));
			p.addChatComponentMessage(tcb);
			if (ie != null) {
				long n = ie.getEnergy(), nc = ie.getEnergyCapacity();
				p.addChatComponentMessage(nc == 1 ? textEnergyUnknown : new TextComponentTranslation("mcflux.energystat", U.formatMF(n, nc)));
			}
			return true;
		}
		return false;
	}
}
