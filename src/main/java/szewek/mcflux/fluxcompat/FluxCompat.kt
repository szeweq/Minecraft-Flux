package szewek.mcflux.fluxcompat

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.LoaderState
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import szewek.mcflux.MCFlux
import szewek.mcflux.util.ErrMsg
import szewek.mcflux.util.InjectCond
import szewek.mcflux.util.MCFluxLocation
import szewek.mcflux.util.MCFluxReport
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import javax.annotation.meta.TypeQualifierNickname

object FluxCompat {
	private val BLIST_PKG = arrayOf("szewek.", "net.minecraft.", "cofh.thermaldynamics.duct.")
	private val COMPAT = MCFluxLocation("fluxcompat")
	private val thl = java.lang.Object()
	private val th = Thread()
	private val lq = ConcurrentLinkedQueue<LazyEnergyCapProvider>()
	private var compatLookups: Array<Lookup>? = null
	private var lset: MutableSet<Lookup>? = HashSet()

	fun init() {
		if (lset != null) {
			compatLookups = lset!!.toTypedArray()
		}
		MCFlux.L!!.info("Added compat lookups: " + compatLookups!!.size)
		lset = null
		MinecraftForge.EVENT_BUS.register(FluxCompat::class.java)
		th.isDaemon = true
		th.start()
	}

	@Suppress("UNCHECKED_CAST")
	fun addAddon(a: Addon, c: Class<*>) {
		if (!(Lookup::class.java.isAssignableFrom(c) && a.requires.check(a.args))) return
		val lc = c as Class<out Lookup>
		try {
			lset!!.add(lc.newInstance())
		} catch (e: Exception) {
			MCFluxReport.sendException(e, "Adding FluxCompat addons")
		}

	}

	private fun blacklisted(o: Any): Boolean {
		val cn = o.javaClass.name
		for (s in BLIST_PKG) {
			if (cn.startsWith(s, 0))
				return true
		}
		return cn.endsWith("Cable")
	}

	internal fun findActiveEnergy(lecp: LazyEnergyCapProvider) {
		if (blacklisted(lecp.obj!!)) {
			lecp.setNotEnergy()
			return
		}
		lecp.status = LazyEnergyCapProvider.Status.ACTIVATED
		lq.offer(lecp)
		synchronized(thl) {
			thl.notify()
		}
	}

	private fun findCompat(lecp: LazyEnergyCapProvider) {
		val fs = EnumMap<EnergyType, Factory>(EnergyType::class.java)
		val reg = { et:EnergyType, f:Factory -> fs.put(et, f) } as Registry
		for (l in compatLookups!!) l.lookFor(lecp, reg)
		for (et in EnergyType.ALL) {
			val f = fs[et]
			if (f != null) {
				f.factorize(lecp)
				return
			}
		}
		lecp.setNotEnergy()
	}

	@SubscribeEvent
	fun tileCompat(ace: AttachCapabilitiesEvent<TileEntity>) {
		if (Loader.instance().hasReachedState(LoaderState.SERVER_ABOUT_TO_START)) {
			val te = ace.`object`
			if (te == null || blacklisted(te)) return
			ace.addCapability(COMPAT, LazyEnergyCapProvider(te))
		}
	}

	private class Thread internal constructor() : java.lang.Thread("FluxCompat Thread") {

		override fun run() {
			while (isAlive)
				try {
					synchronized(thl) {
						thl.wait(0)
					}
					var l: LazyEnergyCapProvider? = lq.poll()
					while (l != null) {
						if (l.obj == null && l.status !== LazyEnergyCapProvider.Status.READY)
							MCFluxReport.addErrMsg(ErrMsg.NullWrapper(true))
						else
							FluxCompat.findCompat(l)
						l = lq.poll()
					}
				} catch (e: Exception) {
					MCFluxReport.sendException(e, "FluxCompat Thread loop")
				}

		}
	}

	@FunctionalInterface
	interface Factory {
		fun factorize(lecp: LazyEnergyCapProvider)
	}

	@FunctionalInterface
	interface Lookup {
		fun lookFor(lecp: LazyEnergyCapProvider, r: Registry)
	}

	@FunctionalInterface
	interface Registry {
		fun register(et: EnergyType, f: Factory)
	}

	@FunctionalInterface
	interface Convert {
		val energyType: EnergyType
	}


	@TypeQualifierNickname
	@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
	@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
	@SuppressWarnings("UNCHECKED_CAST")
	annotation class Addon(val requires: InjectCond, val args: Array<String>)
}
