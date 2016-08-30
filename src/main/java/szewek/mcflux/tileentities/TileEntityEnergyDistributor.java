package szewek.mcflux.tileentities;

public class TileEntityEnergyDistributor extends TileEntityEnergyMachine {
	
	@Override
	public void update() {
		if (worldObj.isRemote) return;
		super.update();
	}

}
