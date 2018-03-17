package szewek.mcflux.items

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import szewek.fl.FLU
import szewek.mcflux.U
import szewek.mcflux.fluxable.FluxableCapabilities
import szewek.mcflux.fluxcompat.FluxCompat
import szewek.mcflux.tileentities.TileEntityEnergyMachine

class ItemMFTool : ItemMCFlux() {
	private val textBlock = TextComponentTranslation("mcflux.blockcompat.start")
	private val textEntity = TextComponentTranslation("mcflux.entitycompat.start")
	private val textIsCompat = TextComponentTranslation("mcflux.iscompat")
	private val textNoCompat = TextComponentTranslation("mcflux.nocompat")
	private val textEnergyUnknown = TextComponentTranslation("mcflux.energystatunknown")
	private val textWorldChunk = TextComponentTranslation("mcflux.worldchunk")

	init {
		setMaxStackSize(1)
		textIsCompat.style.setColor(TextFormatting.GREEN).bold = true
		textNoCompat.style.setColor(TextFormatting.RED).bold = true
	}

	override fun onItemUse(p: EntityPlayer, w: World, pos: BlockPos, h: EnumHand, f: EnumFacing, x: Float, y: Float, z: Float): EnumActionResult {
		if (!w.isRemote) {
			val te = w.getTileEntity(pos)
			if (te != null) {
				if (te is TileEntityEnergyMachine) {
					val teem = te as TileEntityEnergyMachine?
					if (teem!!.moduleId < 2)
						p.sendMessage(TextComponentTranslation("mcflux.transfer", te.getTransferSide(f)))
					return EnumActionResult.SUCCESS
				}
				val ie = FLU.getEnergySafely(te, f)
				val tcb = textBlock.createCopy()
				tcb.appendSibling(if (ie != null) textIsCompat else textNoCompat).appendSibling(TextComponentTranslation("mcflux.blockcompat.end", f))
				p.sendMessage(tcb)
				if (ie != null)
					p.sendMessage(TextComponentTranslation("mcflux.energystat", U.formatMF(ie)))
				if (ie is FluxCompat.Convert)
					p.sendMessage(TextComponentTranslation("mcflux.fluxcompat.type", (ie as FluxCompat.Convert).energyType.name))

			} else {
				val wce = w.getCapability(FluxableCapabilities.CAP_WCE, null)
				if (wce != null) {
					val bat = wce.getEnergyChunk(p.posX.toInt(), (p.posY + 0.5).toInt(), p.posZ.toInt())
					val tcb = textWorldChunk.createCopy()
					tcb.appendSibling(TextComponentTranslation("mcflux.energystat", U.formatMF(bat)))
					p.sendMessage(tcb)
				} else {
					return EnumActionResult.PASS
				}
			}
			return EnumActionResult.SUCCESS
		} else if (p.isSneaking) {
			return EnumActionResult.SUCCESS
		}
		return EnumActionResult.PASS
	}

	override fun itemInteractionForEntity(stk: ItemStack, p: EntityPlayer, elb: EntityLivingBase, h: EnumHand): Boolean {
		if (!elb.world.isRemote) {
			val ie = FLU.getEnergySafely(elb, EnumFacing.UP) // FIXME: SET TO null
			val tcb = textEntity.createCopy()
			tcb.appendSibling(if (ie != null) textIsCompat else textNoCompat)
			tcb.appendSibling(TextComponentTranslation("mcflux.entitycompat.end"))
			p.sendMessage(tcb)
			if (ie != null) {
				val nc = ie.energyCapacity
				p.sendMessage(if (nc == 1L) textEnergyUnknown else TextComponentTranslation("mcflux.energystat", U.formatMF(ie)))
			}
			if (ie is FluxCompat.Convert)
				p.sendMessage(TextComponentTranslation("mcflux.fluxcompat.type", (ie as FluxCompat.Convert).energyType.name))
			return true
		}
		return false
	}
}
