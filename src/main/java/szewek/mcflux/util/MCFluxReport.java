package szewek.mcflux.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import szewek.mcflux.L;
import szewek.mcflux.util.error.ErrMsg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public enum MCFluxReport {
	;
	private static Int2ObjectMap<ErrMsg> errMsgs = new Int2ObjectOpenHashMap<>();
	private static Long2ObjectMap<Timer> timers = new Long2ObjectOpenHashMap<>();
	private static final DateFormat fileDate = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	public static void addErrMsg(ErrMsg em) {
		int hc = em.hashCode();
		if (errMsgs.containsKey(hc)) {
			ErrMsg xem = errMsgs.get(hc);
			xem.addThrowable(em.msgThrown);
			xem.addUp();
		} else {
			errMsgs.put(hc, em);
			em.addUp();
		}
	}

	public static long measureTime(String s) {
		long hc = ((long) s.hashCode() << 32) + Thread.currentThread().hashCode();
		Timer tt;
		if (timers.containsKey(hc)) {
			tt = timers.get(hc);
		} else {
			tt = new Timer(s);
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

	public static void makeReportFile(File dirf) throws IOException {
		File f = new File(dirf, "mcflux-" + fileDate.format(new Date()) + ".log.gz");
		PrintStream ps = new PrintStream(new GZIPOutputStream(new FileOutputStream(f)));
		ps.println("== TIMER MEASURES ==");
		for (Timer tt : timers.values()) {
			ps.println("+-- Timer: " + tt.name + " [" + tt.thName + "]");
			long lmin, lmax, ltot = 0;
			double lavg;
			long[] l = tt.getMeasures();
			ps.println("| Count: " + l.length);
			lmin = lmax = l[0];
			for (int i = 0; i < l.length; i++) {
				if (i > 0) {
					if (l[i] < lmin)
						lmin = l[i];
					if (l[i] > lmax)
						lmax = l[i];
				}
				ltot += l[i];
			}
			lavg = (double) ltot / l.length;
			ps.println("| Total: " + ltot + " ns\n| Average: " + lavg + " ns\n| Min: " + lmin + " ns\n| Max: " + lmax + " ns");
			ps.println("+--");
		}
		ps.println("== END OF TIMER MEASURES ==");
		timers.clear();
		if (!errMsgs.isEmpty()) {
			ps.println("== START OF ERROR MESSAGES ==");
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
			ps.println("== END OF ERROR MESSAGES ==");
			errMsgs.clear();
		} else {
			L.info("No errors found!");
		}
		ps.close();
	}
}
