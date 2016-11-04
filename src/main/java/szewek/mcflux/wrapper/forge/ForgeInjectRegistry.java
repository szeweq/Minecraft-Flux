package szewek.mcflux.wrapper.forge;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectCond;
import szewek.mcflux.util.InjectRegistry;
import szewek.mcflux.wrapper.EnergyType;
import szewek.mcflux.wrapper.InjectWrappers;

@InjectRegistry(requires = InjectCond.CLASS, args = "net.minecraftforge.energy.IEnergyStorage")
public class ForgeInjectRegistry implements IInjectRegistry {
	@Override public void registerInjects() {
		InjectWrappers.registerTileWrapperInject(ForgeInjectRegistry::wrapTile);
		InjectWrappers.registerEntityWrapperInject(ForgeInjectRegistry::wrapEntity);
		InjectWrappers.registerWorldWrapperInject(ForgeInjectRegistry::wrapWorld);
		InjectWrappers.registerItemWrapperInject(ForgeInjectRegistry::wrapItem);
	}

	private static boolean wrapGlobal(ICapabilityProvider icp, InjectWrappers.Registry reg) {
		try {
			for (EnumFacing f : EnumFacing.VALUES)
				if (icp.hasCapability(CapabilityEnergy.ENERGY, f)) {
					reg.add(EnergyType.FORGE_ENERGY, new ForgeEnergyCapabilityProvider(icp));
					return true;
				}
		} catch (Exception e) {
			InjectWrappers.reportBadImplementation("Forge Energy", true, icp, e);
		}
		try {
			if (icp.hasCapability(CapabilityEnergy.ENERGY, null)) {
				reg.add(EnergyType.FORGE_ENERGY, new ForgeEnergyCapabilityProvider(icp));
				return true;
			}
		} catch (Exception e) {
			InjectWrappers.reportBadImplementation("Forge Energy", false, icp, e);
		}
		return false;
	}

	private static void wrapMappedProvider(ICapabilityProvider icp, InjectWrappers.Registry reg) {
		for (ICapabilityProvider icx : reg.capMap.values()) {
			if (wrapGlobal(icx, reg))
				break;
		}
	}

	private static void wrapTile(TileEntity te, InjectWrappers.Registry reg) {
		if (wrapGlobal(te, reg))
			return;
		wrapMappedProvider(te, reg);
	}

	private static void wrapEntity(Entity ent, InjectWrappers.Registry reg) {
		if (wrapGlobal(ent, reg))
			return;
		wrapMappedProvider(ent, reg);
	}

	private static void wrapWorld(World w, InjectWrappers.Registry reg) {
		if (wrapGlobal(w, reg))
			return;
		wrapMappedProvider(w, reg);
	}

	private static void wrapItem(ItemStack is, InjectWrappers.Registry reg) {
		wrapMappedProvider(is, reg);
	}
}
