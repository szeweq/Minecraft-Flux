package szewek.mcflux.fluxable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;
import szewek.mcflux.L;
import szewek.mcflux.api.ex.Battery;
import szewek.mcflux.config.MCFluxConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * World Chunk Energy implementation.
 */
public class WorldChunkEnergy implements ICapabilityProvider, INBTSerializable<NBTBase> {
	@CapabilityInject(WorldChunkEnergy.class)
	public static Capability<WorldChunkEnergy> CAP_WCE = null;

	private Map<ChunkPos, Battery> energyChunks = new HashMap<>();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		return cap == CAP_WCE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		if (cap == CAP_WCE)
			return (T) this;
		return null;
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
		ChunkPos cp = new ChunkPos(bx / 16, by / 16, bz / 16);
		Battery eb = energyChunks.get(cp);
		if (eb == null) {
			eb = new Battery(MCFluxConfig.WORLDCHUNK_CAP);
			energyChunks.put(cp, eb);
		}
		return eb;
	}

	private static class ChunkPos {
		final int cx, cy, cz;

		ChunkPos(int x, int y, int z) {
			cx = x;
			cy = y;
			cz = z;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof ChunkPos))
				return false;
			ChunkPos cp = (ChunkPos) obj;
			return cx == cp.cx && cy == cp.cy && cz == cp.cz;
		}
		
		@Override
		public int hashCode() {
			return (cy + cz * 31) * 31 + cx;
		}
	}

	@Override
	public NBTBase serializeNBT() {
		NBTTagList nbtl = new NBTTagList();
		for (Map.Entry<ChunkPos, Battery> e : energyChunks.entrySet()) {
			Battery eb = e.getValue();
			if (eb.getEnergy() <= 0)
				continue;
			NBTTagCompound nbt = new NBTTagCompound();
			ChunkPos cp = e.getKey();
			nbt.setInteger("x", cp.cx);
			nbt.setInteger("y", cp.cy);
			nbt.setInteger("z", cp.cz);
			nbt.setTag("e", eb.serializeNBT());
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
				if (nbt.hasKey("x", NBT.TAG_INT) && nbt.hasKey("z", NBT.TAG_INT)) {
					ChunkPos cp = new ChunkPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
					Battery eb = new Battery(MCFluxConfig.WORLDCHUNK_CAP);
					if (nbt.hasKey("e"))
						eb.deserializeNBT(nbt.getTag("e"));
					energyChunks.put(cp, eb);
				}
			}
		}
	}

	public static class ChunkStorage implements Capability.IStorage<WorldChunkEnergy> {
		@Override
		public NBTBase writeNBT(Capability<WorldChunkEnergy> cap, WorldChunkEnergy instance, EnumFacing side) {
			L.info("WRITING World Chunk Energy NBT");
			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<WorldChunkEnergy> cap, WorldChunkEnergy instance, EnumFacing side, NBTBase nbt) {
			L.info("READING World Chunk Energy NBT " + nbt);
			instance.deserializeNBT(nbt);
		}
	}
}
