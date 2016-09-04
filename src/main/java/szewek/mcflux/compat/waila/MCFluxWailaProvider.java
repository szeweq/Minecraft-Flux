package szewek.mcflux.compat.waila;

import java.util.List;

import mcp.mobius.waila.api.ITaggedList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import szewek.mcflux.R;
import szewek.mcflux.U;
import szewek.mcflux.api.IEnergyHolder;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;

public class MCFluxWailaProvider implements IWailaDataProvider, IWailaEntityProvider {
	public static void callbackRegister(IWailaRegistrar reg) {
		MCFluxWailaProvider dp = new MCFluxWailaProvider();
		IWailaEntityProvider iwep = (IWailaEntityProvider) dp;
		reg.registerBodyProvider((IWailaDataProvider) dp, TileEntity.class);
		reg.registerBodyProvider(iwep, EntityPlayer.class);
		reg.registerBodyProvider(iwep, EntityPig.class);
		reg.registerBodyProvider(iwep, EntityCreeper.class);
	}

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
		IEnergyHolder ieh = U.getEnergyHolderTile(te, f);
		if (ieh == null)
			return ctip;
		@SuppressWarnings("unchecked")
		ITaggedList<String, String> tgl = (ITaggedList<String, String>) ctip;
		int nc = ieh.getEnergyCapacity();
		if (nc > 0 && tgl.getEntries(R.TAG_MF).size() == 0) {
			tgl.add(U.formatMF(ieh.getEnergy(), nc), R.TAG_MF);
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

	@Override
	public Entity getWailaOverride(IWailaEntityAccessor ea, IWailaConfigHandler cfg) {
		return null;
	}

	@Override
	public List<String> getWailaHead(Entity e, List<String> ctip, IWailaEntityAccessor ea, IWailaConfigHandler cfg) {
		return ctip;
	}

	@Override
	public List<String> getWailaBody(Entity e, List<String> ctip, IWailaEntityAccessor ea, IWailaConfigHandler cfg) {
		IEnergyHolder ieh = U.getEnergyHolderEntity(e);
		@SuppressWarnings("unchecked")
		ITaggedList<String, String> tgl = (ITaggedList<String, String>) ctip;
		if (ieh != null) {
			int nc = ieh.getEnergyCapacity();
			if (nc > 0 && tgl.getEntries(R.TAG_MF).size() == 0) {
				tgl.add(nc == 1 ? I18n.format("mcflux.mfcompatible") : U.formatMF(ieh.getEnergy(), nc), R.TAG_MF);
			}
		}
		return ctip;
	}

	@Override
	public List<String> getWailaTail(Entity e, List<String> ctip, IWailaEntityAccessor ea, IWailaConfigHandler cfg) {
		return ctip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP mp, Entity e, NBTTagCompound tag, World world) {
		return null;
	}
}
