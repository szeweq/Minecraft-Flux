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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MCFluxWrapper implements ICapabilityProvider, INBTSerializable<NBTBase> {
	public static final MCFluxLocation MCFLUX_WRAPPER = new MCFluxLocation("wrapper");
	final Object mainObject;
	private ICapabilityProvider[] providers = new ICapabilityProvider[0];
	private Map<String, INBTSerializable<NBTBase>> nbts = new HashMap<>();
	private NBTBase cachedNBT = null;

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

	@SuppressWarnings("unchecked")
	@Nullable @Override public <T> T getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing f) {
		if (providers.length == 0)
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
	boolean addWrappers(Map<String, ICapabilityProvider> icps) {
		int size = icps.size();
		if (size == 0) {
			return false;
		}
		List<ICapabilityProvider> pl = new ArrayList<>(size);
		for (Map.Entry<String, ICapabilityProvider> e : icps.entrySet()) {
			String s = e.getKey().toLowerCase();
			ICapabilityProvider icp = e.getValue();
			if (icp instanceof INBTSerializable)
				nbts.put(s, (INBTSerializable<NBTBase>) icp);
			pl.add(icp);
		}
		providers = pl.toArray(new ICapabilityProvider[size]);
		if (cachedNBT != null) {
			deserializeNBT(cachedNBT);
			cachedNBT = null;
		}
		return providers.length > 0;
	}

	@Override public NBTBase serializeNBT() {
		NBTTagCompound nbttc = new NBTTagCompound();
		if (nbts.isEmpty())
			return nbttc;
		for (Map.Entry<String, INBTSerializable<NBTBase>> e : nbts.entrySet()) {
			String s = e.getKey();
			INBTSerializable<NBTBase> nbtz = e.getValue();
			nbttc.setTag(s, nbtz.serializeNBT());
		}
		return nbttc;
	}

	@Override public void deserializeNBT(NBTBase nbt) {
		if (providers.length == 0) {
			cachedNBT = nbt;
			return;
		}
		if (nbt == null || !(nbt instanceof NBTTagCompound))
			return;
		NBTTagCompound nbttc = (NBTTagCompound) nbt;
		for (Map.Entry<String, INBTSerializable<NBTBase>> e : nbts.entrySet())
			e.getValue().deserializeNBT(nbttc.getTag(e.getKey()));
	}
}
