package szewek.mcflux.recipes;

public final class RecipeFluxGen {
	public final short factor, usage;
	private final int cachedHash;

	RecipeFluxGen(int f, int u) {
		factor = (short) f;
		usage = (short) u;
		cachedHash = ((int) factor << 16) + usage;
	}

	@Override public int hashCode() {
		return cachedHash;
	}

	@Override public boolean equals(Object obj) {
		return obj != null && obj instanceof RecipeFluxGen && obj.hashCode() == cachedHash;
	}
}
