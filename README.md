![Minecraft Flux Logo](https://raw.githubusercontent.com/Szewek/Minecraft-Flux/master/mcflux.png)
# Minecraft-Flux [![Build Status](https://travis-ci.org/Szewek/Minecraft-Flux.svg?branch=master)](https://travis-ci.org/Szewek/Minecraft-Flux) [![CurseForge](http://cf.way2muchnoise.eu/full_248942_downloads.svg)](http://minecraft.curseforge.com/projects/minecraft-flux)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FSzewek%2FMinecraft-Flux.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2FSzewek%2FMinecraft-Flux?ref=badge_shield)
An open energy distribution system for Minecraft mods. **NOW WITH IC2 SUPPORT**

1 Minecraft Flux (MF) = 1 RF = 1 any other RF-based unit

Minecraft-Flux is a mod (and a library) which uses Forge Capabilities to provide a simple way of implementing energy to items, tile entities, living entities and a whole world.
It requires Java 8 to run.

Check [CurseForge page](https://minecraft.curseforge.com/projects/minecraft-flux) for more details.

## Add as dependency in your _build.gradle_ file
```gradle
// NOT IN BUILDSCRIPT
repositories {
    maven {
        name = "CurseForge"
        url = "https://minecraft.curseforge.com/api/maven/"
    }
}
dependencies {
    // Here you can add dependencies
    deobfCompile "minecraft-flux:mcflux:<VERSION>"
}
```
Replace `<VERSION>` with a release version.

## TODO List
- [x] Add Flavored Energy (like Vis or Mana)
- [x] Provide FULL RF Compatiblity (including wrappers for capabilities)
- [x] Implement and document Fluxable
- [x] Implement and document Flux Work
- [x] IndustrialCraft 2 Support
- [x] Force update on client-side tile entities
- [ ] Add compatibility for RF-like mods and libraries (few mods left)
- [ ] Mixing Flavors
- [ ] Compatibility for non-RF mods

## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FSzewek%2FMinecraft-Flux.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2FSzewek%2FMinecraft-Flux?ref=badge_large)