package szewek.mcflux.util.error;

import szewek.mcflux.L;

public class ErrMsgThrownException extends ErrMsg {
	public ErrMsgThrownException(Exception x) {
		super("exception", x.getClass(), x);
	}

	@Override protected void printError() {
		L.warn("Caught an exception: " + cl.getName());
		L.warn(msgThrown);
	}

	@Override protected void printShortError(int total, long delta) {
		L.warn("Exceptions (" + cl.getName() + "): " + total + " in " + delta + " ms");
	}
}
