package szewek.mcflux.wrapper.tesla;

import net.darkhax.tesla.lib.TeslaUtils;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.U;
import szewek.mcflux.util.MCFluxReport;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectCond;
import szewek.mcflux.util.InjectRegistry;
import szewek.mcflux.util.ErrMsg;
import szewek.mcflux.wrapper.*;

@InjectRegistry(requires = InjectCond.MOD, args = {"tesla", "TESLA"})
public final class TeslaInjectRegistry implements IInjectRegistry {
	@Override public void registerInjects() {
		InjectCollector ic = InjectWrappers.getCollector();
		ic.addTileWrapperInject(TeslaInjectRegistry::wrapGlobal);
		ic.addEntityWrapperInject(TeslaInjectRegistry::wrapGlobal);
		ic.addItemWrapperInject(TeslaInjectRegistry::wrapGlobal);
	}

	private static <T extends ICapabilityProvider> void wrapGlobal(T icp, WrapperRegistry reg) {
		EnumFacing f = null;
		try {
			for (int i = 0; i < U.FANCY_FACING.length; i++) {
				f = U.FANCY_FACING[i];
				if (TeslaUtils.hasTeslaSupport(icp, f)) {
					reg.add(EnergyType.TESLA, new EnergyCapabilityProvider(icp, TeslaSided::new, null));
					return;
				}
			}
		} catch (Exception e) {
			MCFluxReport.addErrMsg(new ErrMsg.BadImplementation("TESLA", icp.getClass(), e, f));
		}
	}
}
