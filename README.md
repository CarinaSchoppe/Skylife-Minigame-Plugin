# 🎮 Skylife Minigame Plugin

Ein umfangreiches Minecraft-Plugin für ein kompetitives Skyblock-ähnliches Minigame mit Skills, Rängen, Wirtschaftssystem und vollständiger Datenbank-Integration.

## 📋 Übersicht

Skylife ist ein komplexes Minigame-Plugin, das Spielern eine intensive PvP- und Skill-basierte Erfahrung bietet. Spieler spawnen auf einer Skyblock-ähnlichen Map, sammeln Ressourcen, leveln ihre Skills und kämpfen gegeneinander, um Coins und Erfahrung zu verdienen.

## ✨ Features

### 🎯 Kern-Gameplay

- **Minigame-System**: Vollständiges Spiel-Management mit Lobby, laufenden Spielen und automatischen Neustarts
- **Skyblock-Mechanik**: Spieler spawnen auf isolierten Inseln und müssen überleben
- **PvP-Combat**: Intensiver Spieler-gegen-Spieler Kampf mit Kill-Tracking und Belohnungen
- **Respawn-System**: Automatisches Respawning mit konfigurierbarer Verzögerung

### 💪 Skill-System

Spieler können aus verschiedenen Skills wählen und diese während des Spiels nutzen:

- **4 Skill-Slots**: Jeder Spieler kann bis zu 4 verschiedene Skills gleichzeitig ausrüsten
- **Hotbar-Integration**: Skills werden automatisch in der Hotbar platziert (Slots 1-4)
- **Skill-Selektion**: Interaktives GUI-System zur Auswahl von Skills vor Spielbeginn
- **Limit-System**: Konfigurierbare Limitierung, wie oft jeder Skill pro Spiel ausgewählt werden kann
- **Cooldown-Management**: Jeder Skill hat individuelle Cooldowns und Kosten

**Verfügbare Skills:**

- ⚡ **Speed Boost**: Temporärer Geschwindigkeitsschub
- 🔥 **Fireball**: Schleudere eine Feuerkugel auf deine Gegner
- 🛡️ **Shield**: Aktiviere temporären Schutz
- 💊 **Heal**: Stelle deine Gesundheit wieder her
- 🗡️ **Strength**: Erhöhe deinen Schaden temporär
- 👻 **Invisibility**: Werde unsichtbar
- 🚀 **Double Jump**: Springe höher und weiter
- ❄️ **Ice Path**: Erzeuge einen Pfad aus Eis unter deinen Füßen

### 💰 Wirtschaftssystem

- **Coins-System**: Verdiene und verliere Coins durch Kills, Deaths und Spielaktivitäten
- **Kill-Belohnungen**: Erhalte Coins für jeden Kill
- **Death-Strafen**: Verliere Coins bei jedem Tod
- **Persistente Speicherung**: Alle Coins werden in der Datenbank gespeichert

### 🏆 Rang-System

Hierarchisches Rängesystem mit verschiedenen Berechtigungsstufen:

- 🔴 **ADMIN**: Höchste Berechtigungsstufe
- 🟡 **DEV**: Entwickler-Zugriff
- 🟢 **MOD**: Moderatoren-Rechte
- 🔵 **VIP**: Premium-Spieler
- ⚪ **PLAYER**: Standard-Spieler

**Rang-Features:**

- Farbige Chat-Tags
- Spezielle Berechtigungen
- Priorisierte Scoreboard-Anzeige
- Persistente Speicherung

### 🎨 UI & Anzeige

- **Scoreboard**: Umfassendes Echtzeit-Scoreboard mit:
    - Spieler-Status und Stats
    - Kill/Death-Anzeige
    - Coins-Balance
    - Rang-Tag
    - Skill-Limits
    - Spieler-Liste mit Farb-Kodierung nach Rängen
- **Chat-System**: Formatierte Chat-Nachrichten mit Rang-Tags und Coins-Anzeige
- **Nametags**: Dynamische Spieler-Nametags mit Rang-Anzeige über dem Kopf
- **Action Bar**: Skill-Cooldown und Status-Informationen

### 📊 Datenbank-Integration

Vollständige MySQL/PostgreSQL-Integration mit Exposed Framework:

**Gespeicherte Daten:**

- Spieler-Profile (UUID, Name, Rang)
- Skill-Auswahl und Limits pro Spiel
- Coins und Wirtschaftsdaten
- Kill/Death-Statistiken
- Spiel-Historie

**Tabellen:**

- `players`: Spieler-Stammdaten
- `player_coins`: Wirtschaftsdaten
- `game_skills`: Skill-Limits und Auswahl
- `game_stats`: Spiel-Statistiken

### 🎯 Gameplay-Features

- **Void-Damage**: Spieler nehmen Schaden unter bestimmter Y-Koordinate
- **Spawn-Protection**: Kurzzeitiger Schutz nach dem Spawn
- **World-Management**: Automatisches Laden und Verwalten der Spielwelt
- **Auto-Respawn**: Konfigurierbare automatische Wiederbelebung
- **Event-Handling**: Umfassendes Event-System für alle Gameplay-Aspekte

## 🔧 Technische Details

### Verwendete Technologien

