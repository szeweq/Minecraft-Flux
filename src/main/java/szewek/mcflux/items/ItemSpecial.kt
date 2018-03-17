package szewek.mcflux.items

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.EnumAction
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import net.minecraftforge.items.ItemHandlerHelper
import szewek.mcflux.special.SpecialEventHandler

class ItemSpecial : ItemMCFlux() {

	override fun getItemUseAction(stack: ItemStack) = EnumAction.EAT

	override fun getMaxItemUseDuration(stack: ItemStack) = 30

	override fun onItemRightClick(w: World, p: EntityPlayer, h: EnumHand): ActionResult<ItemStack> {
		p.activeHand = h
		return ActionResult(EnumActionResult.SUCCESS, p.getHeldItem(h))
	}

	override fun onItemUseFinish(stk: ItemStack, w: World, elb: EntityLivingBase): ItemStack {
		if (!w.isRemote && elb is EntityPlayerMP && stk.item === this && stk.hasTagCompound()) {
			val mp = elb as EntityPlayerMP?
			val nbt = stk.tagCompound!!
			val se = SpecialEventHandler.getEvent(nbt.getInteger("seid")) ?: return stk
			if (se.endTime <= System.currentTimeMillis() / 60000) {
				mp!!.sendMessage(TextComponentTranslation("mcflux.special.ended"))
				return ItemStack.EMPTY
			}
			val items = se.createItems()
			for (item in items) {
				if (item == ItemStack.EMPTY)
					continue
				ItemHandlerHelper.giveItemToPlayer(mp!!, item, -1)
			}
			stk.grow(-1)
			mp!!.sendMessage(TextComponentTranslation("mcflux.special.desc", se.description))
		}
		return stk
	}
}
