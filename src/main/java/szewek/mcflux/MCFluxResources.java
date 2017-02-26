package szewek.mcflux;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import szewek.mcflux.blocks.*;
import szewek.mcflux.blocks.itemblocks.ItemBlockEnergyMachine;
import szewek.mcflux.blocks.itemblocks.ItemMCFluxBlock;
import szewek.mcflux.items.*;
import szewek.mcflux.recipes.FluxGenRecipes;
import szewek.mcflux.tileentities.TileEntityECharger;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;
import szewek.mcflux.tileentities.TileEntityFluxGen;
import szewek.mcflux.tileentities.TileEntityWET;
import szewek.mcflux.recipes.IX;
import szewek.mcflux.util.MCFluxLocation;
import szewek.mcflux.recipes.RecipeBuilder;
import szewek.mcflux.recipes.RecipeItem;

import java.util.function.Function;

import static szewek.mcflux.recipes.IX.*;

public enum MCFluxResources {
	;
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
	static void preInit() {
		if (state > 0)
			return;
		state++;
		MFTOOL = item("mftool", new ItemMFTool());
		FESNIFFER = item("fesniffer", new ItemFESniffer());
		UPCHIP = item("upchip", new ItemUpChip());
		ASSISTANT = item("fluxassistant", new ItemFluxAssistant());
		SPECIAL = item("mfspecial", new ItemSpecial());
		SIDED = new BlockSided("sided");
		ENERGY_MACHINE = block("energy_machine", new BlockEnergyMachine(), ItemBlockEnergyMachine::new);
		ECHARGER = block("echarger", new BlockEntityCharger(), ItemMCFluxBlock::new);
		WET = block("wet", new BlockWET(), ItemMCFluxBlock::new);
		FLUXGEN = block("fluxgen", new BlockFluxGen(), ItemMCFluxBlock::new);
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
		IX[][] ixStar = new IX[][]{{null, A, null}, {A, B, A}, {null, A, null}};
		IX[][] ixCross = new IX[][]{{A, null, A}, {null, B, null}, {A, null, A}};
		IX[][] ixTool = new IX[][]{{A, null, A}, {B, C, B}, {B, B, B}};
		IX[][] ixSandwich = new IX[][]{{A, A, A}, {B, C, B}, {A, A, A}};
		IX[][] ixWet = new IX[][]{{A, B, A}, {C, D, C}, {A, E, A}};
		RecipeBuilder.buildRecipeFor(MFTOOL, 1)
				.shape(ixTool, 3, 3)
				.with(A, sNugGold)
				.with(B, rRedstone)
				.with(C, sIngIron)
				.deploy();
		RecipeBuilder.buildRecipeFor(FESNIFFER, 1)
				.shape(ixTool, 3, 3)
				.with(A, sNugGold)
				.with(B, rLapis)
				.with(C, sIngGold)
				.deploy();
		RecipeBuilder.buildRecipeFor(ENERGY_MACHINE, 1)
				.shape(ixStar, 3, 3)
				.with(A, sIngIron)
				.with(B, rEnderEye)
				.deploy()
				.resultMeta(1)
				.clear(A, B)
				.shape(ixCross, 3, 3)
				.with(A, rRedstone)
				.with(B, rEnergyDist)
				.deploy()
				.resultMeta(2)
				.clear(A, B)
				.shape(ixStar, 3, 3)
				.with(A, sIngGold)
				.with(B, rEnderEye)
				.deploy()
				.resultMeta(3)
				.clear(A, B)
				.shape(ixCross, 3, 3)
				.with(A, rRedstone)
				.with(B, rFlavorDist)
				.deploy();
		RecipeBuilder.buildRecipeFor(ECHARGER, 1)
				.shape(ixSandwich, 3, 3)
				.with(A, sIngIron)
				.with(B, rLapis)
				.with(C, new RecipeItem(Blocks.GLOWSTONE))
				.deploy();
		RecipeBuilder.buildRecipeFor(WET, 1)
				.shape(ixWet, 3, 3)
				.with(A, rRedstone)
				.with(B, sBlIron)
				.with(C, sIngIron)
				.with(D, rbRedstone)
				.with(E, new RecipeItem(Items.COMPARATOR))
				.deploy();
		RecipeBuilder.buildRecipeFor(FLUXGEN, 1)
				.shape(new IX[][]{{A, B, C}}, 3, 1)
				.mirror(true, false)
				.with(A, sBlIron)
				.with(B, new RecipeItem(Blocks.FURNACE))
				.with(C, rbRedstone)
				.deploy();
	}

	private static <T extends Item> T item(String name, T i) {
		i.setUnlocalizedName(name).setCreativeTab(MCFlux.MCFLUX_TAB);
		return GameRegistry.register(i, new MCFluxLocation(name));
	}

	private static <T extends Block> T block(String name, T b, Function<Block, ItemBlock> ibfn) {
		MCFluxLocation rs = new MCFluxLocation(name);
		b.setUnlocalizedName(name).setCreativeTab(MCFlux.MCFLUX_TAB);
		GameRegistry.register(b, rs);
		GameRegistry.register(ibfn.apply(b), rs);
		return b;
	}
}
