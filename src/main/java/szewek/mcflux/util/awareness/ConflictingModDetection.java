package szewek.mcflux.util.awareness;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import szewek.mcflux.L;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum ConflictingModDetection {
	;

	public static void listAllConflictingMods() {
		String[] mods = new String[] {
				"energysynergy",
				"commoncapabilities"
		};
		Map<String, ModContainer> modmap = Loader.instance().getIndexedModList();
		List<String> sl = new ArrayList<>();
		for (String m : mods) {
			if (modmap.containsKey(m)) {
				sl.add(modmap.get(m).getName());
			}
		}
		if (!sl.isEmpty()) {
			StringBuilder sb = new StringBuilder("There are mods that can cause a conflict with Minecraft-Flux: ");
			boolean comma = false;
			for (String s : sl) {
				sb.append(s);
				if (comma)
					sb.append(", ");
				comma = true;
			}
			L.warn(sb.toString());
		}
	}
}
