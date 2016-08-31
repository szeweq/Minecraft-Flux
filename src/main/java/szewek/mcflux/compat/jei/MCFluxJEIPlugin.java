package szewek.mcflux.compat.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import szewek.mcflux.compat.jei.crafting.BuiltShapedRecipeHandler;

@JEIPlugin
public class MCFluxJEIPlugin implements IModPlugin {

	@Override
	public void register(IModRegistry registry) {
		registry.addRecipeHandlers(
			new BuiltShapedRecipeHandler()
		);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
	}

}