- **Bukkit/Spigot API**: Minecraft-Server-Framework
- **Kotlin**: Moderne, prägnante Programmiersprache
- **Exposed ORM**: Typsichere SQL-Datenbank-Abstraktion
- **HikariCP**: High-Performance JDBC Connection Pool
- **MySQL/PostgreSQL**: Relationale Datenbank
- **Gradle**: Build-Management

### Architektur

Das Plugin folgt einer modularen Architektur mit klarer Trennung der Zuständigkeiten:

```
├── managers/          # Zentrale Manager-Klassen
│   ├── DatabaseManager     # Datenbank-Verwaltung
│   ├── GameManager         # Spiel-Status und Ablauf
│   ├── PlayerDataManager   # Spieler-Daten und Profile
│   ├── SkillManager        # Skill-System und Cooldowns
│   ├── CoinManager         # Wirtschaftssystem
│   └── RankManager         # Rang-Verwaltung
├── listeners/         # Event-Handler
│   ├── PlayerJoinListener
│   ├── PlayerDeathListener
│   ├── PlayerInteractListener
│   └── ...
├── skills/            # Skill-Implementierungen
│   ├── Skill (Interface)
│   ├── SpeedSkill
│   ├── FireballSkill
│   └── ...
├── utils/             # Hilfsklassen
│   ├── ScoreboardUtil
│   ├── SkillSelectionGUI
│   └── ...
└── database/          # Datenbank-Schema
    └── tables/
```

## ⚙️ Installation

1. **Voraussetzungen**
    - Spigot/Paper Server (1.20+)
    - MySQL oder PostgreSQL Datenbank
    - Java 17+

2. **Plugin installieren**
   ```bash
   # Plugin in den plugins/ Ordner kopieren
   cp Skylife-Minigame-Plugin.jar server/plugins/
   ```

3. **Konfiguration**
    - Bearbeite `config.yml` für grundlegende Einstellungen
    - Konfiguriere Datenbank-Verbindung in der Config
    - Passe Skill-Limits und Gameplay-Parameter an

4. **Datenbank einrichten**
    - Erstelle eine neue Datenbank
    - Das Plugin erstellt automatisch alle benötigten Tabellen beim ersten Start

5. **Server starten**
   ```bash
   java -jar spigot.jar
   ```

## 🎮 Verwendung

### Für Spieler

1. **Beitreten**: Betrete den Server und warte in der Lobby
2. **Skills wählen**: Klicke auf Items in der Hotbar, um deine Skills auszuwählen
3. **Spielen**: Kämpfe gegen andere Spieler und sammle Coins
4. **Skills nutzen**: Rechtsklick auf Skill-Items zum Aktivieren

### Für Admins

```
/minigame start         # Spiel manuell starten
/minigame stop          # Spiel beenden
/rank <player> <rang>   # Rang zuweisen
/coins <player> <amount> # Coins setzen
```

## 📝 Konfiguration

### config.yml Beispiel

```yaml
minigame:
  min-players: 2
  max-players: 16
  game-duration: 900  # Sekunden
  respawn-delay: 5    # Sekunden

skills:
  speed-limit: 2
  fireball-limit: 2
  shield-limit: 2
  heal-limit: 3
  strength-limit: 2
  invisibility-limit: 1
  double-jump-limit: 2
  ice-path-limit: 2

economy:
  kill-reward: 10
  death-penalty: 5
  starting-coins: 100

database:
  host: localhost
  port: 3306
  database: skylife
  username: root
  password: password
```

## 🏗️ Entwicklung

### Build

```bash
./gradlew build
```

### Testing

```bash
./gradlew test
```

### Hot-Reload während Entwicklung

Das Plugin unterstützt Hot-Reload über PluginManager für schnellere Entwicklungszyklen.

## 🔄 Datenbank-Schema

### Players Tabelle

- `id`: Primärschlüssel
- `uuid`: Spieler-UUID (unique)
- `name`: Minecraft-Name
- `rank`: Spieler-Rang
- `created_at`: Erstellungszeitpunkt

### Player Coins Tabelle

- `id`: Primärschlüssel
- `player_id`: Referenz zu Players
- `coins`: Aktuelle Coin-Balance
- `updated_at`: Letzte Aktualisierung

### Game Skills Tabelle

- `id`: Primärschlüssel
- `game_id`: Aktuelle Spiel-ID
- `skill_name`: Name des Skills
- `current_count`: Wie oft der Skill bereits gewählt wurde
- `max_limit`: Maximales Limit

## 🎯 Features in Entwicklung

- [ ] Achievements-System
- [ ] Team-Modus
- [ ] Custom Items und Waffen
- [ ] Seasonal Events
- [ ] Leaderboards
- [ ] Replay-System
- [ ] Spectator-Modus

## 🐛 Bekannte Issues

- Siehe [GitHub Issues](../../issues) für aktuelle Bugs und Feature-Requests

## 🤝 Beitragen

Contributions sind willkommen! Bitte erstelle einen Pull Request oder öffne ein Issue für Vorschläge.

## 📄 Lizenz

Dieses Projekt ist privat und nicht für öffentliche Verwendung lizenziert.

## 👥 Credits

Entwickelt mit ❤️ für die Skylife-Community

---

**Version**: 1.0.0
**Letztes Update**: Januar 2026
**Minecraft Version**: 1.20+
**Status**: In aktiver Entwicklung