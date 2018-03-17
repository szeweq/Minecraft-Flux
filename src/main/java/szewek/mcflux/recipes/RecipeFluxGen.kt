package szewek.mcflux.recipes

class RecipeFluxGen internal constructor(f: Int, u: Int) {
	val factor = f.toShort()
	val usage = u.toShort()

	override fun hashCode(): Int {
		return (factor.toInt() shl 16) + usage
	}

	override fun equals(other: Any?): Boolean {
		return other != null && other is RecipeFluxGen && other.hashCode() == hashCode()
	}
}
