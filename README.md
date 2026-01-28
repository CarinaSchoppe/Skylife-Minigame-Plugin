# Skylife — Competitive Sky-Islands Minigame (Paper / Kotlin)

A high-intensity **Sky-Islands PvP minigame** that blends **SkyWars-style survival combat** with **Smash-like abilities** (skills), **ranks**, a **coin economy**, and **full database persistence**.

> Status: **Active development** (expect breaking changes).  
> Minecraft: **1.20+** · Java: **17+** · Server: **Paper/Spigot** :contentReference[oaicite:1]{index=1}

---

## Table of Contents

- [What is Skylife?](#what-is-skylife)
- [Core Gameplay Loop](#core-gameplay-loop)
- [Key Features](#key-features)
    - [Skills](#skills)
    - [Economy](#economy)
    - [Ranks](#ranks)
    - [UI & Feedback](#ui--feedback)
    - [Database](#database)
- [Installation](#installation)
- [Configuration](#configuration)
- [Commands](#commands)
- [Database Schema](#database-schema)
- [Development](#development)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)

---

## What is Skylife?

**Skylife** is a modular minigame platform for Paper servers. Players spawn on isolated sky islands, gather resources, pick a loadout of abilities, and fight for coins and stats.

Design goals:

- **Fast rounds** with clear pacing: lobby → skill pick → combat → reset
- **Skill expression** through abilities + positioning
- **Persistence**: coins, ranks, stats survive restarts (DB-backed)
- **Modular architecture** (managers/listeners/skills) to scale features cleanly

---

## Core Gameplay Loop

1. **Lobby / Queue**
2. **Skill Selection** (4 slots, hotbar-integrated)
3. **Match Start** (island spawn, protection window)
4. **Combat & Progression** (kills/deaths, coins, stats, skill usage)
5. **Match End** (results, persistence, auto restart/reset)

---

## Key Features

### Skills

- **4 Skill Slots** per player (auto placed in hotbar slots 1–4)
- **Pre-game selection UI** (GUI-based)
- **Per-skill global limits** (how many players can pick a skill per match)
- **Cooldowns & costs** (per skill, configurable)

Current skills (initial pack):

- ⚡ Speed Boost
- 🔥 Fireball
- 🛡️ Shield
- 💊 Heal
- 🗡️ Strength
- 👻 Invisibility
- 🚀 Double Jump
- ❄️ Ice Path :contentReference[oaicite:2]{index=2}

> Tip: Keep skills readable. Players should understand “what happened” within 1–2 seconds.

---

### Economy

- **Coins** earned through gameplay events
- **Kill rewards** and **death penalties**
- **Persistent storage** in DB :contentReference[oaicite:3]{index=3}

Typical use cases:

- Shop cosmetics / kits
- Unlock new skills / upgrades
- Seasonal resets with leaderboards (planned)

---

### Ranks

Hierarchical ranks for permissions and prestige:

- 🔴 ADMIN
- 🟡 DEV
- 🟢 MOD
- 🔵 VIP
- ⚪ PLAYER :contentReference[oaicite:4]{index=4}

Rank perks:

- Colored chat tags
- Permission gates
- Scoreboard priority
- Persistent storage :contentReference[oaicite:5]{index=5}

---

### UI & Feedback

- **Live scoreboard**: status, K/D, coins, rank, skill limits, colored player list
- **Chat formatting**: rank tags + coin info
- **Nametags**: dynamic rank display above player heads
- **Actionbar**: cooldown/status hints :contentReference[oaicite:6]{index=6}

---

### Database

- **MySQL / PostgreSQL** support
- Uses **Exposed** (ORM) + **HikariCP** (pooling) :contentReference[oaicite:7]{index=7}
- Auto-creates required tables on first startup (recommended on a fresh DB)

Stored data includes:

- player profiles (UUID, name, rank)
- coins
- kill/death stats
- skill selections & match limits
- match history :contentReference[oaicite:8]{index=8}

---

## Installation

### Requirements

- Paper/Spigot **1.20+**
- Java **17+**
- MySQL or PostgreSQL database :contentReference[oaicite:9]{index=9}

### Option A — Build from source (recommended for now)

No releases are published yet, so building locally is the current path. :contentReference[oaicite:10]{index=10}

```bash
./gradlew build
