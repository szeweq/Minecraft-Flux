package szewek.mcflux.wrapper;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import szewek.mcflux.util.MCFluxLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MCFluxWrapper implements ICapabilityProvider, INBTSerializable<NBTBase> {
	public static final MCFluxLocation MCFLUX_WRAPPER = new MCFluxLocation("wrapper");
	final Object mainObject;
	private ICapabilityProvider[] providers = null;
	private INBTSerializable<NBTBase>[] serializables = null;
	private String[] names = null;

	MCFluxWrapper(Object o) {
		mainObject = o;
	}

	@Override public boolean hasCapability(@Nonnull Capability<?> cap, @Nullable EnumFacing f) {
		if (providers == null)
			return false;
		for (ICapabilityProvider icp : providers) {
			if (icp.hasCapability(cap, f))
				return true;
		}
		return false;
	}

	@Nullable @Override public <T> T getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing f) {
		if (providers == null)
			return null;
		T t;
		for (ICapabilityProvider icp : providers) {
			t = icp.getCapability(cap, f);
			if (t != null)
				return t;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public boolean addWrappers(Map<String, ICapabilityProvider> icps) {
		int size = icps.size();
		if (size == 0) {
			return false;
		}
		List<ICapabilityProvider> pl = new ArrayList<>(size);
		List<String> n = new ArrayList<>(size);
		List<INBTSerializable<NBTBase>> sl = new ArrayList<>(size);
		int i = 0;
		for (Map.Entry<String, ICapabilityProvider> e : icps.entrySet()) {
			ICapabilityProvider icp = e.getValue();
			n.add(i, e.getKey());
			pl.add(i, icp);
			if (icp instanceof INBTSerializable)
				sl.add(i, (INBTSerializable<NBTBase>) icp);
			else
				sl.add(i, null);
			i++;
		}
		providers = pl.toArray(new ICapabilityProvider[size]);
		names = n.toArray(new String[size]);
		serializables = sl.toArray(new INBTSerializable[size]);
		return providers.length > 0;
	}

	@Override public NBTBase serializeNBT() {
		NBTTagCompound nbttc = new NBTTagCompound();
		if (names != null && serializables != null)
			for (int i = 0; i < names.length; i++) {
				INBTSerializable<NBTBase> nbts = serializables[i];
				if (nbts == null)
					continue;
				nbttc.setTag(names[i], nbts.serializeNBT());
			}
		return nbttc;
	}

	@Override public void deserializeNBT(NBTBase nbt) {
		if (nbt != null && nbt instanceof NBTTagCompound && names != null && serializables != null) {
			NBTTagCompound nbttc = (NBTTagCompound) nbt;
			for (int i = 0; i < names.length; i++) {
				INBTSerializable<NBTBase> nbts = serializables[i];
				if (nbts == null)
					continue;
				if (nbttc.hasKey(names[i]))
					nbts.deserializeNBT(nbttc.getTag(names[i]));
			}
		}
	}
}
