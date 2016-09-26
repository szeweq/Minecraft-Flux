package szewek.mcflux.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import szewek.mcflux.MCFlux;
import szewek.mcflux.U;
import szewek.mcflux.api.ex.EX;
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
	public void checkSides(int i, int m) {
		for (; i < m; i++) {
			TransferType tt = sideTransfer[i];
			if (tt == TransferType.NONE)
				continue;
			EnumFacing f = EnumFacing.VALUES[i];
			TileEntity te = worldObj.getTileEntity(pos.offset(f, 1));
			if (te == null)
				continue;
			f = f.getOpposite();
			IEnergy ea = te.getCapability(EX.CAP_ENERGY, f);
			if (ea == null)
				continue;
			switch (tt) {
			case INPUT:
				sideValues[i] = U.transferEnergy(ea, bat, MCFluxConfig.ENERGY_DIST_TRANS * 2) / 2;
				break;
			case OUTPUT:
				sideValues[i] = U.transferEnergy(bat, ea, MCFluxConfig.ENERGY_DIST_TRANS * 2) / 2;
				break;
			}
		}
	}
}
