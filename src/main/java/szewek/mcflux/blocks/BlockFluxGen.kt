package szewek.mcflux.blocks

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.items.wrapper.InvWrapper
import szewek.fl.block.BlockContainerModeled
import szewek.mcflux.MCFlux
import szewek.mcflux.R
import szewek.mcflux.tileentities.TileEntityFluxGen

class BlockFluxGen : BlockContainerModeled() {
	init {
		setHardness(1f)
	}

	override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
		return TileEntityFluxGen()
	}

	override fun onBlockActivated(w: World?, pos: BlockPos?, state: IBlockState?, p: EntityPlayer?, h: EnumHand?, f: EnumFacing?, x: Float, y: Float, z: Float): Boolean {
		val `is` = p!!.getHeldItem(h)
		if (!w!!.isRemote) {
			val te = w.getTileEntity(pos!!)
			if (te is TileEntityFluxGen) {
				val far = FluidUtil.tryEmptyContainerAndStow(`is`, te as TileEntityFluxGen?, InvWrapper(p.inventory), TileEntityFluxGen.fluidCap, p)
				if (far.success)
					p.setHeldItem(h, far.result)
				else
					p.openGui(MCFlux.MF!!, R.MF_GUI_FLUXGEN, w, pos.x, pos.y, pos.z)
			}
		}
		return true
	}

	override fun onBlockPlacedBy(w: World?, bp: BlockPos?, ibs: IBlockState?, placer: EntityLivingBase?, stack: ItemStack?) {
		if (!w!!.isRemote)
			updateRedstoneState(w, bp)
	}

	override fun neighborChanged(ibs: IBlockState?, w: World?, bp: BlockPos?, b: Block?, fromPos: BlockPos?) {
		if (!w!!.isRemote)
			updateRedstoneState(w, bp)
	}

	override fun breakBlock(w: World, pos: BlockPos, state: IBlockState) {
		val te = w.getTileEntity(pos)
		if (te is TileEntityFluxGen) {
			InventoryHelper.dropInventoryItems(w, pos, (te as TileEntityFluxGen?)!!)
			w.updateComparatorOutputLevel(pos, this)
		}
		super.breakBlock(w, pos, state)
	}

	private fun updateRedstoneState(w: World, bp: BlockPos?) {
		val tefg = w.getTileEntity(bp!!) as TileEntityFluxGen?
		if (tefg != null) {
			val b = tefg.receivedRedstone
			var nb = false
			for (f in EnumFacing.VALUES)
				if (w.getRedstonePower(bp.offset(f, 1), f) > 0) {
					nb = true
					break
				}
			if (b != nb)
				tefg.receivedRedstone = nb
		}
	}
}
