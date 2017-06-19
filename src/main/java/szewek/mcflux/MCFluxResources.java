package szewek.mcflux;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import szewek.fl.recipes.R9;
import szewek.fl.recipes.RecipeItem;
import szewek.mcflux.blocks.*;
import szewek.mcflux.blocks.itemblocks.ItemBlockEnergyMachine;
import szewek.mcflux.blocks.itemblocks.ItemMCFluxBlock;
import szewek.mcflux.items.*;
import szewek.mcflux.recipes.FluxGenRecipes;
import szewek.mcflux.tileentities.TileEntityECharger;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;
import szewek.mcflux.tileentities.TileEntityFluxGen;
import szewek.mcflux.tileentities.TileEntityWET;
import szewek.mcflux.util.MCFluxLocation;

import static szewek.fl.recipes.R9.*;

public final class MCFluxResources {
	// ITEMS
	public static ItemMFTool MFTOOL;
	public static ItemFESniffer FESNIFFER;
	public static ItemUpChip UPCHIP;
	public static ItemFluxAssistant ASSISTANT;
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
		FESNIFFER = item("fesniffer", new ItemFESniffer());
		UPCHIP = item("upchip", new ItemUpChip());
		ASSISTANT = item("fluxassistant", new ItemFluxAssistant());
		SPECIAL = item("mfspecial", new ItemSpecial());
		SIDED = new BlockSided("sided");
		ENERGY_MACHINE = block("energy_machine", new BlockEnergyMachine());
		ECHARGER = block("echarger", new BlockEntityCharger());
		WET = block("wet", new BlockWET());
		FLUXGEN = block("fluxgen", new BlockFluxGen());
	}

	static void items(IForgeRegistry<Item> ifr) {
		createResources();
		ifr.registerAll(MFTOOL, FESNIFFER, UPCHIP, ASSISTANT, SPECIAL);
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
		items(GameRegistry.findRegistry(Item.class));
		blocks(GameRegistry.findRegistry(Block.class));
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
		String sIngIron = "ingotIron", sIngGold = "ingotGold", sNugGold = "nuggetGold", sBlIron = "blockIron";
		RecipeItem rRedstone = new RecipeItem(Items.REDSTONE);
		RecipeItem rbRedstone = new RecipeItem(Blocks.REDSTONE_BLOCK);
		RecipeItem rEnderEye = new RecipeItem(Items.ENDER_EYE);
		RecipeItem rLapis = new RecipeItem(Items.DYE, EnumDyeColor.BLUE.getDyeDamage(), null);
		RecipeItem rEnergyDist = new RecipeItem(ENERGY_MACHINE, 0, null);
		RecipeItem rFlavorDist = new RecipeItem(ENERGY_MACHINE, 2, null);
		new szewek.fl.recipes.RecipeBuilder()
				.result(MFTOOL).size(1)
				.shape(new R9[]{A, null, A, B, C, B, A, null, A}, 3, 3)
				.with(A, sNugGold).with(B, rbRedstone).with(C, sIngIron)
				.register()
				.result(FESNIFFER)
				.with(B, rLapis).with(C, sIngGold)
				.register()
				.clear(A, B)
				.result(ENERGY_MACHINE)
				.shape(STAR, 3, 3).with(A, sIngIron).with(B, rEnderEye)
				.register()
				.meta(2)
				.with(A, sIngGold)
				.register()
				.clear(A)
				.meta(1)
				.shape(CROSS, 3, 3).with(A, rbRedstone).with(B, rEnergyDist)
				.register()
				.meta(3).with(B, rFlavorDist)
				.register()
				.result(ECHARGER).meta(0)
				.shape(new R9[]{A, A, A, B, C, B, A, A, A}, 3, 3)
				.with(A, sIngIron).with(B, rLapis).with(C, new RecipeItem(Blocks.GLOWSTONE))
				.register()
				.clear(A, B, C)
				.result(WET)
				.shape(new R9[]{A, B, A, C, D, C, A, E, A}, 3, 3)
				.with(A, rRedstone).with(B, sBlIron).with(C, sIngIron).with(D, rbRedstone).with(E, new RecipeItem(Items.COMPARATOR))
				.register()
				.clear(A, B, C, D, E)
				.result(FLUXGEN)
				.shape(new R9[]{A, B, C}, 3, 1)
				.with(A, sBlIron).with(B, new RecipeItem(Blocks.FURNACE)).with(C, rbRedstone)
				.register();
	}

	private static <T extends Item> T item(String name, T i) {
		i.setUnlocalizedName(name).setCreativeTab(MCFlux.MCFLUX_TAB).setRegistryName(new MCFluxLocation(name));
		return i;
	}

	private static <T extends Block> T block(String name, T b) {
		b.setUnlocalizedName(name).setCreativeTab(MCFlux.MCFLUX_TAB).setRegistryName(new MCFluxLocation(name));
		return b;
	}

	private MCFluxResources() {}
}
