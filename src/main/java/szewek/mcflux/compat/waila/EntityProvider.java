package szewek.mcflux.compat.waila;

import mcp.mobius.waila.api.ITaggedList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import szewek.mcflux.R;
import szewek.mcflux.U;
import szewek.mcflux.api.ex.EX;
import szewek.mcflux.api.ex.IEnergy;

import java.util.List;

class EntityProvider implements IWailaEntityProvider {
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
		IEnergy ie = e.getCapability(EX.CAP_ENERGY, null);
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
