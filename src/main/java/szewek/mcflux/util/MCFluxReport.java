package szewek.mcflux.util;

import com.rollbar.Rollbar;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;
import szewek.mcflux.L;
import szewek.mcflux.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public final class MCFluxReport {
	private static final Rollbar rollbar = new Rollbar(R.MF_ACCESS_TOKEN, R.MF_ENVIRONMENT, null, R.MF_VERSION, null, null, null, null, null, null, null, new HashMap<>(), null, null, null, null);
	private static final Int2ObjectMap<ErrMsg> errMsgs = new Int2ObjectOpenHashMap<>();
	private static final DateFormat fileDate = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	public static void init() {
		Map<String, Object> cm = rollbar.getCustom();
		cm.put("Forge", ForgeVersion.getVersion());
		cm.putAll(Collections.singletonMap("Mods", Loader.instance().getIndexedModList().keySet().toArray()));
	}

	public static void handleErrors() {
		Thread t = Thread.currentThread();
		Thread.UncaughtExceptionHandler ueh = t.getUncaughtExceptionHandler();
		t.setUncaughtExceptionHandler(new Uncaught(ueh));
	}

	public static void sendException(Throwable th, String n) {
		rollbar.warning(th, n + ": " + th.getMessage());
	}

	public static void addErrMsg(ErrMsg em) {
		int hc = em.hashCode();
		em.sendInfo(rollbar);
		if (errMsgs.containsKey(hc)) {
			ErrMsg xem = errMsgs.get(hc);
			xem.addThrowable(em.msgThrown);
			xem.addUp();
		} else {
			errMsgs.put(hc, em);
			em.addUp();
		}
	}

	public static void reportAll(File dirf) throws IOException {
		File f = new File(dirf, "mcflux-" + fileDate.format(new Date()) + ".log.gz");
		PrintStream ps = new PrintStream(new GZIPOutputStream(new FileOutputStream(f)));
		if (!errMsgs.isEmpty()) {
			ps.println("== START OF ERROR MESSAGES");
			for (ErrMsg em : errMsgs.values()) {
				ps.println("+-- ErrMsg: " + em);
				ps.println(em.makeInfo());
				List<Throwable> lt = em.getThrowables();
				if (lt.isEmpty())
					ps.println("| No throwables found.");
				else {
					ps.println("| Throwables: " + lt.size());
					for (Throwable th : lt) {
						if (th == null) {
							ps.println("A null throwable found.");
							continue;
						}
						th.printStackTrace(ps);
						ps.println();
					}
				}
				ps.println("+--");
			}
			ps.println("== END OF ERROR MESSAGES");
			errMsgs.clear();
		} else
			L.info("No errors found!");
		ps.close();
	}

	static final class Uncaught implements Thread.UncaughtExceptionHandler {
		private final Thread.UncaughtExceptionHandler ueh;

		Uncaught(Thread.UncaughtExceptionHandler ueh) {
			this.ueh = ueh;
		}

		@Override public void uncaughtException(Thread t, Throwable e) {
			rollbar.error(e, "Uncaught Exception from [" + t.getName() + "]: " + e.getMessage());
			if (ueh != null && !ueh.equals(this))
				ueh.uncaughtException(t, e);
			else
				t.getThreadGroup().uncaughtException(t, e);
		}
	}

	private MCFluxReport() {}
}
