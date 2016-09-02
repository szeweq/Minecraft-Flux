package szewek.mcflux.wrapper;

import java.util.function.BiConsumer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

@FunctionalInterface
public interface IWrapperInject<T> {
	void injectWrapper(T t, BiConsumer<ResourceLocation, ICapabilityProvider> add);
}
