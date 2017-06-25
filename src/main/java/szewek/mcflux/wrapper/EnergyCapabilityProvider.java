package szewek.mcflux.wrapper;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import szewek.fl.FL;
import szewek.fl.energy.IEnergy;
import szewek.mcflux.U;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class EnergyCapabilityProvider implements ICapabilityProvider {
	protected boolean broken, forgeCompatible = false;
	protected final IEnergy[] sides = new IEnergy[7];
	private final Predicate<EnumFacing> connectFunc;

	protected EnergyCapabilityProvider() {
		this(null, null, null);
	}

	public EnergyCapabilityProvider(ICapabilityProvider icp, BiFunction<ICapabilityProvider, EnumFacing, IEnergy> newside, Predicate<EnumFacing> cf) {
		broken = false;
		if (icp != null && newside != null) {
			for (int i = 0; i < U.FANCY_FACING.length; i++)
				sides[i] = newside.apply(icp, U.FANCY_FACING[i]);
			if (sides[6] != null && sides[6] instanceof net.minecraftforge.energy.IEnergyStorage)
				forgeCompatible = true;
		}
		connectFunc = cf;
	}

	protected boolean canConnect(EnumFacing f) {
		if (connectFunc != null)
			return connectFunc.test(f);
		int n = f == null ? 6 : f.getIndex();
		IEnergy ie = sides[n];
		return ie.canOutputEnergy() || ie.canInputEnergy();
	}

	@Override public boolean hasCapability(@Nonnull Capability<?> cap, @Nullable EnumFacing f) {
		return (cap == FL.ENERGY_CAP || (forgeCompatible && cap == CapabilityEnergy.ENERGY)) && !broken && canConnect(f);
	}

	@SuppressWarnings("unchecked")
	@Override public <T> T getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing f) {
		return hasCapability(cap, f) ? (T) sides[f == null ? 6 : f.getIndex()] : null;
	}
}
