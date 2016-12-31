package szewek.mcflux.tileentities;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import szewek.mcflux.U;
import szewek.mcflux.api.ex.Battery;
import szewek.mcflux.api.ex.EX;
import szewek.mcflux.api.ex.IEnergy;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TileEntityECharger extends TileEntityWCEAware implements ITickable {
	private Battery bat = null;
	private int sideIndex = -1;
	private IEnergy[] sides = new IEnergy[6];
	private IEnergy esrc = null;
	private List<IEnergy> chargeables = new ArrayList<>();

	@Override
	public void setPos(@Nonnull BlockPos bp) {
		super.setPos(bp);
		bat = world != null && !world.isRemote ? wce.getEnergyChunk(pos.getX(), pos.getY(), pos.getZ()) : null;
	}

	@Override public void update() {
		checkSources();
		nextSource();
		if (sideIndex == -1)
			return;
		for (IEnergy ie : chargeables) {
			long t;
			do {
				t = U.transferEnergy(esrc, ie, 4000);
				if (esrc == null || esrc.getEnergy() == 0)
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
			TileEntity e = world.getTileEntity(bp);
			if (e != null) {
				esrc = e.getCapability(EX.CAP_ENERGY, f.getOpposite());
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
