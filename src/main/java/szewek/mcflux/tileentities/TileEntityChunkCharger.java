package szewek.mcflux.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import szewek.mcflux.MCFlux;
import szewek.mcflux.U;
import szewek.mcflux.api.EnergyBattery;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.fluxable.CapabilityFluxable;
import szewek.mcflux.fluxable.WorldChunkEnergy;
import szewek.mcflux.util.TransferType;

public class TileEntityChunkCharger extends TileEntityEnergyMachine {
	private static final int ENERGY_TRANSFER = 2000000;
	
	public TileEntityChunkCharger() {
		super(MCFlux.ENERGY_MACHINE.getDefaultState().withProperty(BlockEnergyMachine.VARIANT, BlockEnergyMachine.Variant.CHUNK_CHARGER));
	}
	
	public TileEntityChunkCharger(IBlockState ibs) {
		super(ibs);
	}

	@Override
	public void update() {
		super.update();
		if (worldObj.isRemote) return;
		WorldChunkEnergy wce = worldObj.getCapability(CapabilityFluxable.FLUXABLE_WORLD_CHUNK, null);
		EnergyBattery eb = wce.getEnergyChunk(pos.getX(), pos.getY(), pos.getZ());
		for (int i = 0; i < 6; i++) {
			TransferType tt = sideTransfer[i];
			if (tt == TransferType.NONE) continue;
			EnumFacing f = EnumFacing.VALUES[i];
			BlockPos bpc = pos.offset(f, 16);
			EnergyBattery ebc = wce.getEnergyChunk(bpc.getX(), bpc.getY(), bpc.getZ());
			if (ebc == null) continue;
			switch (tt) {
			case INPUT:
				U.transferEnergy(ebc, eb, ENERGY_TRANSFER);
			case OUTPUT:
				U.transferEnergy(eb, ebc, ENERGY_TRANSFER);
			default:
			}
			
		}
	}

}
