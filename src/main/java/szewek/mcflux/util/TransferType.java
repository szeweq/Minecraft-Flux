package szewek.mcflux.util;

public enum TransferType {
	NONE, INPUT, OUTPUT;
	
	public final int ord;
	
	TransferType() {
		ord = ordinal();
	}
}
