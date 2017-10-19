package szewek.mcflux.special;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import szewek.fl.test.NamedCounters;
import szewek.mcflux.network.MCFluxNetwork;
import szewek.mcflux.util.MCFluxReport;

import javax.annotation.Nullable;
import java.util.Map;

import static szewek.mcflux.MCFlux.L;

public final class SpecialEventHandler {
	private static final long TIME = 300000;
	private static final Int2ObjectMap<SpecialEvent> events = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());
	private static final Object eventLock = new Object();
	private static EventStatus EVENT_STATUS = EventStatus.READY2DOWNLOAD;
	private static long lastUpdate = -1;
	static final NamedCounters.Counter serNBT = NamedCounters.getCounter("SER -> NBT");

	public static EventStatus getEventStatus() {
		synchronized (eventLock) {
			return EVENT_STATUS;
		}
	}

	private static void checkCount(String name, NamedCounters.Counter c) {
		if (!c.expect(1, 0))
			MCFluxReport.sendException(new Exception("Expected count minimum of 1, got " + c.getCount()), name);
	}

	public static void getEvents() {
		NamedCounters.addConsumer(serNBT, SpecialEventHandler::checkCount);
		synchronized (eventLock) {
			if (System.currentTimeMillis() / TIME > lastUpdate)
				new Thread(SpecialEventHandler::downloadEvents, "MCFlux Download Events").start();
		}
	}

	private static void downloadEvents() {
		EventStatus es = EventStatus.NOT_AVAILABLE;
		try {
			synchronized (eventLock) {
				EVENT_STATUS = EventStatus.DOWNLOADING;
			}
			JsonObject jo = MCFluxNetwork.downloadGistJSON("c5cf89068b75c66b47abefd5a9c55480", "events.json");
			JsonObject jev = jo.getAsJsonObject("events");
			if (jev != null) {
				events.clear();
				for (Map.Entry<String, JsonElement> jee : jev.entrySet()) {
					String id = jee.getKey();
					JsonElement je = jee.getValue();
					SpecialEvent se = SpecialEvent.fromJSON(je.getAsJsonObject());
					if (se != null) {
						events.put(Integer.valueOf(id).intValue(), se);
					}
				}
			}
			es = EventStatus.DOWNLOADED;
			L.info("Downloaded events: " + events.size());
		} catch (Exception e) {
			MCFluxReport.sendException(e, "Downloading events");
		}
		synchronized (eventLock) {
			EVENT_STATUS = es;
			lastUpdate = System.currentTimeMillis() / TIME;
		}
	}

	public static int getColors(ItemStack is, int tint) {
		if (!is.isEmpty()) {
			NBTTagCompound nbt = is.getTagCompound();
			if (nbt == null)
				return 0x808080;
			SpecialEvent se = events.get(nbt.getInteger("seid"));
			return se == null ? 0x404040 : tint == 0 ? se.colorBox : se.colorRibbon;
		}
		return 0x202020;
	}

	public static int[] getEventIDs() {
		return events.keySet().toIntArray();
	}

	@Nullable public static SpecialEvent getEvent(int l) {
		return events.get(l);
	}

	private SpecialEventHandler() {}

	public enum EventStatus {
		READY2DOWNLOAD, DOWNLOADING, DOWNLOADED, NOT_AVAILABLE;
	}
}
