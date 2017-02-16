package szewek.mcflux.wrapper;

@FunctionalInterface
public interface IWrapperInject<T> {
	void injectWrapper(T t, WrapperRegistry reg);
}