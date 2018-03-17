package szewek.mcflux.util

import com.rollbar.notifier.Rollbar
import net.minecraft.util.EnumFacing
import szewek.mcflux.MCFlux.Companion.L
import szewek.mcflux.fluxcompat.LazyEnergyCapProvider
import java.util.*

abstract class ErrMsg internal constructor(protected val name: String, internal val cl: Class<*>, internal val msgThrown: Throwable?, title: String) {
	private val title: String = "$title errors: "
	private val thrownList: MutableList<Throwable>
	internal var cachedHash: Int = 0
	private var count: Int = 0
	private var lastCount: Int = 0
	private var nextShow: Long = 0

	internal val throwables: List<Throwable?>
		get() = Collections.unmodifiableList(thrownList)

	init {
		thrownList = ArrayList()
		count = 0
		cachedHash = (this.javaClass.hashCode() shl 24) + (cl.hashCode() shl 16) + name.hashCode()
		addThrowable(msgThrown)
	}

	override fun hashCode(): Int {
		return cachedHash
	}

	override fun equals(other: Any?): Boolean {
		return other === this || other is ErrMsg && other.hashCode() == cachedHash
	}

	internal fun addThrowable(th: Throwable?) {
		if (th != null)
			thrownList.add(th)
	}

	internal fun addUp() {
		val now = System.currentTimeMillis()
		count++
		if (count == 1) {
			printError()
			nextShow = now + ERRORS_TIME
			lastCount = 1
			return
		}
		if (nextShow < now) {
			L!!.warn(title + (count - lastCount) + " in " + (now - nextShow + ERRORS_TIME) + " ms")
			nextShow = now + ERRORS_TIME
			lastCount = count
		}
	}

	open fun makeInfo(): String {
		return "| Name: $name\n| Class: ${cl.name}\n| Count: $count"
	}

	internal fun sendInfo(rb: Rollbar) {
		val m = LinkedHashMap<String, Any>()
		m["EM.Name"] = name
		m["EM.Class"] = cl.name
		addInfo(m)
		rb.warning(msgThrown, m, javaClass.name + ": " + if (msgThrown != null) msgThrown.message else "[No MSG thrown]")
	}

	protected open fun addInfo(m: MutableMap<String, Any>) {}

	protected abstract fun printError()

	class BadImplementation(name: String, cl: Class<*>, thrown: Throwable, private val face: EnumFacing?) : ErrMsg(name, cl, thrown, "Bad " + name + " implemenation (" + cl.name + "; " + face + ')'.toString()) {

		init {
			if (face != null)
				cachedHash += 1 + (face.index shl 28)
		}

		override fun printError() {
			L!!.warn("\n+--= Warning: Bad/incomplete $name implementation =--"
					+ "\n| Checked " + (if (face != null) "WITH SIDE $face" else "SIDELESS")
					+ "\n| Capability provider class: ${cl.name} "
					+ "\n| Possibly this is not meant to be an error."
					+ "\n| Tell authors of this implementation about it!"
					+ "\n+--"
			)
			L!!.warn(msgThrown)
		}

		override fun addInfo(m: MutableMap<String, Any>) {
			m["EM.Side"] = face as Any
		}

		override fun makeInfo(): String {
			return super.makeInfo() + "\n| Side: " + (face ?: "none")
		}
	}

	class NullWrapper(private val objectNull: Boolean) : ErrMsg("wrapper", LazyEnergyCapProvider::class.java, null, "Null wrapp" + if (objectNull) "ed obj" else "er") {
		init {
			if (objectNull)
				cachedHash++
		}

		override fun printError() {
			L!!.warn("A wrapp" + (if (objectNull) "ed obj" else "er") + " is null!")
		}

		override fun addInfo(m: MutableMap<String, Any>) {
			m["EM.NullObject"] = objectNull
		}
	}

	companion object {
		private const val ERRORS_TIME: Long = 15000
	}
}
