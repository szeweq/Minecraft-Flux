package szewek.mcflux.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumBlockRenderType
import net.minecraftforge.fml.common.registry.GameRegistry
import szewek.mcflux.util.MCFluxLocation

class BlockSided(name: String) : Block(Material.ROCK) {

	init {
		val rs = MCFluxLocation(name)
		unlocalizedName = name
		registryName = rs
		GameRegistry.findRegistry(Block::class.java).register(this)
		GameRegistry.findRegistry(Item::class.java).register(ItemBlock(this).setRegistryName(rs))
	}

	override fun getMetaFromState(state: IBlockState) = 0

	override fun createBlockState() = BlockStateContainer(this, DOWN, UP, NORTH, SOUTH, WEST, EAST)

	override fun getRenderType(state: IBlockState?) = EnumBlockRenderType.MODEL

	companion object {
		private val UP = PropertyInteger.create("up", 0, 2)
		private val DOWN = PropertyInteger.create("down", 0, 2)
		private val NORTH = PropertyInteger.create("north", 0, 2)
		private val SOUTH = PropertyInteger.create("south", 0, 2)
		private val EAST = PropertyInteger.create("east", 0, 2)
		private val WEST = PropertyInteger.create("west", 0, 2)

		fun sideFromId(id: Int): PropertyInteger {
			when (id) {
				0 -> return DOWN
				1 -> return UP
				2 -> return NORTH
				3 -> return SOUTH
				4 -> return WEST
				5 -> return EAST
				else -> return DOWN
			}
		}
	}
}
