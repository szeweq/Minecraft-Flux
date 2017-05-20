package szewek.mcflux.wrapper.roots;

import elucent.roots.capability.mana.IManaCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.fe.Flavored;
import szewek.mcflux.api.fe.FlavoredImmutable;
import szewek.mcflux.util.*;
import szewek.mcflux.wrapper.InjectWrappers;
import szewek.mcflux.wrapper.WrapperRegistry;

@InjectRegistry(requires = InjectCond.MOD, args = "roots")
public final class RootsInjectRegistry implements IInjectRegistry {
	@CapabilityInject(IManaCapability.class)
	static Capability<IManaCapability> MANA_CAP = null;
	static final String MANA = "roots:mana";
	static final Flavored[] manaFill = new Flavored[]{new FlavoredImmutable(MANA, null)};

	@Override public void registerInjects() {
		InjectWrappers.getCollector().addEntityWrapperInject(RootsInjectRegistry::wrapGlobal);
	}

	private static <T extends ICapabilityProvider> void wrapGlobal(T icp, WrapperRegistry reg) {
		try {
			if (icp instanceof EntityPlayer && icp.hasCapability(MANA_CAP, null)) {
				reg.register("rootsmana", new RootsPlayerWrapper((EntityPlayer) icp));
			}
		} catch (Exception e) {
			MCFluxReport.addErrMsg(new ErrMsg.BadImplementation("Roots Mana", icp.getClass(), e, null));
		}
	}
}
