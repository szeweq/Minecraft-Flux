package szewek.mcflux.util;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import static szewek.mcflux.R.MF_NAME;

public final class MCFluxLocation extends ResourceLocation {
	private static final int MF_HASH = MF_NAME.hashCode() * 31;

	public MCFluxLocation(String name) {
		super(0, MF_NAME, name);
	}

	@Nonnull @Override
	public String getResourceDomain() {
		return MF_NAME;
	}

	@Nonnull @Override
	public String toString() {
		return MF_NAME + ':' + resourcePath;
	}

	@Override
	public int hashCode() {
		return MF_HASH + resourcePath.hashCode();
	}
}
