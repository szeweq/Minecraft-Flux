package szewek.mcflux.fluxcompat;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import szewek.mcflux.MCFlux;
import szewek.mcflux.util.ErrMsg;
import szewek.mcflux.util.InjectCond;
import szewek.mcflux.util.MCFluxLocation;
import szewek.mcflux.util.MCFluxReport;

import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public final class FluxCompat {
	private static final MCFluxLocation COMPAT = new MCFluxLocation("fluxcompat");
	private static final Thread th = new Thread();
	private static final ConcurrentLinkedQueue<LazyEnergyCapProvider> lq = new ConcurrentLinkedQueue<>();
	private static Lookup[] compatLookups = null;
	private static Set<Lookup> lset = new HashSet<>();

	public static void init() {
		if (lset != null) {
			compatLookups = lset.toArray(new Lookup[lset.size()]);
		}
		MCFlux.L.info("Added compat lookups: " + compatLookups.length);
		lset = null;
		MinecraftForge.EVENT_BUS.register(FluxCompat.class);
		th.setDaemon(true);
		th.start();
	}

	@SuppressWarnings("unchecked")
	public static void addAddon(Addon a, Class<?> c) {
		if (!(Lookup.class.isAssignableFrom(c) && a.requires().check(a.args()))) return;
		final Class<? extends Lookup> lc = (Class<? extends Lookup>) c;
		try {
			lset.add(lc.newInstance());
		} catch (Exception e) {
			MCFluxReport.sendException(e, "Adding FluxCompat addons");
		}
	}

	private static boolean blacklisted(final Object o) {
		final String cn = o.getClass().getName();
		return cn.startsWith("szewek.") || cn.startsWith("net.minecraft.");
	}

	static void findActiveEnergy(LazyEnergyCapProvider lecp) {
		if (blacklisted(lecp.lazyObject)) {
			lecp.setNotEnergy();
			return;
		}
		lecp.status = LazyEnergyCapProvider.Status.ACTIVATED;
		lq.offer(lecp);
		synchronized (th) {
			th.notify();
		}
	}

	private static void findCompat(LazyEnergyCapProvider lecp) {
		final EnumMap<EnergyType, Factory> fs = new EnumMap<>(EnergyType.class);
		for (Lookup l : compatLookups) l.lookFor(lecp, fs::put);
		for (EnergyType et : EnergyType.ALL) {
			Factory f = fs.get(et);
			if (f != null) {
				f.factorize(lecp);
				return;
			}
		}
		lecp.setNotEnergy();
	}

	@SubscribeEvent
	public static void tileCompat(AttachCapabilitiesEvent<TileEntity> ace) {
		if (Loader.instance().hasReachedState(LoaderState.SERVER_ABOUT_TO_START)) {
			final TileEntity te = ace.getObject();
			if (te == null || blacklisted(te)) return;
			ace.addCapability(COMPAT, new LazyEnergyCapProvider(te));
		}
	}

	private static final class Thread extends java.lang.Thread {
		Thread() {
			super("FluxCompat Thread");
		}

		@Override
		public void run() {
			while (isAlive()) try {
				synchronized (this) {
					wait(0);
				}
				LazyEnergyCapProvider l;
				while ((l = lq.poll()) != null)
					if (l.lazyObject == null && l.status != LazyEnergyCapProvider.Status.READY) MCFluxReport.addErrMsg(new ErrMsg.NullWrapper(true));
					else FluxCompat.findCompat(l);
			} catch (Exception e) {
				MCFluxReport.sendException(e, "FluxCompat Thread loop");
			}
		}
	}

	@FunctionalInterface
	public interface Factory {
		void factorize(LazyEnergyCapProvider lecp);
	}

	@FunctionalInterface
	public interface Lookup {
		void lookFor(LazyEnergyCapProvider lecp, Registry r);
	}

	@FunctionalInterface
	public interface Registry {
		void register(EnergyType et, Factory f);
	}

	@FunctionalInterface
	public interface Convert {
		EnergyType getEnergyType();
	}

	@TypeQualifierNickname @SuppressWarnings("unused")
	@Target(TYPE)
	@Retention(RUNTIME)
	public @interface Addon {
		InjectCond requires();
		String[] args();
	}
}
