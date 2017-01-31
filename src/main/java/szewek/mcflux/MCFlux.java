package szewek.mcflux;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
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
import szewek.mcflux.network.MCFluxNetwork;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectRegistry;
import szewek.mcflux.util.MCFluxCreativeTab;
import szewek.mcflux.util.NBTSerializableCapabilityStorage;
import szewek.mcflux.util.recipe.BuiltShapedRecipe;
import szewek.mcflux.wrapper.InjectWrappers;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

@SuppressWarnings("unused")
@Mod(modid = R.MF_NAME, name = R.MF_FULL_NAME, version = R.MF_VERSION, useMetadata = true, guiFactory = R.GUI_FACTORY, dependencies = R.MF_DEPENDENCIES)
public class MCFlux {
	static String NEWER_VERSION = "";
	static boolean UPDATE_CHECK_FINISHED = false;
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
		if (MCFluxConfig.UPDATE_CHECK)
			new Thread(MCFlux::updateCheck, "MCFlux Update Check").start();
		MCFluxNetwork.registerAll();
		CapabilityManager cm = CapabilityManager.INSTANCE;
		cm.register(IEnergy.class, new EnergyNBTStorage(), Battery::new);
		cm.register(IFlavorEnergy.class, new FlavorNBTStorage(), FlavoredStorage::new);
		cm.register(WorldChunkEnergy.class, new NBTSerializableCapabilityStorage<>(), WorldChunkEnergy::new);
		cm.register(PlayerEnergy.class, new NBTSerializableCapabilityStorage<>(), PlayerEnergy::new);
		EVENT_BUS.register(MCFluxEvents.INSTANCE);
		MCFluxResources.preInit();
		MCFLUX_TAB.init();
		InjectFluxable.registerWrappers();
		PROXY.preInit();
		registerAllInjects(e.getAsmData());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		RecipeSorter.register("mcflux:builtRecipe", BuiltShapedRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
		MCFluxResources.init();
		// Waila not available
		// FMLInterModComms.sendMessage("Waila", "register", R.WAILA_REGISTER);
		PROXY.init();
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		EVENT_BUS.register(InjectWrappers.EVENTS);
	}

	private void registerAllInjects(ASMDataTable asdt) {
		L.info("Registering inject registries...");
		Set<ASMDataTable.ASMData> aset = asdt.getAll(InjectRegistry.class.getCanonicalName());
		int cnt = 0;
		for (ASMDataTable.ASMData data : aset) {
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

	private static void updateCheck() {
		URL url;
		InputStreamReader isr;
		JsonObject je;
		JsonPrimitive jp;
		UPDATE_CHECK_FINISHED = false;
		ComparableVersion ccv = new ComparableVersion(R.MF_VERSION);
		try {
			url = new URL("https", "api.github.com", 443, "/repos/Szewek/Minecraft-Flux/releases/latest", null);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			isr = new InputStreamReader(huc.getInputStream());
			je = new JsonParser().parse(isr).getAsJsonObject();
			jp = je.getAsJsonPrimitive("tag_name");
			if (jp != null) {
				String jv = jp.getAsString();
				if (new ComparableVersion(jv).compareTo(ccv) > 0) {
					NEWER_VERSION = jv;
					L.info("A newer Minecraft-Flux version is available (" + NEWER_VERSION + ")");
				}
			}
			UPDATE_CHECK_FINISHED = true;
		} catch (Throwable t) {
			L.warn(t);
		}
	}
}
