package szewek.mcflux.recipes;

public enum IX {
	A, B, C, D, E, F, G, H, I;

	public final byte ord;

	IX() {
		ord = (byte) ordinal();
	}
}
