![Minecraft Flux Logo](https://raw.githubusercontent.com/Szewek/Minecraft-Flux/master/mcflux.png)
# Minecraft-Flux [![Build Status](https://travis-ci.org/Szewek/Minecraft-Flux.svg?branch=master)](https://travis-ci.org/Szewek/Minecraft-Flux)
An open energy distribution system for Minecraft mods. **NOW WITH IC2 SUPPORT**

1 Minecraft Flux (MF) = 1 RF = 1 any other RF-based unit

Minecraft-Flux is a mod (and a library) which uses Forge Capabilities to provide a simple way of implementing energy to items, tile entities and... even living entities..
It requires Java 8 to run.

## Future: 1.0.0 (for MC 1.10 or 1.11)
Version 1.0.0 will provide even better energy implementation, named Energy eXtensions (EX). Instead of choosing IEnergyConsumer or IEnergyProvider (or implementing both), a developer can implement single IEnergy interface (and INBTEnergy if reading/writing NBT is needed).

## Compatibility with RF and other energy units
Minecraft-Flux is backward-compatible with Redstone Flux. That means every machine or item can be charged with MF energy (but you can't charge MF with RF... YET). Compatibility with other energy systems will be available in future versions.

It is also compatible with Immersive Flux (IF; From Immersive Engineering) and EU from IndustrialCraft 2.

## Flavored Energy
Flavored Energy provides customization of energy. You can create your own custom energy flavor. Just create a name and add some NBT data. If your mod doesn't use normal energy or you are going to create a magic mod then Flavored Energy is the best option.

## Fluxable
Fluxable is an experimental package. It includes Player Energy and World Chunk Energy.

### Player Energy
You can charge energy directly into yourself so you do not need to hold any additional batteries. You are a battery.

### World Chunk Energy
Very easy wireless energy system for your base. Each 16x16x16 chunk works as a battery.

## Flux Work
Flux Work is a working cycle system with energy use for Tile Entity.

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