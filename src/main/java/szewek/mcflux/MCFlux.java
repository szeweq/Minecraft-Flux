package szewek.mcflux;

import com.google.gson.JsonObject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import szewek.mcflux.api.ex.Battery;
import szewek.mcflux.api.ex.EnergyNBTStorage;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.api.fe.FlavorNBTStorage;
import szewek.mcflux.api.fe.FlavoredStorage;
import szewek.mcflux.api.fe.IFlavorEnergy;
import szewek.mcflux.config.MCFluxConfig;
import szewek.mcflux.fluxable.PlayerEnergy;
import szewek.mcflux.fluxable.WorldChunkEnergy;
import szewek.mcflux.gui.MCFluxGuiHandler;
import szewek.mcflux.network.MCFluxNetwork;
import szewek.mcflux.special.CommandSpecialGive;
import szewek.mcflux.special.SpecialEventHandler;
import szewek.mcflux.special.SpecialEventReceiver;
import szewek.mcflux.util.*;
import szewek.mcflux.wrapper.InjectWrappers;

import java.io.File;
import java.util.Set;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

@SuppressWarnings("unused")
@Mod(modid = R.MF_NAME, name = R.MF_FULL_NAME, version = R.MF_VERSION, useMetadata = true, guiFactory = R.GUI_FACTORY, dependencies = R.MF_DEPS)
public final class MCFlux {
	private static File MC_DIR;
	static String NEWER_VERSION = "";
	static boolean UPDATE_CHECK_FINISHED = false;
	static final MCFluxCreativeTab MCFLUX_TAB = new MCFluxCreativeTab();
	@SidedProxy(modId = R.MF_NAME, serverSide = R.PROXY_SERVER, clientSide = R.PROXY_CLIENT)
	public static szewek.mcflux.proxy.ProxyCommon PROXY = null;

	@Mod.Instance
	public static MCFlux MF = null;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		MCFluxReport.init();
		MCFluxReport.handleErrors();
		L.prepare(e.getModLog());
		MC_DIR = e.getModConfigurationDirectory().getParentFile();
		MCFluxConfig.makeConfig(e.getSuggestedConfigurationFile());
		if (R.MF_VERSION.charAt(0) == '@')
			L.warn("You are running Minecraft-Flux with an unknown version (development maybe?)");
		else
			L.info("Minecraft-Flux " + R.MF_VERSION);
		if (MCFluxConfig.UPDATE_CHECK)
			new Thread(MCFlux::updateCheck, "MCFlux Update Check").start();
		SpecialEventHandler.getEvents();
		MCFluxNetwork.registerAll();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new MCFluxGuiHandler());
		CapabilityManager cm = CapabilityManager.INSTANCE;
		cm.register(IEnergy.class, new EnergyNBTStorage(), Battery::new);
		cm.register(IFlavorEnergy.class, new FlavorNBTStorage(), FlavoredStorage::new);
		cm.register(WorldChunkEnergy.class, CapStorage.getNBTStorage(), WorldChunkEnergy::new);
		cm.register(PlayerEnergy.class, CapStorage.getNBTStorage(), PlayerEnergy::new);
		cm.register(SpecialEventReceiver.class, CapStorage.getNBTStorage(), SpecialEventReceiver::new);
		MCFluxResources.preInit();
		PROXY.preInit();
		MCFluxReport.listAllConflictingMods();
		registerAllInjects(e.getAsmData());
		InjectWrappers.init();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		MCFluxResources.init();
		// Waila not available // FMLInterModComms.sendMessage("Waila", "register", R.WAILA_REGISTER);
		PROXY.init();
	}

	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent e) {
		EVENT_BUS.register(InjectWrappers.EVENTS);
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent e) {
		e.registerServerCommand(new CommandSpecialGive());
	}

	@Mod.EventHandler
	public void serverStopped(FMLServerStoppedEvent e) {
		if (MC_DIR == null) {
			L.warn("Can't save error messages!");
			return;
		}
		try {
			MCFluxReport.reportAll(MC_DIR);
		} catch (Exception x) {
			MCFluxReport.sendException(x, "Creating a report");
		}
	}

	private void registerAllInjects(ASMDataTable asdt) {
		L.info("Registering inject registries...");
		final Set<ASMDataTable.ASMData> aset = asdt.getAll(InjectRegistry.class.getCanonicalName());
		int cnt = 0;
		for (ASMDataTable.ASMData data : aset) {
			final String cname = data.getClassName();
			if (!cname.equals(data.getObjectName())) continue;
			final Class<?> c = U.getClassSafely(cname);
			if (c == null)
				continue;
			final InjectRegistry ann = c.getAnnotation(InjectRegistry.class);
			if (!ann.requires().check(ann.args()))
				continue;
			try {
				final IInjectRegistry iir = c.asSubclass(IInjectRegistry.class).newInstance();
				iir.registerInjects();
				cnt++;
			} catch (Exception e) {
				MCFluxReport.sendException(e, "Registering Injects");
			}
		}
	}

	private static void updateCheck() {
		final ComparableVersion ccv = new ComparableVersion(R.MF_VERSION);
		try {
			final JsonObject je = MCFluxNetwork.downloadGistJSON("97a48d6a61b29171938abf2f6bf9f985", "versions.json");
			final String v = je.getAsJsonObject("mc").getAsJsonPrimitive(Loader.MC_VERSION).getAsString();
			if (new ComparableVersion(v).compareTo(ccv) > 0) {
				L.info("A newer Minecraft-Flux version is available (" + v + ")");
				NEWER_VERSION = v;
			}
			UPDATE_CHECK_FINISHED = true;
		} catch (Throwable t) {
			MCFluxReport.sendException(t, "Update Check");
		}
	}
}
