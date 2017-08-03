package szewek.mcflux.special;

import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public final class SpecialEventReceiver implements ICapabilityProvider, INBTSerializable<NBTBase> {
	@CapabilityInject(SpecialEventReceiver.class)
	public static Capability<SpecialEventReceiver> SELF_CAP = null;

	private LongSet received = new LongArraySet();

	@Override public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing f) {
		return cap == SELF_CAP;
	}

	@SuppressWarnings("unchecked")
	@Nullable @Override public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing f) {
		return cap == SELF_CAP ? (T) this : null;
	}

	@Override public NBTBase serializeNBT() {
		SpecialEventHandler.serNBT.add();
		NBTTagList nbt = new NBTTagList();
		for (long l : received) {
			nbt.appendTag(new NBTTagLong(l));
		}
		return nbt;
	}

	@Override public void deserializeNBT(NBTBase nbt) {
		if (nbt != null && nbt instanceof NBTTagList) {
			SpecialEventHandler.deserNBT.add();
			NBTTagList nbtl = (NBTTagList) nbt;
			for (int i = 0; i < nbtl.tagCount(); i++) {
				NBTTagLong ln = (NBTTagLong) nbtl.get(i);
				if (ln != null) {
					received.add(ln.getLong());
				}
			}
		}
	}

	public void addReceived(long l) {
		received.add(l);
	}

	public boolean alreadyReceived(long l) {
		return received.contains(l);
	}
}
