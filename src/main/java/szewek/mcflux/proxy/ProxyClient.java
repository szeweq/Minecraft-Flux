package szewek.mcflux.proxy;

import szewek.mcflux.MCFlux;

public class ProxyClient extends ProxyCommon {
	@Override
	public void init() {
		MCFlux.renders();
	}
}
