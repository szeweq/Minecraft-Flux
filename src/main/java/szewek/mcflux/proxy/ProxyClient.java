package szewek.mcflux.proxy;

import szewek.mcflux.MCFluxMod;

public class ProxyClient extends ProxyCommon {
	@Override
	public void init() {
		MCFluxMod.renders();
	}
}
