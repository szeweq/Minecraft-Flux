package szewek.mcflux;

import org.apache.logging.log4j.Logger;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.RecipeSorter;
import szewek.mcflux.api.EnergyBattery;
import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.IEnergyProducer;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.flavor.CapabilityFlavorEnergy;
import szewek.mcflux.api.flavor.FlavorEnergyContainer;
import szewek.mcflux.api.flavor.IFlavorEnergyConsumer;
import szewek.mcflux.api.flavor.IFlavorEnergyProducer;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.blocks.itemblocks.ItemBlockEnergyMachine;
import szewek.mcflux.fluxable.InjectFluxable;
import szewek.mcflux.fluxable.WorldChunkEnergy;
import szewek.mcflux.items.ItemMFTool;
import szewek.mcflux.proxy.ProxyCommon;
import szewek.mcflux.tileentities.TileEntityChunkCharger;
import szewek.mcflux.tileentities.TileEntityEnergyDistributor;
import szewek.mcflux.util.RecipeBuilder;
import szewek.mcflux.wrapper.InjectWrappers;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

import java.util.function.Function;

@Mod(modid = R.MCFLUX_NAME, version = R.MCFLUX_VERSION)
public class MCFlux {
	private static Logger log;
	private static ItemMFTool MFTOOL;
	public static BlockEnergyMachine ENERGY_MACHINE;
	private static final CreativeTabs MCFLUX_TAB = new CreativeTabs(R.MCFLUX_NAME) {
		@Override
		public Item getTabIconItem() {
			return MFTOOL;
		}
	};
	@SidedProxy(modId = R.MCFLUX_NAME, serverSide = R.PROXY_SERVER, clientSide = R.PROXY_CLIENT)
	public static ProxyCommon PROXY = null;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		log = e.getModLog();
		if (R.MCFLUX_VERSION.charAt(0) == '@')
			log.warn("You are running Minecraft-Flux with an unknown version (development maybe?)");
		else
			log.info("Minecraft-Flux version " + R.MCFLUX_VERSION);
		CapabilityManager.INSTANCE.register(IEnergyProducer.class, new CapabilityEnergy.Storage<IEnergyProducer>(), EnergyBattery::new);
		CapabilityManager.INSTANCE.register(IEnergyConsumer.class, new CapabilityEnergy.Storage<IEnergyConsumer>(), EnergyBattery::new);
		CapabilityManager.INSTANCE.register(IFlavorEnergyProducer.class, new CapabilityFlavorEnergy.Storage<IFlavorEnergyProducer>(), FlavorEnergyContainer::new);
		CapabilityManager.INSTANCE.register(IFlavorEnergyConsumer.class, new CapabilityFlavorEnergy.Storage<IFlavorEnergyConsumer>(), FlavorEnergyContainer::new);
		CapabilityManager.INSTANCE.register(WorldChunkEnergy.class, new WorldChunkEnergy.ChunkStorage(), WorldChunkEnergy::new);
		EVENT_BUS.register(InjectWrappers.INSTANCE);
		EVENT_BUS.register(InjectFluxable.INSTANCE);
		MFTOOL = registerItem("mftool", new ItemMFTool());
		ENERGY_MACHINE = registerBlock("energy_machine", new BlockEnergyMachine(), ItemBlockEnergyMachine::new);
		GameRegistry.registerTileEntity(TileEntityEnergyDistributor.class, "mcflux.energyDist");
		GameRegistry.registerTileEntity(TileEntityChunkCharger.class, "mcflux.chunkCharger");
		PROXY.preInit();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		RecipeSorter.register("mcflux:builtRecipe", RecipeBuilder.BuiltShapedRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
		GameRegistry.addShapedRecipe(new ItemStack(MFTOOL), new String[] {"n n", "rir", "rrr"}, 'n', Items.GOLD_NUGGET, 'r', Items.REDSTONE, 'i', Items.IRON_INGOT);
		FMLInterModComms.sendMessage("Waila", "register", R.WAILA_REGISTER);
		PROXY.init();
	}

	@SideOnly(Side.CLIENT)
	public static void renders() {
		U.registerItemModels(MFTOOL);
	}

	private static <T extends Item> T registerItem(String name, T i) {
		i.setUnlocalizedName(name).setCreativeTab(MCFLUX_TAB);
		return GameRegistry.register(i, new ResourceLocation(R.MCFLUX_NAME, name));
	}

	private static <T extends Block> T registerBlock(String name, T b, Function<Block, ItemBlock> ibfn) {
		ResourceLocation rs = new ResourceLocation(R.MCFLUX_NAME, name);
		b.setUnlocalizedName(name).setCreativeTab(MCFLUX_TAB);
		GameRegistry.register(b, rs);
		GameRegistry.register(ibfn.apply(b), rs);
		return b;
	}
}
