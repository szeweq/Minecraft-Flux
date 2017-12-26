package szewek.mcflux.network;

import com.google.gson.JsonObject;
import szewek.fl.network.FLCloud;
import szewek.mcflux.R;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class CloudUtils {
	private static final FLCloud FLC = FLCloud.getAPI("mcflux", R.MF_FLC_KEY);
	private static final ExecutorService xs = Executors.newSingleThreadExecutor();
	private static final Trigger sendReports = new Trigger(false);

	public static void init() {
		xs.execute(CloudUtils::checkTriggers);
	}

	public static void executeTask(Runnable r) {
		xs.execute(r);
	}

	private static void checkTriggers() {
		try {
			JsonObject o = FLC.connect("/api/triggers").responseJSON().getAsJsonObject();
			sendReports.value = o.getAsJsonPrimitive("sendReports").getAsBoolean();
			sendReports.checked = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void reportEnergy(Class<?> c, Class<?> n, String t) {
		if (!sendReports.getValue()) return;
		JsonObject o = new JsonObject();
		o.addProperty("container", c.getName());
		if (n != null) o.addProperty("energy", n.getName());
		o.addProperty("type", t);
		xs.execute(() -> {
			try {
				FLC.connect("/api/energy").postJSON(o).responseText();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private static class Trigger {
		private final boolean defaultValue;
		private boolean value, checked;

		Trigger(boolean dv) {
			defaultValue = dv;
			value = dv;
			checked = false;
		}

		boolean getValue() {
			return checked ? value : defaultValue;
		}
	}
}
