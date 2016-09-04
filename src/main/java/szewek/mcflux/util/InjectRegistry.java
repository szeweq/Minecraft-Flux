package szewek.mcflux.util;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Target;

@Target(TYPE)
public @interface InjectRegistry {
	boolean included() default false;
	String[] detectMods();
}
