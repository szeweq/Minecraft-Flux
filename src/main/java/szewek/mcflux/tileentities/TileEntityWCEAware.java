package szewek.mcflux.tileentities;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import szewek.mcflux.fluxable.WorldChunkEnergy;

import javax.annotation.Nonnull;

public class TileEntityWCEAware extends TileEntity {
	protected WorldChunkEnergy wce = null;

	@Override
	public void setWorld(@Nonnull World w) {
		super.setWorld(w);
		wce = world != null && !world.isRemote ? world.getCapability(WorldChunkEnergy.CAP_WCE, null) : null;
	}
}
