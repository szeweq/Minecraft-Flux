package szewek.mcflux.fluxable;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;
import szewek.fl.energy.Battery;
import szewek.mcflux.api.fe.FlavoredContainer;
import szewek.mcflux.config.MCFluxConfig;

import javax.annotation.Nonnull;

/**
 * World Chunk Energy implementation.
 */
public final class WorldChunkEnergy implements ICapabilityProvider, INBTSerializable<NBTBase> {
	@CapabilityInject(WorldChunkEnergy.class)
	public static Capability<WorldChunkEnergy> CAP_WCE = null;
	private static final int X_BITS = 22, Z_BITS = 22, Y_BITS = 4, Y_SHIFT = Z_BITS, X_SHIFT = Y_SHIFT + Y_BITS;
	private static final long X_MASK = (1L << X_BITS) - 1L, Y_MASK = (1L << Y_BITS) - 1L, Z_MASK = (1L << Z_BITS) - 1L;

	private Long2ObjectMap<Battery> eChunks = new Long2ObjectOpenHashMap<>();
	private Long2ObjectMap<FlavoredContainer> fChunks = new Long2ObjectOpenHashMap<>();

	private static long packLong(int x, int y, int z) {
		return ((long) x & X_MASK) << X_SHIFT | ((long) y & Y_MASK) << Y_SHIFT | ((long) z & Z_MASK);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, EnumFacing f) {
		return cap == CAP_WCE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, EnumFacing f) {
		return cap == CAP_WCE ? (T) this : null;
	}

	/**
	 * Gets energy chunk (16x16x16) if available. If not, it creates a new one.
	 * 
	 * @param bx Block X position
	 * @param by Block Y position
	 * @param bz Block Z position
	 * @return Chunk battery
	 */
	public Battery getEnergyChunk(int bx, int by, int bz) {
		long l = packLong(bx / 16, by / 16, bz / 16);
		if (eChunks.containsKey(l)) {
			return eChunks.get(l);
		}
		Battery bat = new Battery(MCFluxConfig.WORLDCHUNK_CAP);
		eChunks.put(l, bat);
		return bat;
	}

	public FlavoredContainer getFlavorEnergyChunk(int bx, int by, int bz) {
		long l = packLong(bx / 16, by / 16, bz / 16);
		if (fChunks.containsKey(l)) {
			return fChunks.get(l);
		}
		FlavoredContainer fc = new FlavoredContainer(MCFluxConfig.WORLDCHUNK_CAP / 4);
		fChunks.put(l, fc);
		return fc;
	}

	@Override
	public NBTBase serializeNBT() {
		NBTTagList nbtl = new NBTTagList();
		LongSet poss = new LongArraySet();
		poss.addAll(eChunks.keySet());
		poss.addAll(fChunks.keySet());
		for (long l : poss) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setLong("cp", l);
			Battery e = eChunks.get(l);
			if (e != null)
				nbt.setTag("e", e.serializeNBT());
			FlavoredContainer cf = fChunks.get(l);
			if (cf != null)
				nbt.setTag("fe", cf.serializeNBT());
			nbtl.appendTag(nbt);
		}
		return nbtl;
	}

	@Override
	public void deserializeNBT(NBTBase nbtb) {
		if (nbtb instanceof NBTTagList) {
			NBTTagList nbtl = (NBTTagList) nbtb;
			for (int i = 0; i < nbtl.tagCount(); i++) {
				NBTTagCompound nbt = nbtl.getCompoundTagAt(i);
				boolean hasPos = false;
				long l = 0;
				if (nbt.hasKey("x", NBT.TAG_INT) && nbt.hasKey("y", NBT.TAG_INT) && nbt.hasKey("z", NBT.TAG_INT)) {
					l = packLong(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
					hasPos = true;
				} else if (nbt.hasKey("cp", NBT.TAG_LONG)) {
					l = nbt.getLong("cp");
					hasPos = true;
				}
				if (hasPos) {
					if (nbt.hasKey("e")) {
						Battery eb = new Battery(MCFluxConfig.WORLDCHUNK_CAP);
						eb.deserializeNBT(nbt.getTag("e"));
						eChunks.put(l, eb);
					}
					if (nbt.hasKey("fe")) {
						FlavoredContainer cf = new FlavoredContainer(MCFluxConfig.WORLDCHUNK_CAP / 4);
						cf.deserializeNBT(nbt.getTag("fe"));
						fChunks.put(l, cf);
					}
				}
			}
		}
	}
}
