package szewek.mcflux.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import szewek.mcflux.R;
import szewek.mcflux.containers.ContainerFluxGen;
import szewek.mcflux.tileentities.TileEntityFluxGen;

public class MCFluxGuiHandler implements IGuiHandler {
	@Override public Object getServerGuiElement(int ID, EntityPlayer p, World w, int x, int y, int z) {
		if (ID == R.MF_GUI_FLUXGEN) {
			final TileEntityFluxGen tefg = (TileEntityFluxGen) w.getTileEntity(new BlockPos(x, y, z));
			if (tefg != null)
				return new ContainerFluxGen(p, tefg);
		}
		return null;
	}

	@Override public Object getClientGuiElement(int ID, EntityPlayer p, World w, int x, int y, int z) {
		if (ID == R.MF_GUI_FLUXGEN) {
			final TileEntityFluxGen tefg = (TileEntityFluxGen) w.getTileEntity(new BlockPos(x, y, z));
			if (tefg != null)
				return new GuiFluxGen(p, tefg);
		}
		return null;
	}
}
