package szewek.mcflux;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public final class L {
	private static Logger l = net.minecraftforge.fml.common.FMLLog.getLogger();

	static void prepare(Logger logger) {
		l = logger;
	}

	public static void info(String msg) {
		l.log(Level.INFO, msg);
	}

	public static void error(String msg, Exception e) {
		l.log(Level.ERROR, msg, e);
	}

	public static void warn(String msg) {
		l.log(Level.WARN, msg);
	}

	public static void warn(Throwable t) {
		l.log(Level.WARN, "Minecraft-Flux received an error", t);
	}

	private L() {}
}
