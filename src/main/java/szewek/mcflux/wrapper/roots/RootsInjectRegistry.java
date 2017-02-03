package szewek.mcflux.wrapper.roots;

import elucent.roots.capability.mana.IManaCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.fe.Flavored;
import szewek.mcflux.api.fe.FlavoredImmutable;
import szewek.mcflux.util.*;
import szewek.mcflux.util.error.ErrMsgBadImplementation;
import szewek.mcflux.wrapper.InjectWrappers;

@InjectRegistry(requires = InjectCond.MOD, args = "roots")
public class RootsInjectRegistry implements IInjectRegistry {
	@CapabilityInject(IManaCapability.class)
	static Capability<IManaCapability> MANA_CAP = null;
	static final String MANA = "roots:mana";
	static final Flavored[] manaFill = new Flavored[]{new FlavoredImmutable(MANA, null)};
	private static final MCFluxLocation MANA_RL = new MCFluxLocation("rootsmana");

	@Override public void registerInjects() {
		InjectWrappers.addEntityWrapperInject(RootsInjectRegistry::wrapGlobal);
	}

	private static <T extends ICapabilityProvider> void wrapGlobal(T icp, InjectWrappers.Registry reg) {
		try {
			if (icp instanceof EntityPlayer && icp.hasCapability(MANA_CAP, null)) {
				reg.register(MANA_RL, new RootsPlayerWrapper((EntityPlayer) icp));
			}
		} catch (Exception e) {
			ErrorReport.addErrMsg(new ErrMsgBadImplementation("Roots Mana", icp.getClass(), e, null));
		}
	}
}
