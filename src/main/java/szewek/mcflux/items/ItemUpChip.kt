package szewek.mcflux.items

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.EnumAction
import net.minecraft.item.ItemStack
import net.minecraft.network.play.server.SPacketTitle
import net.minecraft.stats.StatList
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import szewek.mcflux.fluxable.FluxableCapabilities

class ItemUpChip : ItemMCFlux() {

	override fun getItemUseAction(stack: ItemStack?) = EnumAction.BOW

	override fun getMaxItemUseDuration(stack: ItemStack) = 40

	override fun onItemRightClick(w: World, p: EntityPlayer, h: EnumHand): ActionResult<ItemStack> {
		p.activeHand = h
		return ActionResult(EnumActionResult.SUCCESS, p.getHeldItem(h))
	}

	override fun onItemUseFinish(stk: ItemStack, w: World, elb: EntityLivingBase): ItemStack {
		if (!w.isRemote && elb is EntityPlayerMP) {
			val mp = elb as EntityPlayerMP?
			val pe = mp!!.getCapability(FluxableCapabilities.CAP_PE, null) ?: return stk
			val lvl = pe.updateLevel()
			if (lvl.toInt() == -1)
				return stk
			stk.grow(-1)
			mp.connection.sendPacket(SPacketTitle(SPacketTitle.Type.TITLE, textInstalled, 50, 500, 50))
			mp.connection.sendPacket(SPacketTitle(SPacketTitle.Type.SUBTITLE, if (lvl.toInt() == 30) textLvlMax else TextComponentTranslation(PF + "lvlup", lvl)))
			val stat = StatList.getObjectUseStats(this)
			if (stat != null) mp.addStat(stat)
		}
		return stk
	}

	companion object {
		private const val PF = "mcflux.upchip."
		private val textInstalled = TextComponentTranslation(PF + "installed")
		private val textLvlMax = TextComponentTranslation(PF + "lvlmax")
	}
}
