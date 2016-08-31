package szewek.mcflux.tileentities;

import net.minecraft.block.state.IBlockState;
import szewek.mcflux.MCFlux;
import szewek.mcflux.blocks.BlockEnergyMachine;

public class TileEntityEnergyDistributor extends TileEntityEnergyMachine {
	public TileEntityEnergyDistributor() {
		super(MCFlux.ENERGY_MACHINE.getDefaultState().withProperty(BlockEnergyMachine.VARIANT, BlockEnergyMachine.Variant.ENERGY_DIST));
	}
	
	public TileEntityEnergyDistributor(IBlockState ibs) {
		super(ibs);
	}

	@Override
	public void update() {
		if (worldObj.isRemote) return;
		super.update();
	}

}
