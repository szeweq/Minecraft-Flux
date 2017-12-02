package szewek.mcflux.util;

import szewek.fl.FLU;

public enum InjectCond {
	NONE, MOD, CLASS;

	public boolean check(String[] args) {
		switch(this) {
			case NONE:
				return true;
			case MOD:
				return args.length != 0 && FLU.loadedMods(args);
			case CLASS:
				for (String a : args) {
					try {
						if (Class.forName(a) != null)
							return true;
					} catch (ClassNotFoundException ignored) {
					}
				}
				break;
		}
		return false;
	}
}
