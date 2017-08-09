package szewek.mcflux.special;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public final class SpecialEventReceiver implements ICapabilityProvider, INBTSerializable<NBTBase> {
	@CapabilityInject(SpecialEventReceiver.class)
	public static Capability<SpecialEventReceiver> SELF_CAP = null;

	private IntSet received = new IntArraySet();

	@Override public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing f) {
		return cap == SELF_CAP;
	}

	@SuppressWarnings("unchecked")
	@Nullable @Override public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing f) {
		return cap == SELF_CAP ? (T) this : null;
	}

	@Override public NBTBase serializeNBT() {
		SpecialEventHandler.serNBT.add();
		return new NBTTagIntArray(received.toIntArray());
	}

	@Override public void deserializeNBT(NBTBase nbt) {
		if (nbt == null) {
			return;
		}
		SpecialEventHandler.deserNBT.add();
		// NEW NBT DESERIALIZING
		if (nbt instanceof NBTTagIntArray) {
			int[] ia = ((NBTTagIntArray) nbt).getIntArray();
			for (int i : ia) {
				received.add(i);
			}
		}
		// OLD NBT DESERIALIZING
		else if (nbt instanceof NBTTagList) {
			NBTTagList nbtl = (NBTTagList) nbt;
			for (int i = 0; i < nbtl.tagCount(); i++) {
				NBTBase nb = nbtl.get(i);
				if (nb instanceof NBTPrimitive) {
					received.add(((NBTPrimitive) nb).getInt());
				}
			}
		}
	}

	public void addReceived(int l) {
		received.add(l);
	}

	public boolean alreadyReceived(int l) {
		return received.contains(l);
	}
}
