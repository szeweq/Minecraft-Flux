package szewek.mcflux.blocks

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import szewek.fl.FLU
import szewek.fl.block.BlockContainerModeled
import szewek.mcflux.tileentities.TileEntityECharger

class BlockEntityCharger : BlockContainerModeled() {
	init {
		setHardness(0.5f)
	}

	override fun createNewTileEntity(w: World, meta: Int) = TileEntityECharger()
	override fun getBoundingBox(bs: IBlockState?, source: IBlockAccess?, pos: BlockPos?) = DEF_AABB
	override fun isNormalCube(bs: IBlockState, world: IBlockAccess?, pos: BlockPos?) = false
	override fun isSideSolid(bs: IBlockState, w: IBlockAccess, pos: BlockPos, f: EnumFacing?) = false

	override fun doesSideBlockRendering(bs: IBlockState, w: IBlockAccess?, pos: BlockPos?, f: EnumFacing?) = false

	override fun isFullCube(bs: IBlockState?) = false
	override fun isOpaqueCube(bs: IBlockState?) = false
	override fun getLightValue(bs: IBlockState, w: IBlockAccess?, pos: BlockPos?) = 15
	override fun getLightOpacity(bs: IBlockState, w: IBlockAccess?, pos: BlockPos?) = 255

	override fun onEntityCollidedWithBlock(w: World, pos: BlockPos, state: IBlockState?, e: Entity) {
		val teec = w.getTileEntity(pos) as TileEntityECharger?
		val ie = FLU.getEnergySafely(e, null)
		if (teec == null || ie == null)
			return
		val edx = e.posX - pos.x
		val edz = e.posZ - pos.z
		val pdx = e.prevPosX - pos.x
		val pdz = e.prevPosZ - pos.z
		val inX = edx in 0.125..0.875
		val inZ = edz in 0.125..0.875
		val pinX = pdx in 0.125..0.875
		val pinZ = pdz in 0.125..0.875
		val crossX = inX != pinX
		val crossZ = inZ != pinZ
		if (inX && inZ) {
			if (crossX || crossZ) {
				// Entity is standing on a block
				teec.addEntityEnergy(ie)
			}
		}
		if (pinX && pinZ) {
			if (crossX || crossZ) {
				// Entity moved away from a block
				teec.removeEntityEnergy(ie)
			}
		}
	}

	override fun onFallenUpon(w: World?, pos: BlockPos?, e: Entity, fell: Float) {
		e.fall(fell, 0.5f)
		if (fell > 3.5f)
			w!!.newExplosion(e, pos!!.x + 0.5, pos.y + 0.5, pos.z + 0.5, 1.5f * fell, false, false)
	}

	companion object {
		private val DEF_AABB = AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 0.1875, 0.875)
	}
}
