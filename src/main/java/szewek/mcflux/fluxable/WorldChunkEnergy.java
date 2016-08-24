package szewek.mcflux.fluxable;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.EnergyBattery;

public class WorldChunkEnergy implements ICapabilityProvider {
	private Map<ChunkPos, EnergyBattery> energyChunks = new HashMap<>();

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return false;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return null;
	}
	
	public EnergyBattery getEnergyChunk(int bx, int bz) {
		ChunkPos cp = new ChunkPos(bx / 16, bz / 16);
		EnergyBattery eb = energyChunks.get(cp);
		if (eb == null) {
			eb = new EnergyBattery(10000000);
			energyChunks.put(cp, eb);
		}
		return eb;
	}

	private static class ChunkPos {
		public final int xPos, zPos;
		ChunkPos(int x, int z) {
			xPos = x;
			zPos = z;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof ChunkPos)) return false;
			ChunkPos cp = (ChunkPos) obj;
			return xPos == cp.xPos && zPos == cp.zPos;
		}
	}
}
