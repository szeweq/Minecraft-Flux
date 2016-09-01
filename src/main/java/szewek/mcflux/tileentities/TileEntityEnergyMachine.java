package szewek.mcflux.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.util.TransferType;

public abstract class TileEntityEnergyMachine extends TileEntity implements ITickable {
	protected TransferType[] sideTransfer = new TransferType[] {TransferType.NONE, TransferType.NONE, TransferType.NONE, TransferType.NONE, TransferType.NONE, TransferType.NONE};
	private IBlockState cachedState;
	
	public TileEntityEnergyMachine(IBlockState ibs) {
		cachedState = ibs;
	}
	
	public IBlockState getCachedState() {
		return cachedState;
	}
	
	@Override
	public void update() {
		IBlockState ibs = worldObj.getBlockState(pos);
		if (ibs != cachedState)
			worldObj.setBlockState(pos, cachedState, 3);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		int[] sides = compound.getIntArray("sides");
		if (sides.length != 6) return;
		TransferType[] tt = TransferType.values();
		IBlockState oldState = cachedState;
		for (int i = 0; i < 6; i++) {
			sideTransfer[i] = tt[sides[i]];
			cachedState = cachedState.withProperty(BlockEnergyMachine.sideFromId(i), sides[i]);
		}
		if (oldState != cachedState)
			markDirty();
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
	public boolean shouldRefresh(World w, BlockPos pos, IBlockState obs, IBlockState nbs) {
		if (w.isRemote) return obs != nbs;
		return obs.getBlock() != nbs.getBlock() || obs.getValue(BlockEnergyMachine.VARIANT) != nbs.getValue(BlockEnergyMachine.VARIANT);
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, getBlockMetadata(), writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
	
	public void switchSideTransfer(EnumFacing f) {
		int s = f.ordinal();
		int v = (sideTransfer[s].ordinal() + 1) % 3;
		sideTransfer[s] = TransferType.values()[v];
		cachedState = cachedState.withProperty(BlockEnergyMachine.sideFromId(s), v);
		markDirty();
	}
}
