package szewek.mcflux;

import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public enum L {
	;
	private static Logger l = FMLLog.getLogger();

	static void prepare(Logger logger) {
		l = logger;
	}

	public static void info(String msg) {
		l.log(Level.INFO, msg);
	}

	public static void warn(String msg) {
		l.log(Level.WARN, msg);
	}

	public static void warn(Throwable t) {
		l.log(Level.WARN, "Minecraft-Flux received an error", t);
	}
}
