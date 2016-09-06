package szewek.mcflux;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class L {
	private static boolean ready = false;
	private static Logger l;
	
	private L() {}
	
	static void prepare(Logger logger) {
		l = logger;
		ready = true;
	}
	
	public static void info(String msg) {
		if (!ready) return;
		l.log(Level.INFO, msg);
	}

	public static void warn(String msg) {
		if (!ready) return;
		l.log(Level.WARN, msg);
	}
	public static void warn(Throwable t) {
		if (!ready) {
			t.printStackTrace();
			return;
		}
		l.warn("Minecraft-Flux received an error", t);
	}
}
