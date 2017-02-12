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
	static final MCFluxLocation MCFLUX_WRAPPER = new MCFluxLocation("wrapper");
	Object mainObject = null;
	private boolean checked = false;
	private ICapabilityProvider[] providers = new ICapabilityProvider[0];
	private String[] names = null;
	private INBTSerializable<NBTBase>[] writers = null;
	private NBTBase cachedNBT = null;

	MCFluxWrapper(Object o) {
		mainObject = o;
	}

	@Override public boolean hasCapability(@Nonnull Capability<?> cap, @Nullable EnumFacing f) {
		for (ICapabilityProvider icp : providers) {
			if (icp.hasCapability(cap, f))
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Nullable @Override public <T> T getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing f) {
		for (ICapabilityProvider icp : providers) {
			T t = icp.getCapability(cap, f);
			if (t != null)
				return t;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	boolean addWrappers(Map<String, ICapabilityProvider> icps) {
		int size = icps.size();
		if (size > 0) {
			List<ICapabilityProvider> pl = new ArrayList<>(size);
			List<INBTSerializable<NBTBase>> nb = new ArrayList<>(size);
			List<String> ns = new ArrayList<>(size);
			for (Map.Entry<String, ICapabilityProvider> e : icps.entrySet()) {
				String s = e.getKey().toLowerCase();
				ICapabilityProvider icp = e.getValue();
				if (icp instanceof INBTSerializable) {
					nb.add((INBTSerializable<NBTBase>) icp);
					ns.add(s);
				}
				pl.add(icp);
			}
			providers = pl.toArray(new ICapabilityProvider[size]);
			writers = nb.toArray(new INBTSerializable[nb.size()]);
			names = ns.toArray(new String[ns.size()]);
		} else {
			cachedNBT = null;
		}
		checked = true;
		if (cachedNBT != null) {
			deserializeNBT(cachedNBT);
			cachedNBT = null;
		}
		return providers.length > 0;
	}

	@Override public NBTBase serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		if (writers == null)
			return nbt;
		for (int i = 0; i < writers.length; i++)
			nbt.setTag(names[i], writers[i].serializeNBT());
		return nbt;
	}

	@Override public void deserializeNBT(NBTBase nbt) {
		if (nbt == null)
			return;
		if (!checked) {
			cachedNBT = nbt;
			return;
		}
		if (writers == null)
			return;
		if (nbt instanceof NBTTagCompound) {
			NBTTagCompound tc = (NBTTagCompound) nbt;
			for (int i = 0; i < writers.length; i++)
				if (tc.hasKey(names[i]))
					writers[i].deserializeNBT(tc.getTag(names[i]));
		}
	}
}
