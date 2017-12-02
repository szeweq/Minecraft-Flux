package szewek.mcflux;

import szewek.fl.util.PreRegister;
import szewek.fl.util.RecipeItem;
import szewek.mcflux.blocks.*;
import szewek.mcflux.blocks.itemblocks.ItemBlockEnergyMachine;
import szewek.mcflux.blocks.itemblocks.ItemMCFluxBlock;
import szewek.mcflux.items.ItemMFTool;
import szewek.mcflux.items.ItemSpecial;
import szewek.mcflux.items.ItemUpChip;
import szewek.mcflux.recipes.FluxGenRecipes;
import szewek.mcflux.tileentities.TileEntityECharger;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;
import szewek.mcflux.tileentities.TileEntityFluxGen;
import szewek.mcflux.tileentities.TileEntityWET;

public final class MCFluxResources {
	// ITEMS
	public static ItemMFTool MFTOOL;
	public static ItemUpChip UPCHIP;
	public static ItemSpecial SPECIAL;
	// BLOCKS
	public static BlockSided SIDED;
	public static BlockEnergyMachine ENERGY_MACHINE;
	public static BlockEntityCharger ECHARGER;
	public static BlockWET WET;
	public static BlockFluxGen FLUXGEN;

	/** Current state (0 = untouched; 1 = after preInit; 2 = after init) */
	private static byte state = 0;
	private static boolean created = false;
	static final PreRegister PR = new PreRegister("mcflux", MCFlux.MCFLUX_TAB);

	static void addResources() {
		if (created)
			return;
		created = true;
		SIDED = new BlockSided("sided");
		PR
				.item("mftool", MFTOOL = new ItemMFTool())
				.item("upchip", UPCHIP = new ItemUpChip())
				.item("mfspecial", SPECIAL = new ItemSpecial())
				.block("energy_machine", ENERGY_MACHINE = new BlockEnergyMachine())
				.block("echarger", ECHARGER = new BlockEntityCharger())
				.block("wet", WET = new BlockWET())
				.block("fluxgen", FLUXGEN = new BlockFluxGen())
				.item("echarger", new ItemMCFluxBlock(ECHARGER))
				.item("wet", new ItemMCFluxBlock(WET))
				.item("fluxgen", new ItemMCFluxBlock(FLUXGEN));
		new ItemBlockEnergyMachine(ENERGY_MACHINE, MCFlux.MCFLUX_TAB);
	}

	@SuppressWarnings("unchecked")
	static void preInit() {
		if (state > 0)
			return;
		state++;
		PR.tileEntityClasses(new Class[]{
				TileEntityEnergyMachine.class,
				TileEntityECharger.class,
				TileEntityWET.class,
				TileEntityFluxGen.class
		});
	}

	static void init() {
		if (state > 1)
			return;
		state++;
		FluxGenRecipes.addCatalyst(new RecipeItem(UPCHIP, 0, null), 2, 0);
	}

	private MCFluxResources() {}
}
