package szewek.mcflux;

public final class R {
	public static final String
		MF_VERSION = "@VERSION@",
		MF_NAME = "mcflux",
		MF_FULL_NAME = "Minecraft-Flux",
		MF_DEPENDENCIES = "",
		MF_API = "mcfluxAPI",
		MF_API_EX = MF_API + "|ex",
		MF_API_FE = MF_API + "|fe",
		MF_API_ASSISTANT = MF_API + "|assistant",
		MF_PKG = "szewek.mcflux",
		MF_REPORT_DSN = "https://21320e901b5c4314baae477ed00ecdca:7d0393945e1440cb94ee33217b6ad72b@sentry.io/141749",
		GUI_FACTORY = MF_PKG + ".gui.MCFluxGuiFactory",
		PROXY_SERVER = MF_PKG + ".proxy.ProxyCommon",
		PROXY_CLIENT = MF_PKG + ".proxy.ProxyClient";
		//WAILA_REGISTER = MF_PKG + ".compat.waila.MCFluxWailaProvider.callbackRegister";

	public static final int
		MF_GUI_FLUXGEN = 1;

	private R() {}
}
