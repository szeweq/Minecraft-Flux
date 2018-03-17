package szewek.mcflux

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.fml.common.registry.GameRegistry
import szewek.fl.util.PreRegister
import szewek.fl.util.RecipeItem
import szewek.mcflux.blocks.*
import szewek.mcflux.blocks.itemblocks.ItemBlockEnergyMachine
import szewek.mcflux.blocks.itemblocks.ItemMCFluxBlock
import szewek.mcflux.items.ItemMFTool
import szewek.mcflux.items.ItemSpecial
import szewek.mcflux.items.ItemUpChip
import szewek.mcflux.recipes.FluxGenRecipes
import szewek.mcflux.tileentities.TileEntityECharger
import szewek.mcflux.tileentities.TileEntityEnergyMachine
import szewek.mcflux.tileentities.TileEntityFluxGen
import szewek.mcflux.tileentities.TileEntityWET

class MCFluxResources {
	// ITEMS
	lateinit var MFTOOL: ItemMFTool
	lateinit var UPCHIP: ItemUpChip
	lateinit var SPECIAL: ItemSpecial
	// BLOCKS
	lateinit var SIDED: BlockSided
	lateinit var ENERGY_MACHINE: BlockEnergyMachine
	lateinit var ECHARGER: BlockEntityCharger
	lateinit var WET: BlockWET
	lateinit var FLUXGEN: BlockFluxGen

	/** Current state (0 = untouched; 1 = after preInit; 2 = after init)  */
	private var state: Byte = 0
	private var created = false

	@JvmField
	internal val PR = PreRegister("mcflux", MCFlux.MCFLUX_TAB)

	fun addResources() {
		if (created)
			return
		created = true

		SIDED = BlockSided("sided")

		MFTOOL = ItemMFTool()
		UPCHIP = ItemUpChip()
		SPECIAL = ItemSpecial()
		ENERGY_MACHINE = BlockEnergyMachine()
		ECHARGER = BlockEntityCharger()
		WET = BlockWET()
		FLUXGEN = BlockFluxGen()

		PR
				.item("mftool", MFTOOL)
				.item("upchip", UPCHIP)
				.item("mfspecial", SPECIAL)
				.block("energy_machine", ENERGY_MACHINE)
				.block("echarger", ECHARGER)
				.block("wet", WET)
				.block("fluxgen", FLUXGEN)
				.item("echarger", ItemMCFluxBlock(ECHARGER))
				.item("wet", ItemMCFluxBlock(WET))
				.item("fluxgen", ItemMCFluxBlock(FLUXGEN))
		ItemBlockEnergyMachine(ENERGY_MACHINE, MCFlux.MCFLUX_TAB)
	}

	internal fun preInit() {
		if (state > 0)
			return
		state++
		PR.registerItems(GameRegistry.findRegistry(Item::class.java))
		PR.registerBlocks(GameRegistry.findRegistry(Block::class.java))
		PR.tileEntityClasses(arrayOf(TileEntityEnergyMachine::class.java, TileEntityECharger::class.java, TileEntityWET::class.java, TileEntityFluxGen::class.java))
	}

	internal fun init() {
		if (state > 1)
			return
		state++
		FluxGenRecipes.addCatalyst(RecipeItem(UPCHIP, 0, null), 2, 0)
	}
}
