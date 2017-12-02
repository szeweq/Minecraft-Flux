package szewek.mcflux.tileentities;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import szewek.fl.FLU;
import szewek.fl.annotations.NamedResource;
import szewek.fl.energy.IEnergy;

import java.util.ArrayList;
import java.util.List;

@NamedResource("mcflux:echarger")
public final class TileEntityECharger extends TileEntityWCEAware implements ITickable {
	private int sideIndex = -1;
	private final IEnergy[] sides = new IEnergy[6];
	private IEnergy esrc = null;
	private List<IEnergy> chargeables = new ArrayList<>();

	@Override public void updateTile() {
		if (world.isRemote)
			return;
		checkSources();
		nextSource();
		if (sideIndex == -1)
			return;
		for (IEnergy ie : chargeables) {
			long t = 0;
			do {
				if (esrc != null) {
					t = esrc.to(ie, 4000);
				}
				if (esrc == null || esrc.hasNoEnergy())
					nextSource();
			} while (t == 0 && sideIndex != -1);
			if (sideIndex == -1)
				return;
		}
	}

	public void addEntityEnergy(IEnergy ie) {
		chargeables.add(ie);
	}

	public void removeEntityEnergy(IEnergy ie) {
		chargeables.remove(ie);
	}

	private void checkSources() {
		for (EnumFacing f : EnumFacing.VALUES) {
			BlockPos bp = pos.offset(f, 1);
			TileEntity te = world.getTileEntity(bp);
			if (te != null) {
				esrc = FLU.getEnergySafely(te, f.getOpposite());
				if (esrc != null)
					sides[f.getIndex()] = esrc;
			}
		}
		sideIndex = -1;
	}

	private void nextSource() {
		sideIndex++;
		sideIndex %= 7;
		for (int i = sideIndex; i < 6; i++) {
			if (sides[i] != null && sides[i].getEnergy() > 0) {
				sideIndex = i;
				esrc = sides[i];
				return;
			}
		}
		if (bat != null && bat.getEnergy() > 0) {
			sideIndex = 6;
			esrc = bat;
			return;
		}
		sideIndex = -1;
	}
}
