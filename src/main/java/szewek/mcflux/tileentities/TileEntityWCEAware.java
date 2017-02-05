package szewek.mcflux.tileentities;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import szewek.mcflux.api.ex.Battery;
import szewek.mcflux.fluxable.WorldChunkEnergy;

import javax.annotation.Nonnull;

public class TileEntityWCEAware extends TileEntity implements ITickable {
	protected WorldChunkEnergy wce = null;
	protected Battery bat = null;
	protected int updateMode = 0;
	protected boolean worldUpdate = false, posUpdate = false;

	@Override
	public void setWorld(@Nonnull World w) {
		if (world == null || !world.equals(w))
			updateMode |= 1;
		super.setWorld(w);
	}

	@Override
	public void setPos(@Nonnull BlockPos bp) {
		if (pos == null || !pos.equals(bp))
			updateMode |= 2;
		super.setPos(bp);
	}

	@Override public void update() {
		if (wce == null)
			updateMode |= 1;
		if (bat == null)
			updateMode |= 2;
		if (updateMode != 0) {
			if ((updateMode & 1) != 0)
				wce = world.getCapability(WorldChunkEnergy.CAP_WCE, null);
			if (wce == null)
				return;
			if ((updateMode & 2) != 0)
				bat = wce.getEnergyChunk(pos.getX(), pos.getY(), pos.getZ());
			if (updateVariables())
				updateMode = 0;
		}
		updateTile();
	}

	protected boolean updateVariables() {
		return true;
	}

	protected void updateTile() {}
}
