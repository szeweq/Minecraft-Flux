package szewek.mcflux.recipes;

public final class RecipeFluxGen {
	public final short factor, usage;

	RecipeFluxGen(int f, int u) {
		factor = (short) f;
		usage = (short) u;
	}

	@Override public int hashCode() {
		return ((int) factor << 16) + usage;
	}

	@Override public boolean equals(Object obj) {
		return obj != null && obj instanceof RecipeFluxGen && obj.hashCode() == hashCode();
	}
}
