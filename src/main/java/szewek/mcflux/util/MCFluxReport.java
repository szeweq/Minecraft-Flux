package szewek.mcflux.util;

import com.rollbar.Rollbar;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import szewek.mcflux.L;
import szewek.mcflux.R;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public enum MCFluxReport {
	;
	private static Rollbar rollbar = new Rollbar(R.MF_ACCESS_TOKEN, R.MF_ENVIRONMENT, null, R.MF_VERSION, null, null, null, null, null, null, null, new HashMap<>(), null, null, null, null);
	private static final Int2ObjectMap<ErrMsg> errMsgs = new Int2ObjectOpenHashMap<>();
	private static final Long2ObjectMap<Timer> timers = new Long2ObjectOpenHashMap<>();
	private static final DateFormat fileDate = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	public static void init() {
		rollbar.getCustom().putAll(Collections.singletonMap("Mods", Loader.instance().getIndexedModList().keySet().toArray()));
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

	public static long measureTime(String s, @Nullable Object o) {
		long hc = ((long) (s.hashCode() ^ (o == null ? 0 : o.hashCode())) << 32) + Thread.currentThread().hashCode();
		Timer tt;
		if (timers.containsKey(hc)) {
			tt = timers.get(hc);
		} else {
			tt = new Timer(s, o);
			timers.put(hc, tt);
		}
		tt.start();
		return hc;
	}

	public static void stopTimer(long hc) {
		Timer tt = timers.get(hc);
		if (tt != null)
			tt.stop();
	}

	public static void reportAll(File dirf) throws IOException {
		File f = new File(dirf, "mcflux-" + fileDate.format(new Date()) + ".log.gz");
		PrintStream ps = new PrintStream(new GZIPOutputStream(new FileOutputStream(f)));
		ps.println("== TIMER MEASURES");
		for (Timer tt : timers.values()) {
			tt.report(rollbar);
			ps.println("! " + tt.name + " [" + tt.thName + "]; " + tt.getCount() + " × " + tt.nanoTotal + " ns (avg. " + tt.nanoAvg + " ns; min/max " + tt.nanoMin + '/' + tt.nanoMax + " ns)");
		}
		ps.println("== END OF TIMER MEASURES");
		timers.clear();
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

	public static void listAllConflictingMods() {
		String[] mods = new String[] {
				"energysynergy",
				"commoncapabilities"
		};
		Map<String, ModContainer> modmap = Loader.instance().getIndexedModList();
		List<String> sl = new ArrayList<>();
		for (String m : mods) {
			if (modmap.containsKey(m)) {
				sl.add(modmap.get(m).getName());
			}
		}
		if (!sl.isEmpty()) {
			StringBuilder sb = new StringBuilder("There are mods that can cause a conflict with Minecraft-Flux: ");
			boolean comma = false;
			for (String s : sl) {
				sb.append(s);
				if (comma)
					sb.append(", ");
				comma = true;
			}
			L.warn(sb.toString());
		}
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
}
