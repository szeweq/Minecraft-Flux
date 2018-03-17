package szewek.mcflux.util

import net.minecraft.util.ResourceLocation

import szewek.mcflux.R.MF_NAME

class MCFluxLocation(name: String) : ResourceLocation(0, MF_NAME, name) {

	override fun getResourceDomain(): String {
		return MF_NAME
	}

	override fun toString(): String {
		return MF_NAME + ':'.toString() + resourcePath
	}

	override fun hashCode(): Int {
		return MF_HASH + resourcePath.hashCode()
	}

	override fun equals(other: Any?): Boolean {
		if (other !is ResourceLocation)
			return false
		return other.resourceDomain == MF_NAME && other.resourcePath == resourcePath
	}

	companion object {
		private val MF_HASH = MF_NAME.hashCode() * 31
	}
}
