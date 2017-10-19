package szewek.mcflux;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
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

	private static void createResources() {
		if (created)
			return;
		created = true;
		MFTOOL = item("mftool", new ItemMFTool());
		UPCHIP = item("upchip", new ItemUpChip());
		SPECIAL = item("mfspecial", new ItemSpecial());
		SIDED = new BlockSided("sided");
		ENERGY_MACHINE = block("energy_machine", new BlockEnergyMachine());
		ECHARGER = block("echarger", new BlockEntityCharger());
		WET = block("wet", new BlockWET());
		FLUXGEN = block("fluxgen", new BlockFluxGen());
	}

	static void items(IForgeRegistry<Item> ifr) {
		createResources();
		ifr.registerAll(MFTOOL, UPCHIP, SPECIAL);
		ifr.registerAll(
				item("energy_machine", new ItemBlockEnergyMachine(ENERGY_MACHINE)),
				item("echarger", new ItemMCFluxBlock(ECHARGER)),
				item("wet", new ItemMCFluxBlock(WET)),
				item("fluxgen", new ItemMCFluxBlock(FLUXGEN))
		);
	}
	static void blocks(IForgeRegistry<Block> ifr) {
		createResources();
		ifr.registerAll(ENERGY_MACHINE, ECHARGER, WET, FLUXGEN);
	}

	static void preInit() {
		if (state > 0)
			return;
		state++;
		GameRegistry.registerTileEntity(TileEntityEnergyMachine.class, "mcflux:emachine");
		GameRegistry.registerTileEntity(TileEntityECharger.class, "mcflux:echarger");
		GameRegistry.registerTileEntity(TileEntityWET.class, "mcflux:wet");
		GameRegistry.registerTileEntity(TileEntityFluxGen.class, "mcflux:fluxgen");
	}

	static void init() {
		if (state > 1)
			return;
		state++;
		FluxGenRecipes.addCatalyst(new RecipeItem(UPCHIP, 0, null), 2, 0);
	}

	private static <T extends Item> T item(String name, T i) {
		final String rn = R.MF_NAME + ':' + name;
		i.setUnlocalizedName(name).setCreativeTab(MCFlux.MCFLUX_TAB).setRegistryName(rn);
		return i;
	}

	private static <T extends Block> T block(String name, T b) {
		final String rn = R.MF_NAME + ':' + name;
		b.setUnlocalizedName(name).setCreativeTab(MCFlux.MCFLUX_TAB).setRegistryName(rn);
		return b;
	}

	private MCFluxResources() {}
}
