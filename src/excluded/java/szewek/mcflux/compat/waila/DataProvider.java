package szewek.mcflux.compat.waila;

import mcp.mobius.waila.api.ITaggedList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import szewek.mcflux.R;
import szewek.mcflux.U;
import szewek.mcflux.api.ex.EX;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;

import java.util.List;

class DataProvider implements IWailaDataProvider {
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor da, IWailaConfigHandler cfg) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack is, List<String> ctip, IWailaDataAccessor da, IWailaConfigHandler cfg) {
		return ctip;
	}

	@Override
	public List<String> getWailaBody(ItemStack is, List<String> ctip, IWailaDataAccessor da, IWailaConfigHandler cfg) {
		TileEntity te = da.getTileEntity();
		if (te instanceof TileEntityEnergyMachine)
			return ctip;
		EnumFacing f = da.getSide();
		IEnergy ie = te.getCapability(EX.CAP_ENERGY, f);
		if (ie == null)
			return ctip;
		@SuppressWarnings("unchecked")
		ITaggedList<String, String> tgl = (ITaggedList<String, String>) ctip;
		long nc = ie.getEnergyCapacity();
		if (nc > 0 && tgl.getEntries(R.TAG_MF).size() == 0) {
			tgl.add(U.formatMF(ie.getEnergy(), nc), R.TAG_MF);
		}
		return ctip;
	}

	@Override
	public List<String> getWailaTail(ItemStack is, List<String> ctip, IWailaDataAccessor da, IWailaConfigHandler cfg) {
		return ctip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP mp, TileEntity te, NBTTagCompound tag, World w, BlockPos pos) {
		return null;
	}
}
