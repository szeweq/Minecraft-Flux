package szewek.mcflux.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.util.TransferType;

public abstract class TileEntityEnergyMachine extends TileEntity implements ITickable {
	private TransferType[] sideTransfer = new TransferType[] {TransferType.NONE, TransferType.NONE, TransferType.NONE, TransferType.NONE, TransferType.NONE, TransferType.NONE};
	private boolean needsUpdate = false;
	
	public TransferType[] getSideTransfer() {
		return sideTransfer;
	}
	
	@Override
	public void update() {
		if (needsUpdate) {
			System.out.println("NEED UPDATE");
			IBlockState ibs = worldObj.getBlockState(pos);
			for (int i = 0; i < 6; i++) {
				ibs = ibs.withProperty(BlockEnergyMachine.sideFromId(i), sideTransfer[i].ord);
			}
			worldObj.setBlockState(pos, ibs, 2);
			needsUpdate = false;
		}
		
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		int[] sides = compound.getIntArray("sides");
		if (sides.length != 6) return;
		TransferType[] tt = TransferType.values();
		for (int i = 0; i < 6; i++) {
			sideTransfer[i] = tt[sides[i]];
		}
		needsUpdate = true;
		System.out.println("WORLD: " + worldObj != null);
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		int[] sides = new int[6];
		for (int i = 0; i < 6; i++) {
			sides[i] = sideTransfer[i].ordinal();
		}
		compound.setIntArray("sides", sides);
		return compound;
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState obs, IBlockState nbs) {
		return obs.getBlock() != nbs.getBlock() || obs.getValue(BlockEnergyMachine.VARIANT) != nbs.getValue(BlockEnergyMachine.VARIANT);
	}
	
	public void switchSideTransfer(EnumFacing f) {
		int s = f.ordinal();
		int v = (sideTransfer[s].ordinal() + 1) % 3;
		sideTransfer[s] = TransferType.values()[v];
		worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockEnergyMachine.sideFromId(s), sideTransfer[s].ord), 2);
	}
}
