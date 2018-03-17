package szewek.mcflux.util

import com.rollbar.notifier.Rollbar
import com.rollbar.notifier.config.ConfigBuilder
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraftforge.common.ForgeVersion
import net.minecraftforge.fml.common.Loader
import szewek.mcflux.MCFlux.Companion.L
import szewek.mcflux.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.GZIPOutputStream

object MCFluxReport {
	private val rollbar = Rollbar(ConfigBuilder.withAccessToken(R.MF_ACCESS_TOKEN)
			.environment(R.MF_ENVIRONMENT)
			.codeVersion(R.MF_VERSION)
			.custom(::addCustomInfo)
			.platform(System.getProperty("os.name"))
			.build())
	private val errMsgs = Int2ObjectOpenHashMap<ErrMsg>()
	private val fileDate = SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")

	private fun addCustomInfo(): Map<String, Any> {
		val cm = HashMap<String, Any>()
		cm["Forge"] = ForgeVersion.getVersion()
		cm["Mods"] = Loader.instance().indexedModList.keys.toTypedArray()
		return cm
	}

	fun handleErrors() {
		rollbar.handleUncaughtErrors()
	}

	fun sendException(th: Throwable, n: String) {
		rollbar.warning(th, n + ": " + th.message)
	}

	fun addErrMsg(em: ErrMsg) {
		val hc = em.hashCode()
		em.sendInfo(rollbar)
		if (errMsgs.containsKey(hc)) {
			val xem = errMsgs.get(hc)
			xem.addThrowable(em.msgThrown)
			xem.addUp()
		} else {
			errMsgs[hc] = em
			em.addUp()
		}
	}

	@Throws(IOException::class)
	fun reportAll(dirf: File) {
		if (!errMsgs.isEmpty()) {
			val f = File(dirf, "mcflux-" + fileDate.format(Date()) + ".log.gz")
			val ps = PrintStream(GZIPOutputStream(FileOutputStream(f)))
			ps.println("== START OF ERROR MESSAGES")
			for (em in errMsgs.values) {
				ps.println("+-- ErrMsg: $em")
				ps.println(em.makeInfo())
				val lt = em.throwables
				if (lt.isEmpty())
					ps.println("| No throwables found.")
				else {
					ps.println("| Throwables: " + lt.size)
					for (th in lt) {
						if (th == null) {
							ps.println("A null throwable found.")
							continue
						}
						th.printStackTrace(ps)
						ps.println()
					}
				}
				ps.println("+--")
			}
			ps.println("== END OF ERROR MESSAGES")
			errMsgs.clear()
			ps.close()
		} else
			L!!.info("No errors found!")
	}
}
