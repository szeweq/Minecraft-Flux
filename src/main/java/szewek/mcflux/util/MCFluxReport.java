package szewek.mcflux.util;

import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.ConfigBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;
import szewek.mcflux.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import static szewek.mcflux.MCFlux.L;

public final class MCFluxReport {
	private static final Rollbar rollbar = new Rollbar(ConfigBuilder.withAccessToken(R.MF_ACCESS_TOKEN)
			.environment(R.MF_ENVIRONMENT)
			.codeVersion(R.MF_VERSION)
			.custom(MCFluxReport::addCustomInfo)
			.platform(System.getProperty("os.name"))
			.build());
	private static final Int2ObjectMap<ErrMsg> errMsgs = new Int2ObjectOpenHashMap<>();
	private static final DateFormat fileDate = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	private static Map<String, Object> addCustomInfo() {
		Map<String, Object> cm = new HashMap<>();
		cm.put("Forge", ForgeVersion.getVersion());
		cm.put("Mods", Loader.instance().getIndexedModList().keySet().toArray());
		return cm;
	}

	public static void handleErrors() {
		rollbar.handleUncaughtErrors();
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
		if (!errMsgs.isEmpty()) {
			File f = new File(dirf, "mcflux-" + fileDate.format(new Date()) + ".log.gz");
			PrintStream ps = new PrintStream(new GZIPOutputStream(new FileOutputStream(f)));
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
			ps.close();
		} else
			L.info("No errors found!");
	}

	private MCFluxReport() {}
}
