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

public final class LazyEnergyCapProvider implements ICapabilityProvider {
	private final IEnergy[] sides = new IEnergy[7];
	ICapabilityProvider lazyObject;
	Status status = Status.CREATED;
	private boolean compatFE = false;
	private Predicate<EnumFacing> connectFunc;

	LazyEnergyCapProvider(ICapabilityProvider lo) {
		for (int i = 0; i < 7; i++) sides[i] = new LazyEnergy();
		lazyObject = lo;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, @Nullable EnumFacing f) {
		if (status != Status.NOT_ENERGY && (cap == FL.ENERGY_CAP || (compatFE && cap == CapabilityEnergy.ENERGY))) {
			if (connectFunc != null) return connectFunc.test(f);
			if (status == Status.CREATED && sides[f == null ? 6 : f.getIndex()] instanceof LazyEnergy)
				FluxCompat.findActiveEnergy(this);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing f) {
		final int n = f == null ? 6 : f.getIndex();
		return hasCapability(cap, f) ? (T) sides[n] : null;
	}

	public ICapabilityProvider getObject() {
		return lazyObject;
	}

	public void update(IEnergy[] ies, int[] l, Predicate<EnumFacing> func, boolean fe) {
		switch (l.length) {
			case 0:
				if (ies.length < 7) return;
				for (int i = 0; i < 7; i++) {
					LazyEnergy le = ((LazyEnergy) sides[i]);
					if (le != null)
						le.ie = ies[i];
					sides[i] = ies[i];
				}
				break;
			case 7:
				for (int i = 0; i < 7; i++) {
					int x = l[i];
					LazyEnergy le = ((LazyEnergy) sides[i]);
					if (le != null)
						le.ie = ies[x];
					sides[i] = ies[x];
				}
				break;
			default:
		}
		if (func != null) connectFunc = func;
		compatFE = fe;
		status = Status.READY;
		lazyObject = null;
	}

	void setNotEnergy() {
		status = Status.NOT_ENERGY;
		for (int i = 0; i < 7; i++)
			sides[i] = null;
			//sides[i].notEnergy = true;
	}

	enum Status {
		CREATED, ACTIVATED, READY, NOT_ENERGY;
	}
}
