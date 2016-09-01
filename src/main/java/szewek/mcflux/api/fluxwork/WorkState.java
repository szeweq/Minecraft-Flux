package szewek.mcflux.api.fluxwork;

public enum WorkState {
	/** TileEntity can't do anything. */
	LAZY,
	/** TileEntity is working. */
	WORKING,
	/** TileEntity paused work because it doesn't have enough energy. */
	PAUSED,
	/** TileEntity finished its work. */
	FINISHED;
}
