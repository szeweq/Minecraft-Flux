package szewek.mcflux;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.blocks.BlockEntityCharger;
import szewek.mcflux.blocks.BlockSided;
import szewek.mcflux.blocks.itemblocks.ItemBlockEnergyMachine;
import szewek.mcflux.blocks.itemblocks.ItemMCFluxBlock;
import szewek.mcflux.items.ItemFESniffer;
import szewek.mcflux.items.ItemMFTool;
import szewek.mcflux.items.ItemUpChip;
import szewek.mcflux.tileentities.TileEntityECharger;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;
import szewek.mcflux.util.IX;
import szewek.mcflux.util.MCFluxLocation;
import szewek.mcflux.util.RecipeBuilder;

import java.util.function.Function;

public enum MCFluxResources {
	;
	// ITEMS
	public static ItemMFTool MFTOOL;
	public static ItemFESniffer FESNIFFER;
	public static ItemUpChip UPCHIP;
	// BLOCKS
	public static BlockSided SIDED;
	public static BlockEnergyMachine ENERGY_MACHINE;
	public static BlockEntityCharger ECHARGER;

	/** Current state (0 = untouched; 1 = after preInit; 2 = after init) */
	private static byte state = 0;
	static void preInit() {
		if (state > 0)
			return;
		state++;
		MFTOOL = item("mftool", new ItemMFTool());
		FESNIFFER = item("fesniffer", new ItemFESniffer());
		UPCHIP = item("upchip", new ItemUpChip());
		SIDED = new BlockSided("sided");
		ENERGY_MACHINE = block("energy_machine", new BlockEnergyMachine(), ItemBlockEnergyMachine::new);
		ECHARGER = block("echarger", new BlockEntityCharger(), ItemMCFluxBlock::new);
		GameRegistry.registerTileEntity(TileEntityEnergyMachine.class, "mcflux.emachine");
		GameRegistry.registerTileEntity(TileEntityECharger.class, "mcflux.echarger");
	}
	static void init() {
		if (state > 1)
			return;
		state++;
		ItemStack iRedstone = new ItemStack(Items.REDSTONE);
		ItemStack iEnderEye = new ItemStack(Items.ENDER_EYE);
		ItemStack iEnergyDist = new ItemStack(ENERGY_MACHINE, 1, 0);
		ItemStack iFlavorDist = new ItemStack(ENERGY_MACHINE, 1, 2);
		IX[][] ixStar = new IX[][]{{null, IX.A, null}, {IX.A, IX.B, IX.A}, {null, IX.A, null}};
		IX[][] ixCross = new IX[][]{{IX.A, null, IX.A}, {null, IX.B, null}, {IX.A, null, IX.A}};
		new RecipeBuilder(MFTOOL)
				.shape(new IX[][]{{IX.A, null, IX.A}, {IX.B, IX.C, IX.B}, {IX.B, IX.B, IX.B}}, 3, 3)
				.with(IX.A, "nuggetGold")
				.with(IX.B, iRedstone)
				.with(IX.C, "ingotIron")
				.deploy();
		new RecipeBuilder(ENERGY_MACHINE)
				.shape(ixStar, 3, 3)
				.with(IX.A, "ingotIron")
				.with(IX.B, iEnderEye)
				.deploy()
				.resultMeta(1)
				.clear(IX.A, IX.B)
				.shape(ixCross, 3, 3)
				.with(IX.A, iRedstone)
				.with(IX.B, iEnergyDist)
				.deploy()
				.resultMeta(2)
				.clear(IX.A, IX.B)
				.shape(ixStar, 3, 3)
				.with(IX.A, "ingotGold")
				.with(IX.B, iEnderEye)
				.deploy()
				.resultMeta(3)
				.clear(IX.A, IX.B)
				.shape(ixCross, 3, 3)
				.with(IX.A, iRedstone)
				.with(IX.B, iFlavorDist)
				.deploy();
		new RecipeBuilder(ECHARGER)
				.shape(new IX[][]{{IX.A, IX.A, IX.A}, {IX.B, IX.C, IX.B}, {IX.A, IX.A, IX.A}}, 3, 3)
				.with(IX.A, "ingotIron")
				.with(IX.B, new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()))
				.with(IX.C, new ItemStack(Blocks.GLOWSTONE))
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
