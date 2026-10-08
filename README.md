# Toolbelt Inventory

A small Fabric mod for Minecraft 26.3 that adds five tool-specific slots beside
the player inventory: pickaxe, sword, axe, shovel, and hoe.

Two extra slots appear to the left of the regular hotbar if the toolbelt is used.
One for just the sword and one for cycling tools.  
Scrolling acts as they were just additional hotbar slots. Hold
the **Cycle Toolbelt Tools** key (defaults to **Left Ctrl**) while
scrolling to cycle through the tools that are stored in the toolbelt.

Keybind Option: **Options → Controls → Key Binds → Toolbelt**.

## UI resource packs

The added inventory slots use Minecraft's GUI slot sprite, and the extra HUD
slots use slices of Minecraft's hotbar background and selection sprites. To
customize them, make a resource pack that overrides:

- `assets/minecraft/textures/gui/sprites/container/slot.png` for the slot
  background in the inventory (18×18).
- `assets/minecraft/textures/gui/sprites/hud/hotbar.png` for the extra HUD
  background (182×22; the mod uses the leftmost one or two slot sections).
- `assets/minecraft/textures/gui/sprites/hud/hotbar_selection.png` for the
  selected-slot frame (24×23).

The Minecraft sprites are shared with vanilla, so overriding them also changes
their vanilla uses throughout the game.

## Setup

**Download the .jar**  
(or build the mod with `gradlew build`. The remapped mod jar is written to
`build/libs`)
