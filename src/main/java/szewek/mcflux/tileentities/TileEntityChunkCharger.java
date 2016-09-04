package szewek.mcflux.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import szewek.mcflux.MCFlux;
import szewek.mcflux.U;
import szewek.mcflux.api.EnergyBattery;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.fluxable.CapabilityFluxable;
import szewek.mcflux.fluxable.WorldChunkEnergy;
import szewek.mcflux.util.TransferType;

import static szewek.mcflux.config.MCFluxConfig.CHUNK_CHARGER_TRANS;

public class TileEntityChunkCharger extends TileEntityEnergyMachine {
	private WorldChunkEnergy wce = null;
	private EnergyBattery eb = null;
	
	public TileEntityChunkCharger() {
		super(MCFlux.ENERGY_MACHINE.getDefaultState().withProperty(BlockEnergyMachine.VARIANT, BlockEnergyMachine.Variant.CHUNK_CHARGER));
	}

	public TileEntityChunkCharger(IBlockState ibs) {
		super(ibs);
	}
	
	@Override
	public void setWorldObj(World worldIn) {
		super.setWorldObj(worldIn);
		if (!worldObj.isRemote)
			wce = worldObj != null && !worldObj.isRemote ? worldObj.getCapability(CapabilityFluxable.FLUXABLE_WORLD_CHUNK, null) : null;
	}
	
	@Override
	public void setPos(BlockPos posIn) {
		super.setPos(posIn);
		eb = worldObj != null && !worldObj.isRemote && pos != null ? wce.getEnergyChunk(pos.getX(), pos.getY(), pos.getZ()) : null;
	}

	@Override
	public void update() {
		super.update();
		if (wce == null || eb == null)
			return;
		for (int i = 0; i < 6; i++) {
			TransferType tt = sideTransfer[i];
			if (tt == TransferType.NONE)
				continue;
			EnumFacing f = EnumFacing.VALUES[i];
			BlockPos bpc = pos.offset(f, 16);
			EnergyBattery ebc = wce.getEnergyChunk(bpc.getX(), bpc.getY(), bpc.getZ());
			if (ebc == null)
				continue;
			switch (tt) {
			case INPUT:
				U.transferEnergy(ebc, eb, CHUNK_CHARGER_TRANS);
				break;
			case OUTPUT:
				U.transferEnergy(eb, ebc, CHUNK_CHARGER_TRANS);
				break;
			default:
			}

		}
	}
}
