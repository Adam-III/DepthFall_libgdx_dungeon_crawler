# DepthFall: Turn-Based Roguelite RPG

A 2D turn-based dungeon crawler with roguelite meta-progression, written in **Java** with **libGDX** built as a school project.
You recruit a party of heroes, explore procedurally generated dungeons, fight enemy squads and bosses, collect loot, and spend currencies on permanent upgrades between runs.

---

## Features

- **Procedural dungeons:** a random-walk generator with backtracking builds a main path from the start room to the boss, plus side branches with battle rooms, hallways and treasure chests. Map size and complexity scale with floor depth.
- **Turn-based combat:** turn order comes from attack speed. You fight with a front row (melee) and a back row (ranged) and can attack, block or use an ability. Combat includes critical hits, stuns, and animated melee lunges and projectiles.
- **Six hero classes:** Swordsman, Knight, Tank, Archer, Mage and Healer, each with its own stats and weapon type. Some have unlockable abilities: Heal, Stun and Multishot.
- **Budget-based enemy spawning:** each floor has a "battle budget" that is spent on enemies from five biomes (Slimes, Plants, Mushrooms, Animals, Devils) across 100 floors, with a Red Dragon boss at the end of every floor.
- **Loot and gear:** 8 item ranks, from Wooden to Obsidian. Drop chances are interpolated per floor. Weapons, shields and armor can be upgraded up to level 10.
- **Economy and meta-progression:** gold buys gear in the shop and hires heroes. Knowledge Orbs unlock new heroes and permanent upgrades, such as crit chance, gold and orb multipliers, party size and starting floor.
- **Save system:** 3 manual save slots plus an autosave, stored as JSON. Saves cover party, gear, HP/mana, inventory, shop stock and upgrades.

## Tech Stack

| | |
|---|---|
| Language | Java 17 |
| Framework | libGDX (LWJGL3 desktop backend), Scene2D UI |
| Build | Gradle (wrapper included) |
| Serialization | libGDX `Json` |
| Logging | SLF4J |

## Architecture & Design Patterns

| Pattern | Where |
|---|---|
| **State machine** | `GameManager` switches screens by `GameState`; `BattleManager` drives combat through `BattleState` |
| **Strategy** | `AbilityStrategy` with `HealingAbility`, `StunAbility` and `MultishotAbility` |
| **Factory** | `EnemyFactory`, `FriendlyFactory`, `LootFactory`, `RoomFactory` |
| **Blueprint / data-driven design** | Enemies and items are defined as blueprint records and instantiated through constructor references or reflection |
| **Observer** | `HealthObserver` is notified when a character's HP changes |
| **DTO** | `SaveData` separates persisted state from runtime objects |

```
core/src/main/java/org/game/
├── coreGame/    GameManager (entry point, screen routing, currencies), GameState
├── entities/    Characters, heroes, enemies, bosses, abilities, factories
├── items/       Item hierarchy, blueprints, LootFactory, ShopGenerator
├── managers/    Battle, Dungeon, Party, Inventory, Shop, Loot, Save, Upgrades…
├── screens/     One libGDX Screen per game state
├── ui/          Scene2D components and full-screen UI stages
├── world/       Dungeon generation, difficulty scaling, rooms, chests
├── saves/       Save data DTO
└── systems/     Global upgrade registry
```

## Getting Started

**Requirements:** JDK 17 or newer. You don't need to install Gradle because the wrapper is included.

**IntelliJ IDEA**
1. Clone the repository.
2. Choose `File → Open` and select the project folder (or its `build.gradle`). IntelliJ imports it as a Gradle project.
3. Set the project SDK to JDK 17+ under `File → Project Structure`.
4. Run the Gradle task **`lwjgl3 → Tasks → application → run`**, or run `Lwjgl3Launcher` directly.

**Command line**
```bash
./gradlew lwjgl3:run        # run the game (Windows: gradlew.bat lwjgl3:run)
./gradlew lwjgl3:jar        # build a runnable JAR into lwjgl3/build/libs
```

## Controls

| Action | Input |
|---|---|
| Move between rooms | On-screen arrows |
| Open chest (needs a key) | Hover over the chest and press `E` |
| Attack / Block / Ability | `E` / `W` / `Q`, or click the icons |
| Select a target | Left click |
| Cancel targeting | Right click |

## Demo Save

The repo includes a save from later in the game, so you can try deeper floors, better gear and unlocked heroes without grinding.
Start the game → **SAVE / LOAD** → **Load** on **Slot 1**.

## Credits

- **Environment sprites:** [2D Pixel Dungeon Asset Pack v2.0](https://pixel-poem.itch.io/dungeon-assetpuck) by Pixel_Poem
- **Enemy sprites** by [CraftPix.net](https://craftpix.net), used under the CraftPix Freebie License:
  - [Free Chaos Monsters 32x32 Icon Pack](https://craftpix.net/freebies/free-chaos-monsters-32x32-icon-pack)
  - [Free Low Level Monsters Pixel Icons 32x32](https://craftpix.net/freebies/free-low-level-monsters-pixel-icons-32x32)

Third-party assets belong to their creators and are not for reuse or redistribution. Get them from the original sources.
All other art by me.

## AI Assistance

The game's design, architecture and code are my own work. I used AI tools for debugging help and guidance during development, and for a final code review, which found bugs and memory leaks that I then fixed with its help.
