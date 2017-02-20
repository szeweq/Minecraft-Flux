package szewek.mcflux.special;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import szewek.mcflux.L;
import szewek.mcflux.network.MCFluxNetUtil;
import szewek.mcflux.util.MCFluxReport;
import szewek.mcflux.util.error.ErrMsgThrownException;

import java.util.Map;

public enum SpecialEventHandler {
	;
	private static final long TIME = 300000;
	private static final Long2ObjectMap<SpecialEvent> events = Long2ObjectMaps.synchronize(new Long2ObjectArrayMap<>());
	private static final Object eventLock = new Object();
	private static EventStatus EVENT_STATUS = EventStatus.READY2DOWNLOAD;
	private static long lastUpdate = -1;

	public static EventStatus getEventStatus() {
		synchronized (eventLock) {
			return EVENT_STATUS;
		}
	}

	public static void getEvents() {
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
			JsonObject jo = MCFluxNetUtil.downloadGistJSON("c5cf89068b75c66b47abefd5a9c55480", "events.json");
			JsonObject jev = jo.getAsJsonObject("events");
			if (jev != null) {
				events.clear();
				for (Map.Entry<String, JsonElement> jee : jev.entrySet()) {
					String id = jee.getKey();
					JsonElement je = jee.getValue();
					SpecialEvent se = SpecialEvent.fromJSON(je.getAsJsonObject());
					if (se != null) {
						events.put(Long.valueOf(id).longValue(), se);
					}
				}
			}
			es = EventStatus.DOWNLOADED;
			L.info("Downloaded events: " + events.size());
		} catch (Exception e) {
			MCFluxReport.addErrMsg(new ErrMsgThrownException(e));
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
			SpecialEvent se = events.get(nbt.getLong("seid"));
			return se == null ? 0x404040 : tint == 0 ? se.colorBox : se.colorRibbon;
		}
		return 0x202020;
	}

	public static long[] getEventIDs() {
		return events.keySet().toLongArray();
	}

	public static SpecialEvent getEvent(long l) {
		return events.get(l);
	}

	public enum EventStatus {
		READY2DOWNLOAD, DOWNLOADING, DOWNLOADED, NOT_AVAILABLE;
	}
}
