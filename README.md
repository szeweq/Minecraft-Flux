# Minecraft-Flux ![Minecraft Flux Logo](https://raw.githubusercontent.com/Szewek/Minecraft-Flux/master/src/main/resources/mcflux.png) [![Build Status](https://travis-ci.org/Szewek/Minecraft-Flux.svg?branch=master)](https://travis-ci.org/Szewek/Minecraft-Flux)
An open energy distribution system for Minecraft mods.

1 Minecraft Flux (MF) = 1 RF = 1 any other RF-based unit

Minecraft-Flux uses Forge Capabilities to implement energy handling (it's optional).
It requires Java 8 to run.

## Story
When CoFH Energy API (which was only a part of CoFH API) was released to public many developers started implementing it in their own mods.
But when Minecraft 1.9 and 1.10 came out only a few of them tried implementing code recommended for 1.8 to future versions.
This mod/library provides a simple way of implementing energy to items, tile entities and... even living entities.

## Compatibility with RF and other energy units
Minecraft-Flux is backward-compatible with Redstone Flux. That means every machine or item can be charged with MF energy (but you can't charge MF with RF... YET). Compatibility with other energy systems will be available in future versions.

It is also compatible with Immersive Flux (IF; From Immersive Engineering).

## Flavored Energy
Flavored Energy provides customization of energy. You can create your own custom energy flavor. Just create a name and add some NBT data. If your mod doesn't use normal energy or you are going to create a magic mod then Flavored Energy is the best option.

## Fluxable
WIP

## Flux Work
WIP

## TODO List
- [ ] Find a good Maven repo
- [ ] Provide FULL RF Compatiblity (including wrappers for capabilities)
- [ ] Add compatibility for RF-like mods and libraries
- [x] Add Flavored Energy (like Vis or Mana)
- [ ] Mixing Flavors
- [ ] Implement and document Fluxable
- [ ] Implement and document Flux Work
- [ ] Compatibility for non-RF mods (like InductrialCraft 2)