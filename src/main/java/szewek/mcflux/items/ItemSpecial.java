package szewek.mcflux.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import szewek.fl.FL;
import szewek.mcflux.special.SpecialEvent;
import szewek.mcflux.special.SpecialEventHandler;

import javax.annotation.Nonnull;

public final class ItemSpecial extends ItemMCFlux {

	@Override public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.EAT;
	}

	@Override public int getMaxItemUseDuration(ItemStack stack) {
		return 30;
	}

	@Override public ActionResult<ItemStack> onItemRightClick(World w, EntityPlayer p, EnumHand h) {
		p.setActiveHand(h);
		return new ActionResult<>(EnumActionResult.SUCCESS, p.getHeldItem(h));
	}

	@Override public ItemStack onItemUseFinish(@Nonnull ItemStack is, World w, EntityLivingBase elb) {
		if (!w.isRemote && elb instanceof EntityPlayerMP && is.getItem() == this && is.hasTagCompound()) {
			EntityPlayerMP mp = (EntityPlayerMP) elb;
			NBTTagCompound nbt = is.getTagCompound();
			assert nbt != null;
			SpecialEvent se = SpecialEventHandler.getEvent(nbt.getInteger("seid"));
			if (se == null)
				return is;
			if (se.endTime <= System.currentTimeMillis() / 60000) {
				mp.sendMessage(new TextComponentTranslation("mcflux.special.ended"));
				return ItemStack.EMPTY;
			}
			ItemStack[] items = se.createItems();
			for (ItemStack item : items) {
				if (item == null)
					continue;
				FL.giveItemToPlayer(item, mp);
			}
			is.grow(-1);
			mp.sendMessage(new TextComponentTranslation("mcflux.special.desc", se.description));
		}
		return is;
	}
}
