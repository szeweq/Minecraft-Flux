![Minecraft Flux Logo](https://raw.githubusercontent.com/Szewek/Minecraft-Flux/master/mcflux.png)
# Minecraft-Flux [![Build Status](https://travis-ci.org/Szewek/Minecraft-Flux.svg?branch=master)](https://travis-ci.org/Szewek/Minecraft-Flux)
An open energy distribution system for Minecraft mods. **NOW WITH IC2 SUPPORT**

1 Minecraft Flux (MF) = 1 RF = 1 any other RF-based unit

Minecraft-Flux is a mod (and a library) which uses Forge Capabilities to provide a simple way of implementing energy to items, tile entities and... even living entities..
It requires Java 8 to run.

Check [CurseForge page](https://minecraft.curseforge.com/projects/minecraft-flux) for more details.

## Future: 1.0.0 (for MC 1.10 or 1.11)
Version 1.0.0 will provide even better energy implementation, named Energy eXtensions (EX). Instead of choosing IEnergyConsumer or IEnergyProvider (or implementing both), a developer can implement single IEnergy interface (and INBTEnergy if reading/writing NBT is needed).

## TODO List
- [x] Add Flavored Energy (like Vis or Mana)
- [x] Provide FULL RF Compatiblity (including wrappers for capabilities)
- [x] Implement and document Fluxable
- [x] Implement and document Flux Work
- [x] IndustrialCraft 2 Support
- [ ] Force update on client-side tile entities
- [ ] Find a good Maven repo
- [ ] Add compatibility for RF-like mods and libraries
- [ ] Mixing Flavors
- [ ] Compatibility for non-RF mods