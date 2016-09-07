package szewek.mcflux.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import szewek.mcflux.MCFlux;
import szewek.mcflux.U;
import szewek.mcflux.api.ex.Battery;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.config.MCFluxConfig;
import szewek.mcflux.fluxable.WorldChunkEnergy;
import szewek.mcflux.util.TransferType;

public class TileEntityEnergyDistributor extends TileEntityEnergyMachine {
	private WorldChunkEnergy wce = null;
	private Battery bat = null;
	
	public TileEntityEnergyDistributor() {
		super(MCFlux.ENERGY_MACHINE.getDefaultState().withProperty(BlockEnergyMachine.VARIANT, BlockEnergyMachine.Variant.ENERGY_DIST));
	}

	public TileEntityEnergyDistributor(IBlockState ibs) {
		super(ibs);
	}
	
	@Override
	public void setWorldObj(World w) {
		super.setWorldObj(w);
		wce = worldObj != null && !worldObj.isRemote ? worldObj.getCapability(WorldChunkEnergy.CAP_WCE, null) : null;
	}

	@Override
	public void setPos(BlockPos posIn) {
		super.setPos(posIn);
		bat = worldObj != null && !worldObj.isRemote && pos != null ? wce.getEnergyChunk(pos.getX(), pos.getY(), pos.getZ()) : null;
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
