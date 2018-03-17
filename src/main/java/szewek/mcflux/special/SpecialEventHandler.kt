package szewek.mcflux.special

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.item.ItemStack
import szewek.fl.test.NamedCounters
import szewek.mcflux.MCFlux.Companion.L
import szewek.mcflux.network.CloudUtils
import szewek.mcflux.network.MCFluxNetwork
import szewek.mcflux.util.MCFluxReport
import java.util.function.BiConsumer

object SpecialEventHandler {
	private const val TIME: Long = 300000
	private val events = Int2ObjectMaps.synchronize(Int2ObjectOpenHashMap<SpecialEvent>())
	private val eventLock = Any()
	private var EVENT_STATUS = EventStatus.READY2DOWNLOAD
	private var lastUpdate: Long = -1
	internal val serNBT: NamedCounters.Counter = NamedCounters.getCounter("SER to NBT")

	@JvmStatic
	val eventStatus: EventStatus
		get() = synchronized(eventLock) {
			return EVENT_STATUS
		}

	@JvmStatic
	val eventIDs: IntArray
		get() = events.keys.toIntArray()

	private fun checkCount(name: String, c: NamedCounters.Counter) {
		if (!c.expect(1, 0))
			MCFluxReport.sendException(Exception("Expected count minimum of 1, got " + c.count), name)
	}

	fun getEvents() {
		NamedCounters.addConsumer(serNBT, BiConsumer { name, c -> checkCount(name, c) })
		synchronized(eventLock) {
			if (System.currentTimeMillis() / TIME > lastUpdate)
				CloudUtils.executeTask { downloadEvents() }
		}
	}

	private fun downloadEvents() {
		var es = EventStatus.NOT_AVAILABLE
		try {
			synchronized(eventLock) {
				EVENT_STATUS = EventStatus.DOWNLOADING
			}
			val jo = MCFluxNetwork.downloadGistJSON("c5cf89068b75c66b47abefd5a9c55480", "events.json")
			val jev = jo.getAsJsonObject("events")
			if (jev != null) {
				events.clear()
				for ((id, je) in jev.entrySet()) {
					val se = SpecialEvent.fromJSON(je.asJsonObject)
					if (se != null) {
						events[Integer.valueOf(id).toInt()] = se
					}
				}
			}
			es = EventStatus.DOWNLOADED
			L!!.info("Downloaded events: " + events.size)
		} catch (e: Exception) {
			MCFluxReport.sendException(e, "Downloading events")
		}

		synchronized(eventLock) {
			EVENT_STATUS = es
			lastUpdate = System.currentTimeMillis() / TIME
		}
	}

	fun getColors(stk: ItemStack, tint: Int): Int {
		if (!stk.isEmpty) {
			val nbt = stk.tagCompound ?: return 0x808080
			val se = events.get(nbt.getInteger("seid"))
			return if (se == null) 0x404040 else if (tint == 0) se.colorBox else se.colorRibbon
		}
		return 0x202020
	}

	fun getEvent(l: Int): SpecialEvent? {
		return events.get(l)
	}

	enum class EventStatus {
		READY2DOWNLOAD, DOWNLOADING, DOWNLOADED, NOT_AVAILABLE
	}
}
