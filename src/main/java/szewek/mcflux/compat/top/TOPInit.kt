package szewek.mcflux.compat.top

import mcjty.theoneprobe.api.ITheOneProbe

import szewek.mcflux.MCFlux.Companion.L

class TOPInit : com.google.common.base.Function<ITheOneProbe, Void> {

	override fun apply(probe: ITheOneProbe?): Void? {
		L!!.info("Minecraft-flux prepares integration with The One Probe...")
		val mftop = MCFluxTOPProvider()
		probe!!.registerProvider(mftop)
		probe.registerEntityProvider(mftop)
		return null
	}
}
