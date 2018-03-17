package szewek.mcflux.network

import com.google.gson.JsonObject
import szewek.fl.network.FLCloud
import szewek.mcflux.R

import java.io.IOException
import java.util.concurrent.Executors

object CloudUtils {
	private val FLC = FLCloud.getAPI("mcflux", R.MF_FLC_KEY)
	private val xs = Executors.newSingleThreadExecutor()
	private val sendReports = Trigger(false)

	fun init() {
		xs.execute { checkTriggers() }
	}

	fun executeTask(r: () -> Unit) {
		xs.execute(r)
	}

	private fun checkTriggers() {
		try {
			val o = FLC.connect("/api/triggers")!!.responseJSON().asJsonObject
			sendReports.currentValue = o.getAsJsonPrimitive("sendReports").asBoolean
			sendReports.checked = true
		} catch (e: Exception) {
			e.printStackTrace()
		}

	}

	fun reportEnergy(c: Class<*>, n: Class<*>?, t: String) {
		if (!sendReports.value) return
		val o = JsonObject()
		o.addProperty("container", c.name)
		if (n != null) o.addProperty("energy", n.name)
		o.addProperty("type", t)
		xs.execute {
			try {
				FLC.connect("/api/energy")!!.postJSON(o).responseText()
			} catch (e: IOException) {
				e.printStackTrace()
			}
		}
	}

	private class Trigger internal constructor(private val defaultValue: Boolean) {
		internal var currentValue: Boolean = defaultValue
		internal var checked: Boolean = false

		internal val value: Boolean
			get() = if (checked) currentValue else defaultValue
	}
}
