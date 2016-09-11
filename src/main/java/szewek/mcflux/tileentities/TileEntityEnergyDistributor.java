package szewek.mcflux.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import szewek.mcflux.MCFlux;
import szewek.mcflux.U;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.config.MCFluxConfig;
import szewek.mcflux.util.TransferType;

public class TileEntityEnergyDistributor extends TileEntityEnergyMachine {
	public TileEntityEnergyDistributor() {
		super(MCFlux.ENERGY_MACHINE.getDefaultState().withProperty(BlockEnergyMachine.VARIANT, BlockEnergyMachine.Variant.ENERGY_DIST));
	}

	public TileEntityEnergyDistributor(IBlockState ibs) {
		super(ibs);
	}

	@Override
	public void update() {
		super.update();
		if (wce == null || bat == null)
			return;
		for (int i = 0; i < 6; i++) {
			TransferType tt = sideTransfer[i];
			if (tt == TransferType.NONE)
				continue;
			EnumFacing f = EnumFacing.VALUES[i];
			TileEntity te = worldObj.getTileEntity(pos.offset(f));
			if (te == null)
				continue;
			f = f.getOpposite();
			IEnergy from = null, to = null;
			switch (tt) {
			case INPUT:
				from = U.getEnergyHolderTile(te, f);
				if (from == null)
					continue;
				to = bat;
				break;
			case OUTPUT:
				to = U.getEnergyHolderTile(te, f);
				if (to == null)
					continue;
				from = bat;
				break;
			default:
			}
			sideValues[i] = U.transferEnergy(from, to, MCFluxConfig.ENERGY_DIST_TRANS);
		}
	}
}
