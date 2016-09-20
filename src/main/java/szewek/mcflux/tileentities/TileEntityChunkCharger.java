package szewek.mcflux.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import szewek.mcflux.MCFlux;
import szewek.mcflux.U;
import szewek.mcflux.api.ex.Battery;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.util.TransferType;

import static szewek.mcflux.config.MCFluxConfig.CHUNK_CHARGER_TRANS;

public class TileEntityChunkCharger extends TileEntityEnergyMachine {
	public TileEntityChunkCharger() {
		super(MCFlux.ENERGY_MACHINE.getDefaultState().withProperty(BlockEnergyMachine.VARIANT, BlockEnergyMachine.Variant.CHUNK_CHARGER));
	}

	public TileEntityChunkCharger(IBlockState ibs) {
		super(ibs);
	}

	@Override
	public void checkSides(int i, int m) {
		for (; i < m; i++) {
			TransferType tt = sideTransfer[i];
			if (tt == TransferType.NONE)
				continue;
			EnumFacing f = EnumFacing.VALUES[i];
			BlockPos bpc = pos.offset(f, 16);
			Battery ebc = wce.getEnergyChunk(bpc.getX(), bpc.getY(), bpc.getZ());
			if (ebc == null)
				continue;
			switch (tt) {
			case INPUT:
				sideValues[i] = U.transferEnergy(ebc, bat, CHUNK_CHARGER_TRANS) / 2;
				break;
			case OUTPUT:
				sideValues[i] = U.transferEnergy(bat, ebc, CHUNK_CHARGER_TRANS) / 2;
				break;
			default:
			}

		}
	}
}
