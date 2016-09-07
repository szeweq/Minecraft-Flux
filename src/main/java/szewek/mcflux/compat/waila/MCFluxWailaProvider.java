package szewek.mcflux.compat.waila;

import mcp.mobius.waila.api.*;
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
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;

import java.util.List;

public class MCFluxWailaProvider {
	@SuppressWarnings("unused")
	public static void callbackRegister(IWailaRegistrar reg) {
		DataProvider dp = new DataProvider();
		EntityProvider ep = new EntityProvider();
		reg.registerBodyProvider(dp, TileEntity.class);
		reg.registerBodyProvider(ep, EntityPlayer.class);
		reg.registerBodyProvider(ep, EntityPig.class);
		reg.registerBodyProvider(ep, EntityCreeper.class);
	}

	private static class DataProvider implements IWailaDataProvider {
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
			IEnergy ie = U.getEnergyHolderTile(te, f);
			if (ie == null)
				return ctip;
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
	private static class EntityProvider implements IWailaEntityProvider {
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
			IEnergy ie = U.getEnergyHolderEntity(e);
			@SuppressWarnings("unchecked")
			ITaggedList<String, String> tgl = (ITaggedList<String, String>) ctip;
			if (ie != null) {
				long nc = ie.getEnergyCapacity();
				if (nc > 0 && tgl.getEntries(R.TAG_MF).size() == 0) {
					tgl.add(nc == 1 ? I18n.format("mcflux.mfcompatible") : U.formatMF(ie.getEnergy(), nc), R.TAG_MF);
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
}
