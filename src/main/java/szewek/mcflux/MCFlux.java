package szewek.mcflux;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.RecipeSorter;
import szewek.mcflux.api.ex.Battery;
import szewek.mcflux.api.ex.EnergyNBTStorage;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.api.fe.FlavorNBTStorage;
import szewek.mcflux.api.fe.FlavoredStorage;
import szewek.mcflux.api.fe.IFlavorEnergy;
import szewek.mcflux.config.MCFluxConfig;
import szewek.mcflux.fluxable.InjectFluxable;
import szewek.mcflux.fluxable.PlayerEnergy;
import szewek.mcflux.fluxable.WorldChunkEnergy;
import szewek.mcflux.network.MessageHandlerDummy;
import szewek.mcflux.network.MessageHandlerServer;
import szewek.mcflux.network.UpdateMessageClient;
import szewek.mcflux.network.UpdateMessageServer;
import szewek.mcflux.util.*;
import szewek.mcflux.wrapper.InjectWrappers;

import java.util.Set;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

@SuppressWarnings("unused")
@Mod(modid = R.MF_NAME, name = R.MF_FULL_NAME, version = R.MF_VERSION, useMetadata = true, guiFactory = R.GUI_FACTORY, dependencies = R.MF_DEPENDENCIES)
public class MCFlux {
	public static SimpleNetworkWrapper SNW;
	private static final int UPDATE_CLI = 67;
	public static final int UPDATE_SRV = 69;
	private static final MessageHandlerServer MSG_SRV = new MessageHandlerServer();
	private static final MessageHandlerDummy MSG_DMM = new MessageHandlerDummy();
	static final MCFluxCreativeTab MCFLUX_TAB = new MCFluxCreativeTab();
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
		cm.register(IFlavorEnergy.class, new FlavorNBTStorage(), FlavoredStorage::new);
		cm.register(WorldChunkEnergy.class, new NBTSerializableCapabilityStorage<>(), WorldChunkEnergy::new);
		cm.register(PlayerEnergy.class, new NBTSerializableCapabilityStorage<>(), PlayerEnergy::new);
		EVENT_BUS.register(InjectWrappers.EVENTS);
		EVENT_BUS.register(MCFluxEvents.INSTANCE);
		MCFluxResources.preInit();
		MCFLUX_TAB.init();
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
		MCFluxResources.init();
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
			Class<?> c = U.getClassSafely(cname);
			if (c == null)
				continue;
			InjectRegistry ann = c.getAnnotation(InjectRegistry.class);
			if (!ann.requires().check(ann.args()))
				continue;
			try {
				IInjectRegistry iir = c.asSubclass(IInjectRegistry.class).newInstance();
				iir.registerInjects();
				cnt++;
			} catch (Exception e) {
				L.warn(e);
			}
		}
		L.info("Registered " + cnt + " inject registries");
	}
}
