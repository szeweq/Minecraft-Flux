package szewek.mcflux

import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.*
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.versioning.ComparableVersion
import org.apache.logging.log4j.Logger
import szewek.fl.util.CapStorage
import szewek.fl.util.JavaUtils
import szewek.mcflux.compat.top.TOPInit
import szewek.mcflux.config.MCFluxConfig
import szewek.mcflux.fluxable.PlayerEnergy
import szewek.mcflux.fluxable.WorldChunkEnergy
import szewek.mcflux.fluxcompat.FluxCompat
import szewek.mcflux.gui.MCFluxGuiHandler
import szewek.mcflux.network.CloudUtils
import szewek.mcflux.network.MCFluxNetwork
import szewek.mcflux.special.CommandSpecialGive
import szewek.mcflux.special.SpecialEventHandler
import szewek.mcflux.special.SpecialEventReceiver
import szewek.mcflux.util.MCFluxCreativeTab
import szewek.mcflux.util.MCFluxReport
import java.io.File
import java.util.function.BiConsumer

@Mod(modid = R.MF_NAME, name = R.MF_FULL_NAME, version = R.MF_VERSION, useMetadata = true, guiFactory = R.GUI_FACTORY, dependencies = R.MF_DEPS)
class MCFlux {

	@Mod.EventHandler
	fun preInit(e: FMLPreInitializationEvent) {
		MCFluxReport.handleErrors()
		L = e.modLog
		MC_DIR = e.modConfigurationDirectory.parentFile
		MCFluxConfig.makeConfig(e.suggestedConfigurationFile)
		if (R.MF_VERSION[0] == '$')
			L!!.warn("You are running Minecraft-Flux with an unknown version (development maybe?)")
		if (MCFluxConfig.UPDATE_CHECK)
			CloudUtils.executeTask(::updateCheck)
		SpecialEventHandler.getEvents()
		MCFluxNetwork.registerAll()
		NetworkRegistry.INSTANCE.registerGuiHandler(this, MCFluxGuiHandler())
		val cm = CapabilityManager.INSTANCE
		cm.register<WorldChunkEnergy>(WorldChunkEnergy::class.java, CapStorage.getNBTStorage(), ::WorldChunkEnergy)
		cm.register<PlayerEnergy>(PlayerEnergy::class.java, CapStorage.getNBTStorage(), ::PlayerEnergy)
		cm.register<SpecialEventReceiver>(SpecialEventReceiver::class.java, CapStorage.getNBTStorage(), ::SpecialEventReceiver)
		MCFlux.Resources.preInit()
		PROXY!!.preInit()
		JavaUtils.eachAnnotatedClasses(e.asmData, FluxCompat.Addon::class.java, BiConsumer<FluxCompat.Addon, Class<*>> { a, c -> FluxCompat.addAddon(a, c) })
		FluxCompat.init()
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", TOPInit::class.java.name)
	}

	@Mod.EventHandler
	fun init(e: FMLInitializationEvent) {
		MCFlux.Resources.init()
		PROXY!!.init()
	}

	@Mod.EventHandler
	fun serverStarting(e: FMLServerStartingEvent) {
		e.registerServerCommand(CommandSpecialGive())
	}

	@Mod.EventHandler
	fun serverStopped(e: FMLServerStoppedEvent) {
		if (MC_DIR == null) {
			L!!.warn("Can't save error messages!")
			return
		}
		try {
			MCFluxReport.reportAll(MC_DIR!!)
		} catch (x: Exception) {
			MCFluxReport.sendException(x, "Creating a report")
		}

	}

	companion object {
		private var MC_DIR: File? = null
		@JvmField
		internal var NEWER_VERSION = ""
		@JvmField
		internal var UPDATE_CHECK_FINISHED = false
		internal val MCFLUX_TAB = MCFluxCreativeTab()

		@JvmField
		var L: Logger? = null

		@SidedProxy(modId = R.MF_NAME, serverSide = R.PROXY_SERVER, clientSide = R.PROXY_CLIENT)
		var PROXY: szewek.mcflux.proxy.ProxyCommon? = null

		@Mod.Instance
		var MF: MCFlux? = null

		@JvmField
		var Resources: MCFluxResources = MCFluxResources()

		private fun updateCheck() {
			val ccv = ComparableVersion(R.MF_VERSION)
			try {
				val je = MCFluxNetwork.downloadGistJSON("97a48d6a61b29171938abf2f6bf9f985", "versions.json")
				val v = je.getAsJsonObject("mc").getAsJsonPrimitive(Loader.MC_VERSION).asString
				if (ComparableVersion(v) > ccv) {
					L!!.info("A newer Minecraft-Flux version is available ($v)")
					NEWER_VERSION = v
				}
				UPDATE_CHECK_FINISHED = true
			} catch (t: Throwable) {
				MCFluxReport.sendException(t, "Update Check")
			}

		}
	}
}
