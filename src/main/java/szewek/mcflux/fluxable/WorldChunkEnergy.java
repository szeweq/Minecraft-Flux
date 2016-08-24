package szewek.mcflux.fluxable;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;
import szewek.mcflux.api.EnergyBattery;

public class WorldChunkEnergy implements ICapabilityProvider, INBTSerializable<NBTTagList> {
	private static final int CHUNK_CAPACITY = 20000000;
	private Map<ChunkPos, EnergyBattery> energyChunks = new HashMap<>();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		return cap == CapabilityFluxable.FLUXABLE_WORLD_CHUNK;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		if (cap == CapabilityFluxable.FLUXABLE_WORLD_CHUNK)
			return (T) this;
		return null;
	}
	
	public EnergyBattery getEnergyChunk(int bx, int by, int bz) {
		ChunkPos cp = new ChunkPos(bx / 16, by / 16, bz / 16);
		EnergyBattery eb = energyChunks.get(cp);
		if (eb == null) {
			eb = new EnergyBattery(CHUNK_CAPACITY);
			energyChunks.put(cp, eb);
		}
		return eb;
	}

	private static class ChunkPos {
		public final int cx, cy, cz;
		ChunkPos(int x, int y, int z) {
			cx = x;
			cy = y;
			cz = z;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof ChunkPos)) return false;
			ChunkPos cp = (ChunkPos) obj;
			return cx == cp.cx && cy == cp.cy && cz == cp.cz;
		}
	}

	@Override
	public NBTTagList serializeNBT() {
		NBTTagList nbtl = new NBTTagList();
		for (Map.Entry<ChunkPos, EnergyBattery> e : energyChunks.entrySet()) {
			NBTTagCompound nbt = new NBTTagCompound();
			ChunkPos cp = e.getKey();
			nbt.setInteger("x", cp.cx);
			nbt.setInteger("y", cp.cy);
			nbt.setInteger("z", cp.cz);
			nbt.setTag("e", e.getValue().writeEnergyNBT());
			nbtl.appendTag(nbt);
		}
		return nbtl;
	}

	@Override
	public void deserializeNBT(NBTTagList nbtl) {
		for (int i = 0; i < nbtl.tagCount(); i++) {
			NBTTagCompound nbt = nbtl.getCompoundTagAt(i);
			if (nbt.hasKey("x", NBT.TAG_INT) && nbt.hasKey("z", NBT.TAG_INT)) {
				ChunkPos cp = new ChunkPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
				EnergyBattery eb = new EnergyBattery(CHUNK_CAPACITY);
				if (nbt.hasKey("e", NBT.TAG_COMPOUND))
					eb.readEnergyNBT(nbt.getCompoundTag("e"));
				energyChunks.put(cp, eb);
			}
		}
	}
	
	public static class ChunkStorage implements Capability.IStorage<WorldChunkEnergy> {
		@Override
		public NBTBase writeNBT(Capability<WorldChunkEnergy> capability, WorldChunkEnergy instance, EnumFacing side) {
			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<WorldChunkEnergy> capability, WorldChunkEnergy instance, EnumFacing side, NBTBase nbt) {
			if (nbt instanceof NBTTagList)
				instance.deserializeNBT((NBTTagList) nbt);
		}
	}
}
