package szewek.mcflux.util;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.Map;

public enum InjectCond {
	NONE, MOD, CLASS;

	public boolean check(String[] args) {
		switch(this) {
			case NONE:
				return true;
			case MOD:
				if (args.length == 0)
					return false;
				Map<String, ModContainer> modmap = Loader.instance().getIndexedModList();
				for (String a : args) {
					if (modmap.containsKey(a)) {
						return true;
					}
				}
				break;
			case CLASS:
				for (String a : args) {
					Class<?> cl;
					try {
						cl = Class.forName(a);
						if (cl != null)
							return true;
					} catch (ClassNotFoundException ignored) {}
				}
				break;
		}
		return false;
	}
}
