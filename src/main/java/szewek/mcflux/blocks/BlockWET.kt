package szewek.mcflux.blocks

import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.Mirror
import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import szewek.fl.block.BlockContainerModeled
import szewek.mcflux.tileentities.TileEntityWET

class BlockWET : BlockContainerModeled() {
	init {
		setHardness(1f)
	}

	override fun createNewTileEntity(w: World, meta: Int) = TileEntityWET()

	override fun getStateForPlacement(w: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase, hand: EnumHand?): IBlockState {
		return defaultState.withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)).withProperty(MODE, 0)
	}

	override fun onBlockPlacedBy(w: World, pos: BlockPos?, state: IBlockState, placer: EntityLivingBase?, stack: ItemStack?) {
		w.setBlockState(pos!!, state.withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer!!)), 2)
	}

	override fun onBlockActivated(w: World?, bp: BlockPos?, ibs: IBlockState?, p: EntityPlayer?, h: EnumHand?, f: EnumFacing?, x: Float, y: Float, z: Float): Boolean {
		val b = h == EnumHand.MAIN_HAND && p!!.getHeldItem(h).isEmpty
		if (b && !w!!.isRemote)
			w.setBlockState(bp!!, ibs!!.cycleProperty(MODE), 3)
		return b
	}

	override fun getStateFromMeta(meta: Int): IBlockState {
		return defaultState.withProperty(MODE, meta % 2).withProperty(FACING, EnumFacing.VALUES[meta / 2 % 6])
	}

	override fun getMetaFromState(state: IBlockState): Int {
		return state.getValue(MODE) + 2 * state.getValue(FACING).index
	}

	override fun withRotation(state: IBlockState, rot: Rotation): IBlockState {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)))
	}

	override fun withMirror(state: IBlockState, mir: Mirror): IBlockState {
		return state.withRotation(mir.toRotation(state.getValue(FACING)))
	}

	override fun createBlockState(): BlockStateContainer {
		return BlockStateContainer(this, FACING, MODE)
	}

	companion object {
		private val FACING = PropertyDirection.create("f")
		val MODE = PropertyInteger.create("m", 0, 1)
	}
}
