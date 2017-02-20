package szewek.mcflux.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import szewek.mcflux.fluxable.PlayerEnergy;

public final class ItemUpChip extends ItemMCFlux {
	private static final String PF = "mcflux.upchip.";
	private static final TextComponentTranslation
			textInstalled = new TextComponentTranslation(PF + "installed"),
			textLvlMax = new TextComponentTranslation(PF + "lvlmax");

	@Override public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override public int getMaxItemUseDuration(ItemStack stack) {
		return 40;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World w, EntityPlayer p, EnumHand h) {
		p.setActiveHand(h);
		return new ActionResult<>(EnumActionResult.SUCCESS, p.getHeldItem(h));
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack is, World w, EntityLivingBase elb) {
		if (!w.isRemote && elb instanceof EntityPlayerMP) {
			EntityPlayerMP mp = (EntityPlayerMP) elb;
			PlayerEnergy pe = mp.getCapability(PlayerEnergy.SELF_CAP, null);
			if (pe == null)
				return is;
			byte lvl = pe.updateLevel();
			if (lvl == -1)
				return is;
			is.grow(-1);
			mp.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.TITLE, textInstalled, 50, 500, 50));
			mp.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.SUBTITLE, lvl == 30 ? textLvlMax : new TextComponentTranslation(PF + "lvlup", lvl)));
			StatBase stat = StatList.getObjectUseStats(this);
			if (stat != null) mp.addStat(stat);
		}
		return is;
	}
}
