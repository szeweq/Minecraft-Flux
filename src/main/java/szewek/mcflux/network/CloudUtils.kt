package szewek.mcflux.network

import java.util.concurrent.Executors

object CloudUtils {
	private val xs = Executors.newSingleThreadExecutor()

	fun executeTask(r: () -> Unit) {
		xs.execute(r)
	}
}
