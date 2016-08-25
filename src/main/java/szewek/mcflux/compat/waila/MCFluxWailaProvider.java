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
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.IEnergyHolder;

public class MCFluxWailaProvider implements IWailaDataProvider, IWailaEntityProvider {
	public static String MF_TAG_ENERGY = "MFEnergy";
	public static void callbackRegister(IWailaRegistrar reg) {
		MCFluxWailaProvider dp = new MCFluxWailaProvider();
		reg.registerBodyProvider((IWailaDataProvider) dp, TileEntity.class);
		reg.registerBodyProvider((IWailaEntityProvider) dp, EntityPlayer.class);
		reg.registerBodyProvider((IWailaEntityProvider) dp, EntityPig.class);
		reg.registerBodyProvider((IWailaEntityProvider) dp, EntityCreeper.class);
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack is, List<String> ctip, IWailaDataAccessor accessor, IWailaConfigHandler cfg) {
		return ctip;
	}

	@Override
	public List<String> getWailaBody(ItemStack is, List<String> ctip, IWailaDataAccessor accessor, IWailaConfigHandler cfg) {
		TileEntity te = accessor.getTileEntity();
		EnumFacing f = accessor.getSide();
		IEnergyHolder ieh = null;
		if (te.hasCapability(CapabilityEnergy.ENERGY_CONSUMER, f)) {
			ieh = te.getCapability(CapabilityEnergy.ENERGY_CONSUMER, f);
		} else if (te.hasCapability(CapabilityEnergy.ENERGY_PRODUCER, f)) {
			ieh = te.getCapability(CapabilityEnergy.ENERGY_PRODUCER, f);
		}
		if (ieh == null)
			return ctip;
		@SuppressWarnings("unchecked")
		ITaggedList<String, String> tgl = (ITaggedList<String, String>) ctip;
		int nc = ieh.getEnergyCapacity();
		if (nc > 0 && tgl.getEntries(MF_TAG_ENERGY).size() == 0) {
			tgl.add(String.format("%d / %d MF", ieh.getEnergy(), nc), MF_TAG_ENERGY);
		}
		return ctip;
	}

	@Override
	public List<String> getWailaTail(ItemStack is, List<String> ctip, IWailaDataAccessor accessor, IWailaConfigHandler cfg) {
		return ctip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP mp, TileEntity te, NBTTagCompound tag, World w, BlockPos pos) {
		return null;
	}

	@Override
	public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler cfg) {
		return null;
	}

	@Override
	public List<String> getWailaHead(Entity e, List<String> ctip, IWailaEntityAccessor accessor, IWailaConfigHandler cfg) {
		return ctip;
	}

	@Override
	public List<String> getWailaBody(Entity e, List<String> ctip, IWailaEntityAccessor accessor, IWailaConfigHandler cfg) {
		IEnergyHolder ieh = null;
		if (e.hasCapability(CapabilityEnergy.ENERGY_CONSUMER, null)) {
			ieh = e.getCapability(CapabilityEnergy.ENERGY_CONSUMER, null);
		} else if (e.hasCapability(CapabilityEnergy.ENERGY_PRODUCER, null)) {
			ieh = e.getCapability(CapabilityEnergy.ENERGY_PRODUCER, null);
		}
		@SuppressWarnings("unchecked")
		ITaggedList<String, String> tgl = (ITaggedList<String, String>) ctip;
		if (ieh != null) {
			int nc = ieh.getEnergyCapacity();
			if (nc > 0 && tgl.getEntries(MF_TAG_ENERGY).size() == 0) {
				tgl.add(nc == 1 ? I18n.format("mcflux.mfcompatible") : String.format("%d / %d MF", ieh.getEnergy(), nc), MF_TAG_ENERGY);
			}
		} else if (tgl.getEntries(MF_TAG_ENERGY).size() == 0) {
			tgl.add("NO MF", MF_TAG_ENERGY);
		}
		return ctip;
	}

	@Override
	public List<String> getWailaTail(Entity e, List<String> ctip, IWailaEntityAccessor accessor, IWailaConfigHandler cfg) {
		return ctip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, Entity ent, NBTTagCompound tag, World world) {
		return null;
	}
}
