package szewek.mcflux;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
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
import szewek.mcflux.api.ex.Battery;
import szewek.mcflux.api.ex.EnergyNBTStorage;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.flavor.CapabilityFlavorEnergy;
import szewek.mcflux.api.flavor.FlavorEnergyContainer;
import szewek.mcflux.api.flavor.IFlavorEnergyConsumer;
import szewek.mcflux.api.flavor.IFlavorEnergyProducer;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.blocks.itemblocks.ItemBlockEnergyMachine;
import szewek.mcflux.config.MCFluxConfig;
import szewek.mcflux.fluxable.InjectFluxable;
import szewek.mcflux.fluxable.WorldChunkEnergy;
import szewek.mcflux.items.ItemMFTool;
import szewek.mcflux.tileentities.TileEntityChunkCharger;
import szewek.mcflux.tileentities.TileEntityEnergyDistributor;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectRegistry;
import szewek.mcflux.util.MCFluxLocation;
import szewek.mcflux.util.RecipeBuilder;
import szewek.mcflux.wrapper.InjectWrappers;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Mod(modid = R.MF_NAME, name = R.MF_FULL_NAME, version = R.MF_VERSION, useMetadata = true, guiFactory = R.GUI_FACTORY)
public class MCFlux {
	public static ItemMFTool MFTOOL;
	public static BlockEnergyMachine ENERGY_MACHINE;
	private static final CreativeTabs MCFLUX_TAB = new CreativeTabs(R.MF_NAME) {
		@Override
		public Item getTabIconItem() {
			return MFTOOL;
		}
	};
	@SidedProxy(modId = R.MF_NAME, serverSide = R.PROXY_SERVER, clientSide = R.PROXY_CLIENT)
	public static szewek.mcflux.proxy.ProxyCommon PROXY = null;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		MCFluxConfig.makeConfig(e.getSuggestedConfigurationFile());
		L.prepare(e.getModLog());
		if (R.MF_VERSION.charAt(0) == '@')
			L.warn("You are running Minecraft-Flux with an unknown version (development maybe?)");
		else
			L.info("Minecraft-Flux version " + R.MF_VERSION);
		CapabilityManager.INSTANCE.register(IEnergy.class, new EnergyNBTStorage(), Battery::new);
		CapabilityManager.INSTANCE.register(IEnergyProducer.class, new CapabilityEnergy.Storage<IEnergyProducer>(), EnergyBattery::new);
		CapabilityManager.INSTANCE.register(IEnergyConsumer.class, new CapabilityEnergy.Storage<IEnergyConsumer>(), EnergyBattery::new);
		CapabilityManager.INSTANCE.register(IFlavorEnergyProducer.class, new CapabilityFlavorEnergy.Storage<IFlavorEnergyProducer>(), FlavorEnergyContainer::new);
		CapabilityManager.INSTANCE.register(IFlavorEnergyConsumer.class, new CapabilityFlavorEnergy.Storage<IFlavorEnergyConsumer>(), FlavorEnergyContainer::new);
		CapabilityManager.INSTANCE.register(WorldChunkEnergy.class, new WorldChunkEnergy.ChunkStorage(), WorldChunkEnergy::new);
		EVENT_BUS.register(InjectWrappers.INSTANCE.getEventHandler());
		EVENT_BUS.register(InjectFluxable.INSTANCE);
		MFTOOL = registerItem("mftool", new ItemMFTool());
		ENERGY_MACHINE = registerBlock("energy_machine", new BlockEnergyMachine(), ItemBlockEnergyMachine::new);
		GameRegistry.registerTileEntity(TileEntityEnergyDistributor.class, "mcflux.energyDist");
		GameRegistry.registerTileEntity(TileEntityChunkCharger.class, "mcflux.chunkCharger");
		PROXY.preInit();
		registerAllInjects(e.getAsmData());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		RecipeSorter.register("mcflux:builtRecipe", RecipeBuilder.BuiltShapedRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
		ItemStack stackRedstone = new ItemStack(Items.REDSTONE);
		new RecipeBuilder()
			.result(new ItemStack(MFTOOL))
			.shapeCode(new byte[][]{{1, 0, 1}, {2, 3, 2}, {2, 2, 2}}, 3, 3)
			.oreDictWithNumber(1, "nuggetGold")
			.stackWithNumber(2, stackRedstone)
			.oreDictWithNumber(3, "ingotIron")
			.deploy();
		ItemStack stackEnergyDist = new ItemStack(ENERGY_MACHINE, 1, 0);
		new RecipeBuilder()
			.result(stackEnergyDist)
			.shapeCode(new byte[][]{{0, 1, 0}, {1, 2, 1}, {0, 1, 0}}, 3, 3)
			.oreDictWithNumber(1, "blockIron")
			.stackWithNumber(2, new ItemStack(Items.END_CRYSTAL))
			.deploy();
		new RecipeBuilder()
			.result(new ItemStack(ENERGY_MACHINE, 1, 1))
			.shapeCode(new byte[][]{{1, 0, 1}, {0, 2, 0}, {1, 0, 1}}, 3, 3)
			.stackWithNumber(1, stackRedstone)
			.stackWithNumber(2, stackEnergyDist)
			.deploy();
		FMLInterModComms.sendMessage("Waila", "register", R.WAILA_REGISTER);
		PROXY.init();
	}
	
	private void registerAllInjects(ASMDataTable asdt) {
		L.info("Registering inject registries...");
		Set<ASMData> aset = asdt.getAll(InjectRegistry.class.getCanonicalName());
		int cnt = 0;
		for (ASMData data : aset) {
			String cname = data.getClassName();
			if(!cname.equals(data.getObjectName())) continue;
			Class<?> c;
			try {
				c = Class.forName(cname);
			} catch (ClassNotFoundException e) {
				continue;
			}
			Map<String, Object> info = data.getAnnotationInfo();
			Boolean incl = (Boolean) info.get("included");
			if (incl == null || !incl.booleanValue()) {
				boolean found = false;
				@SuppressWarnings("unchecked")
				List<String> mns = (List<String>) info.get("detectMods");
				if (mns == null || mns.size() == 0) continue;
				Map<String, ModContainer> modmap = Loader.instance().getIndexedModList();
				for (String mn : mns) {
					if (modmap.containsKey(mn)) {
						found = true;
						break;
					}
				}
				if(!found)
					continue;
			}
			Class<? extends IInjectRegistry> iirc = c.asSubclass(IInjectRegistry.class);
			try {
				IInjectRegistry iir = iirc.newInstance();
				iir.registerInjects();
				cnt++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		L.info("Registered " + cnt + " inject registries");
	}

	@SideOnly(Side.CLIENT)
	public static void renders() {
		U.registerItemModels(MFTOOL);
	}

	private static <T extends Item> T registerItem(String name, T i) {
		i.setUnlocalizedName(name).setCreativeTab(MCFLUX_TAB);
		return GameRegistry.register(i, new MCFluxLocation(name));
	}

	private static <T extends Block> T registerBlock(String name, T b, Function<Block, ItemBlock> ibfn) {
		MCFluxLocation rs = new MCFluxLocation(name);
		b.setUnlocalizedName(name).setCreativeTab(MCFLUX_TAB);
		GameRegistry.register(b, rs);
		GameRegistry.register(ibfn.apply(b), rs);
		return b;
	}
}
