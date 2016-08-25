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
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import szewek.mcflux.R;
import szewek.mcflux.api.IEnergyHolder;

import static szewek.mcflux.api.CapabilityEnergy.ENERGY_CONSUMER;
import static szewek.mcflux.api.CapabilityEnergy.ENERGY_PRODUCER;

public class ItemMFTool extends Item {
	private final TextComponentTranslation
		textBlock = new TextComponentTranslation("mcflux.blockcompat.start"),
		textEntity = new TextComponentTranslation("mcflux.entitycompat.start"),
		textMFCompat = new TextComponentTranslation("mcflux.mfcompat"),
		textNoCompat = new TextComponentTranslation("mcflux.nocompat"),
		textEnergyUnknown = new TextComponentTranslation("mcflux.energystatunknown");
	
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
				boolean e = te.hasCapability(ENERGY_CONSUMER, f) || te.hasCapability(ENERGY_PRODUCER, f);
				TextComponentBase tcb = textBlock.createCopy();
				tcb.appendSibling(e ? textMFCompat : textNoCompat);
				tcb.appendSibling(new TextComponentTranslation("mcflux.blockcompat.end", f));
				p.addChatComponentMessage(tcb);
				if (e) {
					IEnergyHolder ieh = te.getCapability(ENERGY_CONSUMER, null);
					if (ieh == null)
						ieh = te.getCapability(ENERGY_PRODUCER, null);
					p.addChatComponentMessage(new TextComponentTranslation("mcflux.energystat", String.format(R.FORMAT_ENERGY_STAT, ieh.getEnergy(), ieh.getEnergyCapacity())));
				}
				return EnumActionResult.SUCCESS;
			}
		}
		return super.onItemUse(is, p, w, pos, h, f, x, y, z);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack is, EntityPlayer p, EntityLivingBase elb, EnumHand h) {
		if (!elb.worldObj.isRemote) {
			boolean e = elb.hasCapability(ENERGY_CONSUMER, null) || elb.hasCapability(ENERGY_PRODUCER, null);
			TextComponentBase tcb = textEntity.createCopy();
			tcb.appendSibling(e ? textMFCompat : textNoCompat);
			tcb.appendSibling(new TextComponentTranslation("mcflux.entitycompat.end"));
			p.addChatComponentMessage(tcb);
			if (e) {
				IEnergyHolder ieh = elb.getCapability(ENERGY_CONSUMER, null);
				if (ieh == null)
					ieh = elb.getCapability(ENERGY_PRODUCER, null);
				int n = ieh.getEnergy(), nc = ieh.getEnergyCapacity();
				p.addChatComponentMessage(nc == 1 ? textEnergyUnknown : new TextComponentTranslation("mcflux.energystat", String.format(R.FORMAT_ENERGY_STAT, n, nc)));
			}
			return true;
		}
		return false;
	}
}
