package szewek.mcflux.wrapper.forge;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import szewek.mcflux.util.ErrorReport;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectCond;
import szewek.mcflux.util.InjectRegistry;
import szewek.mcflux.util.error.ErrMsgBadImplementation;
import szewek.mcflux.wrapper.EnergyType;
import szewek.mcflux.wrapper.InjectWrappers;

@InjectRegistry(requires = InjectCond.CLASS, args = "net.minecraftforge.energy.IEnergyStorage")
public class ForgeInjectRegistry implements IInjectRegistry {
	@Override public void registerInjects() {
		InjectWrappers.addTileWrapperInject(ForgeInjectRegistry::wrapGlobal);
		InjectWrappers.addEntityWrapperInject(ForgeInjectRegistry::wrapGlobal);
		InjectWrappers.addWorldWrapperInject(ForgeInjectRegistry::wrapGlobal);
		InjectWrappers.addItemWrapperInject(ForgeInjectRegistry::wrapGlobal);
	}

	private static <T extends ICapabilityProvider> void wrapGlobal(T icp, InjectWrappers.Registry reg) {
		EnumFacing f = null;
		try {
			for (int i = 0; i < EnumFacing.VALUES.length; i++) {
				f = EnumFacing.VALUES[i];
				if (icp.hasCapability(CapabilityEnergy.ENERGY, f)) {
					reg.add(EnergyType.FORGE_ENERGY, new ForgeEnergyCapabilityProvider(icp));
					return;
				}
			}
		} catch (Exception e) {
			ErrorReport.addErrMsg(new ErrMsgBadImplementation("Forge Energy", icp.getClass(), e, f));
		}
		try {
			if (icp.hasCapability(CapabilityEnergy.ENERGY, null)) {
				reg.add(EnergyType.FORGE_ENERGY, new ForgeEnergyCapabilityProvider(icp));
			}
		} catch (Exception e) {
			ErrorReport.addErrMsg(new ErrMsgBadImplementation("Forge Energy", icp.getClass(), e, null));
		}
	}
}
