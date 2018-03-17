package szewek.mcflux.containers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IContainerListener
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import szewek.mcflux.recipes.FluxGenRecipes
import szewek.mcflux.tileentities.TileEntityFluxGen

class ContainerFluxGen(p: EntityPlayer, private val fluxGen: TileEntityFluxGen) : Container() {
	private val vals: IntArray

	init {
		fluxGen.openInventory(p)
		vals = IntArray(fluxGen.fieldCount)
		addSlotToContainer(Slot(fluxGen, 0, 67, 35))
		addSlotToContainer(Slot(fluxGen, 1, 93, 35))
		var xBase: Int
		var yBase = 84
		for (y in 0..2) {
			xBase = 8
			for (x in 0..8) {
				addSlotToContainer(Slot(p.inventory, x + 9 * y + 9, xBase, yBase))
				xBase += 18
			}
			yBase += 18
		}
		xBase = 8
		yBase += 4
		for (w in 0..8) {
			addSlotToContainer(Slot(p.inventory, w, xBase, yBase))
			xBase += 18
		}
	}

	override fun addListener(listener: IContainerListener) {
		super.addListener(listener)
		listener.sendAllWindowProperties(this, fluxGen)
	}

	override fun detectAndSendChanges() {
		super.detectAndSendChanges()
		for (icl in listeners) {
			for (i in vals.indices) {
				val f = fluxGen.getField(i)
				if (vals[i] != f) {
					vals[i] = f
					icl.sendWindowProperty(this, i, f)
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	override fun updateProgressBar(id: Int, data: Int) = fluxGen.setField(id, data)

	override fun canInteractWith(p: EntityPlayer) = fluxGen.isUsableByPlayer(p)

	override fun transferStackInSlot(p: EntityPlayer?, index: Int): ItemStack {
		var nis = ItemStack.EMPTY
		val sl = inventorySlots[index]
		if (sl != null && sl.hasStack) {
			val bis = sl.stack
			nis = bis.copy()
			var s = 2
			var e = 38
			if (index > 1) when {
				TileEntityFurnace.getItemBurnTime(bis) > 0 -> {
					s = 0
					e = 1
				}
				FluxGenRecipes.isCatalyst(bis) -> {
					s = 1
					e = 2
				}
				index < 29 -> s = 29
				index < 38 -> e = 29
			}
			if (!mergeItemStack(bis, s, e, false))
				return ItemStack.EMPTY
			if (nis.isEmpty)
				sl.putStack(ItemStack.EMPTY)
			else
				sl.onSlotChanged()
			if (nis.count == bis.count)
				return ItemStack.EMPTY
			sl.onTake(p, nis)
		}
		return nis
	}
}
