package szewek.mcflux.util

import szewek.fl.FLU

enum class InjectCond {
	NONE, MOD, CLASS;

	fun check(args: Array<String>): Boolean {
		when (this) {
			NONE -> return true
			MOD -> return args.isNotEmpty() && FLU.loadedMods(*args)
			CLASS -> for (a in args) {
				try {
					if (Class.forName(a) != null)
						return true
				} catch (ignored: ClassNotFoundException) {
				}

			}
		}
		return false
	}
}
