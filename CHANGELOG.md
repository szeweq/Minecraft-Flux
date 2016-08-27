## 0.7.0
- MF Tool can read World Chunk Energy
- Code cleanup
- Removed sided extractEnergy and consumeEnergy methods
- EntityActionEnergy replaced PigEnergy and CreeperEnergy

## 0.6.0
- Creepers and Pigs are MF Compatible
- Added MF Tool
- Waila Integration

## 0.5.0
- Fluxable
  * Player can be charged with MF
  * World chunks can be filled with energy
- Flux Work - working cycle for Tile Entities

## 0.4.0
- Better NBT implementation
- Added new interfaces:
  * IEnergyNBT
  * IFlavoredEnergyNBT
- Wrappers don't implement NBT serialization
- IF Compatibility

## 0.3.0
- Better RF compatibility
- Items with RF are also compatible with Minecraft-Flux

## 0.2.0
- Flavored Energy
- Very simple RF compatibility (modified CoFH Energy API included)
- Replaced CapabilityEnergyConsumer and CapabilityEnergyProducer with CapabilityEnergy
- Added:
  * IEnergyHolder
  * RFStorageWrapper
- Removed:
  * IEnergyHandler
  * EnergyProducerStorage
  * EnergyConsumerStorage

## 0.1.2
- Added EnergyBattery

## 0.1.1
- [Mod] Check if Minecraft-Flux is loaded without specified version.

## 0.1.0
- First version