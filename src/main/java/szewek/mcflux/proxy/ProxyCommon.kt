package szewek.mcflux.proxy

open class ProxyCommon {
	open fun preInit() {}

	open fun init() {}

	open fun side() = "SERVER"
}
