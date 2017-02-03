package szewek.mcflux.wrapper.tesla;

import net.darkhax.tesla.lib.TeslaUtils;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.util.ErrorReport;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectCond;
import szewek.mcflux.util.InjectRegistry;
import szewek.mcflux.util.error.ErrMsgBadImplementation;
import szewek.mcflux.wrapper.EnergyType;
import szewek.mcflux.wrapper.InjectWrappers;

@InjectRegistry(requires = InjectCond.MOD, args = {"tesla", "TESLA"})
public class TeslaInjectRegistry implements IInjectRegistry {
	@Override public void registerInjects() {
		InjectWrappers.addTileWrapperInject(TeslaInjectRegistry::wrapGlobal);
		InjectWrappers.addEntityWrapperInject(TeslaInjectRegistry::wrapGlobal);
		InjectWrappers.addWorldWrapperInject(TeslaInjectRegistry::wrapGlobal);
		InjectWrappers.addItemWrapperInject(TeslaInjectRegistry::wrapGlobal);
	}

	private static <T extends ICapabilityProvider> void wrapGlobal(T icp, InjectWrappers.Registry reg) {
		EnumFacing f = null;
		try {
			for (int i = 0; i < EnumFacing.VALUES.length; i++) {
				f = EnumFacing.VALUES[i];
				if (TeslaUtils.hasTeslaSupport(icp, f)) {
					reg.add(EnergyType.TESLA, new TeslaCapabilityProvider(icp));
					return;
				}
			}
		} catch (Exception e) {
			ErrorReport.addErrMsg(new ErrMsgBadImplementation("TESLA", icp.getClass(), e, f));
		}
		try {
			if (TeslaUtils.hasTeslaSupport(icp, null)) {
				reg.add(EnergyType.TESLA, new TeslaCapabilityProvider(icp));
			}
		} catch (Exception e) {
			ErrorReport.addErrMsg(new ErrMsgBadImplementation("TESLA", icp.getClass(), e, null));
		}
	}
}
