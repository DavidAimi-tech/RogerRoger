# Roger Roger

![Java](https://img.shields.io/badge/Java-Android-orange)
![Platform](https://img.shields.io/badge/Platform-Android-green)
![Tests](https://img.shields.io/badge/Tests-JUnit-blue)

A turn-based strategy card battler for Android, written in Java. Build an army on a limited strength budget, pick a clan, and outwit the enemy by choosing when to capture, skip, or play your trump card.

<!-- Add screenshots to docs/screenshots/ and uncomment:
| Army Builder | Battle | Results |
|---|---|---|
| ![Army builder](docs/screenshots/builder.png) | ![Battle](docs/screenshots/battle.png) | ![Results](docs/screenshots/results.png) |
-->

## Features

- **Army builder:** choose an army of 15, 20, or 30 soldiers and spend exactly 100 strength points across them (1 to 10 each). Edit each soldier by hand, or use the Even and Randomize buttons.
- **Three clans**, each bending the rules in a different way (see below).
- **Three difficulties:** the enemy gets a randomly generated roster on a bigger or smaller budget, and a random clan.
- **Tactical choices every round:** fight, skip a bad matchup, play your strongest soldier as a trump, or capture an enemy soldier instead of killing it.
- **Random hazards:** each battle has a combustion rating that can burn soldiers on either side.
- **Scouting report and scoreboard:** see both armies' next soldier, strongest soldier, captures left, and your remaining skips and trumps.
- **Clan commanders:** a portrait and a clan-specific line react to captures, deaths, loyalty, and combustion.
- **Pixel art interface, music, and sound effects**, with a settings screen for music, sound, and volume.

## How to Play

1. **Build your army.** Pick a clan, an army size, and a difficulty, then assign strength to every soldier. The total must be exactly 100 before you can start.
2. **Fight.** Each round, your top soldier faces the enemy's top soldier. The higher effective strength wins, and a tie is a coin flip. The winner goes to the bottom of its own deck.
3. **Decide the loser's fate.** When you win, you can capture the enemy soldier so it joins your deck, or let it die. A dying soldier has a small chance to survive and return to its own army. Captures are limited.
4. **Use your tools.** Skip moves your top soldier to the bottom of the deck. Trump plays your strongest soldier instead of the top one. Both are limited and have a cooldown.
5. **Win** by eliminating the enemy army. You lose if yours is eliminated. Reaching round 300, or both armies emptying at once, is a draw.

### Clans

| Clan | Ability |
|---|---|
| **Forsythe** | +1 strength on every soldier in the clash |
| **Leone** | +2 extra captures per battle |
| **Baccus** | Less likely to lose soldiers to combustion |

### Difficulty and army size

| Setting | Values |
|---|---|
| Army size | 15, 20, or 30 soldiers |
| Skips per battle | 5, 7, or 9 (grows with army size) |
| Your budget | 100 strength points |
| Enemy budget | Easy 90, Medium 100, Hard 110 |

Every number above lives in `BattleConfig.java`, so the game is easy to retune.

## Architecture

The game rules are separate from the Android interface. The engine has no UI code, so it can be tested on its own.

```
app/src/main/
├── java/com/example/roger_roger/
│   ├── Soldier.java            immutable soldier (id, base strength)
│   ├── Army.java               one army as a deck (LinkedList of soldiers)
│   ├── BattleEngine.java       all battle rules and state
│   ├── BattleConfig.java       tunable constants
│   ├── BattleEvent.java        structured events (combustion, death, capture, loyalty)
│   ├── EnemyGenerator.java     random roster within a strength budget
│   ├── ValidateRoster.java     roster size, range, and total-points checks
│   ├── SoldierNames.java       random soldier names, kept with the soldier when captured
│   ├── CommanderDialogue.java  clan commander lines per event
│   ├── GameAudio.java          music, sound effects, and saved audio settings
│   ├── RogerApp.java           starts and stops music across screens
│   ├── MainActivity.java       main menu
│   ├── ArmyBuilderActivity.java
│   ├── BattleActivity.java
│   ├── ResultsActivity.java
│   └── SettingsActivity.java
└── res/
    ├── layout/                 XML layouts (ConstraintLayout)
    ├── drawable-nodpi/         pixel art: cards, buttons, backgrounds, portraits
    ├── raw/                    music and sound effects
    └── values/
```

Design decisions:

- **Engine and UI are decoupled.** `BattleEngine` exposes read-only getters and action methods (`playRound`, `skipRound`). The activity only reads state and redraws.
- **Randomness is injected.** The engine takes a `java.util.Random`, so battles can be reproduced with a seeded generator.
- **Events drive the story.** The engine records a `BattleEvent` per round. `CommanderDialogue` turns those events into dialogue, so the engine never knows about text or art.
- **Fair enemy generation.** The generator starts every soldier at the minimum, then hands out the remaining points one at a time to random soldiers who are not yet at the maximum. The budget is always exact, and no strength goes out of range.
- **Soldier identity.** Soldiers from both armies share ids, so names are matched by object identity, not by `equals`.
- **One audio manager.** `GameAudio` stores the settings, and `RogerApp` pauses the music when the battle screen is showing.

## Testing

Roster validation has unit tests (size, strength range, and budget total, including boundary values and wrong-budget cases).

Run them from Android Studio by right-clicking the test class and choosing **Run**, or from the command line:

```
./gradlew test
```

Planned: a headless simulator that plays many seeded battles and reports win rates by clan and army size, to balance the game.

## Getting Started

**Requirements:** Android Studio (current stable), and an Android emulator or a device with USB debugging.

```
git clone https://github.com/DavidAimi-tech/roger-roger.git
```

1. Open the project folder in Android Studio and let Gradle sync.
2. Make sure the audio files exist in `app/src/main/res/raw/`: `bg_main`, `button_audio`, `win_sound`, and `lose_sound` (any of `.mp3`, `.ogg`, or `.wav`).
3. Pick a device and press **Run**.

## Roadmap

- Headless balance simulator and tuning of clan abilities
- Smarter enemy decisions (when to capture)
- More soldier art tiers and clan-specific animations
- A short tutorial on first launch

## Author

**David Aimiator**: [GitHub](https://github.com/DavidAimi-tech)

  ## License & Copyright
  Copyright © 2026 [David Aimiator]. All rights reserved.
  
