package szewek.mcflux.wrapper.projecte;

import moze_intel.projecte.api.tile.IEmcStorage;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.fe.FE;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EMCFlavorWrapper implements ICapabilityProvider {
	private final EMCSided[] sides = new EMCSided[7];

	EMCFlavorWrapper(IEmcStorage storage) {
		for (int i = 0; i < 6; i++) {
			sides[i] = new EMCSided(storage, EnumFacing.VALUES[i]);
		}
		sides[6] = new EMCSided(storage, null);
	}

	@Override public boolean hasCapability(@Nonnull Capability<?> cap, @Nullable EnumFacing f) {
		return cap == FE.CAP_FLAVOR_ENERGY;
	}

	@SuppressWarnings("unchecked")
	@Override public <T> T getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing f) {
		return cap == FE.CAP_FLAVOR_ENERGY ? (T) sides[f == null ? 6 : f.getIndex()] : null;
	}


}
