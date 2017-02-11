package szewek.mcflux.tileentities;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import szewek.mcflux.U;
import szewek.mcflux.api.MCFluxAPI;
import szewek.mcflux.api.ex.IEnergy;

import java.util.HashSet;
import java.util.Set;

public final class TileEntityWET extends TileEntityWCEAware implements ITickable {
	private EnumFacing face = null, faceOpposite = null;
	private boolean out = false;
	private int ix = -1, iy = -1, iz = -1, nx = 1, ny = 1, nz = 1;
	private BlockPos upos;
	private Iterable<BlockPos> poss;
	private Set<IEnergy> eset = new HashSet<>(27);

	@Override public void updateTile() {
		if (world.isRemote)
			return;
		eset.clear();
		IEnergy se = null;
		TileEntity te;
		for (BlockPos cbp : poss) {
			te = world.getTileEntity(cbp);
			if (te == null)
				continue;
			IEnergy ie = MCFluxAPI.getEnergySafely(te, faceOpposite);
			if (ie != null)
				eset.add(ie);
		}
		if (eset.isEmpty())
			return;
		te = world.getTileEntity(upos);
		if (te != null)
			se = MCFluxAPI.getEnergySafely(te, face);
		if (se == null) {
			if (bat != null)
				se = bat;
			else
				return;
		}
		for (IEnergy ue : eset) {
			if (out) {
				U.transferEnergy(ue, se, 4000);
				if (se.getEnergy() == se.getEnergyCapacity())
					break;
			} else {
				U.transferEnergy(se, ue, 4000);
				if (se.getEnergy() == 0)
					break;
			}
		}
	}

	@Override
	protected boolean updateVariables() {
		if ((updateMode & 3) == 0)
			return true;
		if (face == null) {
			int m = getBlockMetadata();
			face = EnumFacing.VALUES[m / 2];
			out = m % 2 == 1;
			faceOpposite = face.getOpposite();
			switch (face) {
				case DOWN:
					iy = -3;
					ny = -1;
					break;
				case UP:
					iy = 1;
					ny = 3;
					break;
				case NORTH:
					iz = -3;
					nz = -1;
					break;
				case SOUTH:
					iz = 1;
					nz = 3;
					break;
				case WEST:
					ix = -3;
					nx = -1;
					break;
				case EAST:
					ix = 1;
					nx = 3;
					break;
			}
		}
		poss = BlockPos.getAllInBox(pos.add(ix, iy, iz), pos.add(nx, ny, nz));
		upos = pos.offset(faceOpposite, 1);
		return true;
	}
}
