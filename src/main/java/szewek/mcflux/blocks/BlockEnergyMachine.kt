package szewek.mcflux.blocks

import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import szewek.fl.block.BlockContainerModeled
import szewek.mcflux.tileentities.TileEntityEnergyMachine

class BlockEnergyMachine : BlockContainerModeled() {
	init {
		setHardness(0.5f)
	}

	override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess?, pos: BlockPos?, state: IBlockState, fortune: Int) {
		drops += ItemStack(this, 1, state.getValue(VARIANT).ordinal)
	}

	override fun damageDropped(state: IBlockState): Int = state.getValue(VARIANT).ordinal

	override fun createNewTileEntity(w: World, m: Int): TileEntity? {
		val teem = TileEntityEnergyMachine()
		teem.moduleId = m
		return teem
	}

	override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
		for (i in Variant.ALL_VARIANTS.indices)
			list += ItemStack(this, 1, i)
	}

	override fun getStateFromMeta(meta: Int) =
			defaultState.withProperty(VARIANT, Variant.ALL_VARIANTS[meta % Variant.ALL_VARIANTS.size])

	override fun getMetaFromState(state: IBlockState) = state.getValue(VARIANT).ordinal
	override fun createBlockState() = BlockStateContainer(this, VARIANT)
	override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos) = DEF_AABB
	override fun isFullCube(state: IBlockState) = false
	override fun isOpaqueCube(state: IBlockState) = false

	override fun onBlockActivated(w: World?, bp: BlockPos?, ibs: IBlockState?, p: EntityPlayer?, h: EnumHand?, f: EnumFacing?, x: Float, y: Float, z: Float): Boolean {
		val b = h == EnumHand.MAIN_HAND && p!!.getHeldItem(h).isEmpty
		if (b && !w!!.isRemote) {
			val te = w.getTileEntity(bp!!)
			if (te != null && te is TileEntityEnergyMachine)
				te.switchSideTransfer(f!!)
		}
		return b
	}

	enum class Variant constructor(private val vname: String) : IStringSerializable {
		ENERGY_DIST("energy_dist"), CHUNK_CHARGER("chunk_charger");

		override fun getName() = vname

		companion object {

			@JvmField
			val ALL_VARIANTS: Array<Variant> = Variant.values()

			fun nameFromStack(stk: ItemStack): String {
				return ALL_VARIANTS[stk.metadata % ALL_VARIANTS.size].vname
			}

		}
	}

	companion object {
		private val DEF_AABB = AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75)
		val VARIANT = PropertyEnum.create("variant", Variant::class.java)
	}
}
