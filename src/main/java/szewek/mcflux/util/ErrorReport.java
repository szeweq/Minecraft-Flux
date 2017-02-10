package szewek.mcflux.util;

import szewek.mcflux.L;
import szewek.mcflux.util.error.ErrMsg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

public enum ErrorReport {
	;
	private static HashMap<Integer, ErrMsg> errMsgs = new HashMap<>();
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

	public static void saveAllErrors(File dirf) throws IOException {
		if (errMsgs.isEmpty()) {
			L.info("No errors found!");
			return;
		}
		File f = new File(dirf, "mcflux-" + fileDate.format(new Date()) + ".log.gz");
		GZIPOutputStream gz = new GZIPOutputStream(new FileOutputStream(f));
		PrintStream ps = new PrintStream(gz);
		ps.println("======== START OF ERROR MESSAGES");
		for (ErrMsg em : errMsgs.values()) {
			ps.println("+-- ErrMsg: " + em);
			ps.println(em.makeInfo());
			Set<Throwable> st = em.getThrowables();
			if (st.isEmpty())
				ps.println("| No throwables found.");
			else {
				ps.println("| Throwables: " + st.size());
				for (Throwable th : st) {
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
		ps.println("======== END OF ERROR MESSAGES");
		ps.close();
		errMsgs.clear();
	}
}
