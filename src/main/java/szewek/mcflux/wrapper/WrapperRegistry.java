package szewek.mcflux.wrapper;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class WrapperRegistry {
	final Map<String, ICapabilityProvider> resultMap = new HashMap<>();
	private final Map<EnergyType, ICapabilityProvider> energyCapMap;

	WrapperRegistry() {
		energyCapMap = new EnumMap<>(EnergyType.class);
	}

	public void add(EnergyType et, ICapabilityProvider icp) {
		energyCapMap.put(et, icp);
	}

	void resolve(EnergyType[] ets) {
		for (EnergyType et : ets) {
			ICapabilityProvider icp = energyCapMap.get(et);
			if (icp != null) {
				resultMap.put(et.name, icp);
				return;
			}
		}
	}

	public void register(String s, ICapabilityProvider icp) {
		resultMap.put(s, icp);
	}
}
