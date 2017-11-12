package szewek.mcflux.fluxcompat;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import szewek.fl.FL;
import szewek.fl.energy.IEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class LazyEnergyCapProvider implements ICapabilityProvider {
	private final IEnergy[] sides = new IEnergy[7];
	private final LazyEnergy[] lazySides = new LazyEnergy[7];
	protected ICapabilityProvider lazyObject = null;
	protected boolean compatFE = false, notEnergy = false;
	private Predicate<EnumFacing> connectFunc;

	public LazyEnergyCapProvider(ICapabilityProvider lo) {
		for (int i = 0; i < 7; i++) lazySides[i] = new LazyEnergy();
		lazyObject = lo;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, @Nullable EnumFacing f) {
		if (cap == FL.ENERGY_CAP || (compatFE && cap == CapabilityEnergy.ENERGY)) {
			if (connectFunc != null) return connectFunc.test(f);
			if (notEnergy) return false;
			final int n = f == null ? 6 : f.getIndex();
			if (sides[n] == null) FluxCompat.findActiveEnergy(this);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing f) {
		final int n = f == null ? 6 : f.getIndex();
		return hasCapability(cap, f) ? (T) (sides[n] == null ? lazySides[n] : sides[n]) : null;
	}

	public ICapabilityProvider getObject() {
		return lazyObject;
	}

	public void update(IEnergy[] ies, int[] l, Predicate<EnumFacing> func, boolean fe) {
		switch (l.length) {
			case 0:
				if (ies.length < 7) return;
				for (int i = 0; i < 7; i++) {
					sides[i] = lazySides[i].ie = ies[i];
				}
				break;
			case 7:
				for (int i = 0; i < 7; i++) {
					int x = l[i];
					sides[i] = lazySides[i].ie = ies[x];
				}
				break;
			default:
		}
		if (func != null) connectFunc = func;
		compatFE = fe;
		lazyObject = null;
	}
}
