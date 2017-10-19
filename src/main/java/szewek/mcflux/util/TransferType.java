package szewek.mcflux.util;

public enum TransferType {
	NONE, INPUT, OUTPUT;
	
	public final byte ord;
	
	TransferType() {
		ord = (byte) ordinal();
	}
}
