package szewek.mcflux.util;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.L;

import java.util.HashSet;
import java.util.Set;

public enum ErrorReport {
	;

	private static Set<Class<?>> badClasses = new HashSet<>(), oldClassAPIs = new HashSet<>();

	public static void badImplementation(String name, EnumFacing face, ICapabilityProvider icp, Throwable th) {
		Class<?> cl = icp.getClass();
		if (badClasses.add(cl)) {
			L.warn("\n+----= An error occured when trying to attach a capability =----"
					+ "\n| Bad/incomplete " + name + " implementation (checked " + (face != null ? "WITH SIDE " + face : "SIDELESS") + ")"
					+ "\n| Capability provider class: " + cl.getName()
					+ "\n| Tell authors of this implementation about it!"
					+ "\n| This is not a Minecraft-Flux problem."
					+ "\n+----"
			);
			L.warn(th);
		} else
			L.warn("Bad/incomplete " + name + " implementation error for \"" + cl.getName() + "\" happened again (checked " + (face != null ? "WITH SIDE " + face : "SIDELESS") + ")!");
	}

	public static void oldAPI(String name, Object o) {
		Class<?> cl = o.getClass();
		if (oldClassAPIs.add(cl)) {
			L.warn("\n+----= Warning: Use of old API =----"
					+ "\n| Minecraft-Flux has detected use of API: " + name
					+ "\n| This API may be not supported in the future"
					+ "\n| Object class: " + cl.getName()
					+ "\n+----"
			);
		} else
			L.warn("Use of old API (" + name + ") on \"" + cl.getName() + "\" detected again!");
	}
}
