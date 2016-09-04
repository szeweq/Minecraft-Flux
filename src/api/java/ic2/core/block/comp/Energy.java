package ic2.core.block.comp;

import ic2.api.energy.tile.IEnergyTile;

public class Energy extends TileEntityComponent {
	public double getCapacity() {
		return 0;
	}

	public double getEnergy() {
		return 0;
	}

	public IEnergyTile getDelegate() {
		return null;
	}
}
