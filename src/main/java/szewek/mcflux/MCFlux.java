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
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.RecipeSorter;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.EnergyBattery;
import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.IEnergyProducer;
import szewek.mcflux.api.ex.Battery;
import szewek.mcflux.api.ex.EnergyNBTStorage;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.api.flavor.CapabilityFlavorEnergy;
import szewek.mcflux.api.flavor.FlavorEnergyContainer;
import szewek.mcflux.api.flavor.IFlavorEnergyConsumer;
import szewek.mcflux.api.flavor.IFlavorEnergyProducer;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.blocks.itemblocks.ItemBlockEnergyMachine;
import szewek.mcflux.config.MCFluxConfig;
import szewek.mcflux.fluxable.InjectFluxable;
import szewek.mcflux.fluxable.PlayerEnergy;
import szewek.mcflux.fluxable.WorldChunkEnergy;
import szewek.mcflux.items.ItemMFTool;
import szewek.mcflux.items.ItemUpChip;
import szewek.mcflux.network.MessageHandlerDummy;
import szewek.mcflux.network.MessageHandlerServer;
import szewek.mcflux.network.UpdateMessageClient;
import szewek.mcflux.network.UpdateMessageServer;
import szewek.mcflux.tileentities.TileEntityChunkCharger;
import szewek.mcflux.tileentities.TileEntityEnergyDistributor;
import szewek.mcflux.util.*;
import szewek.mcflux.wrapper.InjectWrappers;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

@SuppressWarnings("unused")
@Mod(modid = R.MF_NAME, name = R.MF_FULL_NAME, version = R.MF_VERSION, useMetadata = true, guiFactory = R.GUI_FACTORY)
public class MCFlux {
	public static SimpleNetworkWrapper SNW;
	public static ItemMFTool MFTOOL;
	public static ItemUpChip UPCHIP;
	public static BlockEnergyMachine ENERGY_MACHINE;
	public static final int UPDATE_CLI = 67, UPDATE_SRV = 69;
	private static final MessageHandlerServer MSG_SRV = new MessageHandlerServer();
	private static final MessageHandlerDummy MSG_DMM = new MessageHandlerDummy();
	private static final CreativeTabs MCFLUX_TAB = new CreativeTabs(R.MF_NAME) {
		@Override
		public Item getTabIconItem() {
			return MFTOOL;
		}
	};
	@SidedProxy(modId = R.MF_NAME, serverSide = R.PROXY_SERVER, clientSide = R.PROXY_CLIENT)
	static szewek.mcflux.proxy.ProxyCommon PROXY = null;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		L.prepare(e.getModLog());
		MCFluxConfig.makeConfig(e.getSuggestedConfigurationFile());
		if (R.MF_VERSION.charAt(0) == '@')
			L.warn("You are running Minecraft-Flux with an unknown version (development maybe?)");
		else
			L.info("Minecraft-Flux version " + R.MF_VERSION);
		CapabilityManager cm = CapabilityManager.INSTANCE;
		cm.register(IEnergy.class, new EnergyNBTStorage(), Battery::new);
		cm.register(IEnergyProducer.class, new CapabilityEnergy.Storage<>(), EnergyBattery::new);
		cm.register(IEnergyConsumer.class, new CapabilityEnergy.Storage<>(), EnergyBattery::new);
		cm.register(IFlavorEnergyProducer.class, new CapabilityFlavorEnergy.Storage<>(), FlavorEnergyContainer::new);
		cm.register(IFlavorEnergyConsumer.class, new CapabilityFlavorEnergy.Storage<>(), FlavorEnergyContainer::new);
		cm.register(WorldChunkEnergy.class, new NBTSerializableCapabilityStorage<>(), WorldChunkEnergy::new);
		cm.register(PlayerEnergy.class, new NBTSerializableCapabilityStorage<>(), PlayerEnergy::new);
		EVENT_BUS.register(InjectWrappers.events);
		EVENT_BUS.register(MCFluxEvents.INSTANCE);
		MFTOOL = registerItem("mftool", new ItemMFTool());
		UPCHIP = registerItem("upchip", new ItemUpChip());
		ENERGY_MACHINE = registerBlock("energy_machine", new BlockEnergyMachine(), ItemBlockEnergyMachine::new);
		GameRegistry.registerTileEntity(TileEntityEnergyDistributor.class, "mcflux.energyDist");
		GameRegistry.registerTileEntity(TileEntityChunkCharger.class, "mcflux.chunkCharger");
		SNW = NetworkRegistry.INSTANCE.newSimpleChannel(R.MF_NAME);
		SNW.registerMessage(MSG_SRV, UpdateMessageClient.class, UPDATE_CLI, Side.SERVER);
		SNW.registerMessage(MSG_DMM, UpdateMessageServer.class, UPDATE_SRV, Side.SERVER);
		InjectFluxable.registerWrappers();
		PROXY.preInit();
		registerAllInjects(e.getAsmData());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		RecipeSorter.register("mcflux:builtRecipe", RecipeBuilder.BuiltShapedRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
		ItemStack stackRedstone = new ItemStack(Items.REDSTONE);
		ItemStack stackEnergyDist = new ItemStack(ENERGY_MACHINE, 1, 0);
		new RecipeBuilder(MFTOOL)
				.withShape(new IX[][]{{IX.A, null, IX.A}, {IX.B, IX.C, IX.B}, {IX.B, IX.B, IX.B}}, 3, 3)
				.withOreDict(IX.A, "nuggetGold")
				.withStack(IX.B, stackRedstone)
				.withOreDict(IX.C, "ingotIron")
				.deploy();
		new RecipeBuilder(ENERGY_MACHINE)
				.withShape(new IX[][]{{null, IX.A, null}, {IX.A, IX.B, IX.A}, {null, IX.A, null}}, 3, 3)
				.withOreDict(IX.A, "blockIron")
				.withStack(IX.B, new ItemStack(Items.END_CRYSTAL))
				.deploy()
				.resultMeta(1)
				.clear(IX.A, IX.B)
				.withShape(new IX[][]{{IX.A, null, IX.A}, {null, IX.B, null}, {IX.A, null, IX.A}}, 3, 3)
				.withStack(IX.A, stackRedstone)
				.withStack(IX.B, stackEnergyDist)
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
			if (!cname.equals(data.getObjectName())) continue;
			Class<? extends IInjectRegistry> c;
			try {
				c = Class.forName(cname).asSubclass(IInjectRegistry.class);
			} catch (ClassNotFoundException e) {
				continue;
			}
			InjectRegistry ann = c.getAnnotation(InjectRegistry.class);
			boolean incl = ann.included();
			if (!incl) {
				boolean found = false;
				@SuppressWarnings("unchecked")
				String[] mns = ann.detectMods();
				if (mns == null || mns.length == 0) continue;
				Map<String, ModContainer> modmap = Loader.instance().getIndexedModList();
				for (String mn : mns) {
					if (modmap.containsKey(mn)) {
						found = true;
						break;
					}
				}
				if (!found)
					continue;
			}
			try {
				IInjectRegistry iir = c.newInstance();
				iir.registerInjects();
				cnt++;
			} catch (Exception e) {
				L.warn(e);
			}
		}
		L.info("Registered " + cnt + " inject registries");
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
