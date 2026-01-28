# Skylife Minigame Plugin - TODO List

## 🐛 Potenzielle Bugs & Fixes

### Kritisch

- [ ] **🔴 KITS WERDEN NICHT VERGEBEN**: `IngameState.kt:61` - KitManager.giveKitItems(player) fehlt komplett! Spieler starten ohne Kit-Items
- [ ] **Kit Selector Item nicht geschützt**: Kit Selector (Chest) kann gedroppt/bewegt werden - muss zu InventoryProtectionListener hinzugefügt werden
- [ ] **World Cleanup bei Server Crash**: Was passiert wenn Server abstürzt während Games laufen? Alte World-Ordner könnten nicht gelöscht werden
- [ ] **Player Inventory bei Disconnect**: Prüfen ob Inventory korrekt cleared wird wenn Spieler während Game disconnectet
- [ ] **Memory Leak Check**: MapManager.activeWorlds könnte bei Fehlern nie gecleart werden
- [ ] **Race Condition**: Mehrere Spieler joinen gleichzeitig → könnte maxPlayers überschreiten
- [ ] **Skill Items Duplication**: Prüfen ob Skills-Items gedupliziert werden können (z.B. durch Drop-Exploit)
- [ ] **Default Kit Selection**: Was passiert wenn Spieler kein Kit auswählt? Sollte Default-Kit geben

### Mittel

- [ ] **Spectator Mode Issues**: Spectators könnten Items aus Game World picken
- [ ] **Party System**: Was passiert wenn Party Leader disconnectet während Game läuft?
- [ ] **Guild Friendly Fire**: Edge Cases wenn Spieler während Game Guild beitritt/verlässt
- [ ] **Exit Door in InGame State**: Exit Door sollte nicht im InGame State funktionieren, nur in Lobby/End
- [ ] **Map Loading Timeout**: Keine Timeout-Behandlung wenn Map-Loading zu lange dauert

### Niedrig

- [ ] **Scoreboard Flicker**: Mögliches Flackern wenn zu oft upgedated wird
- [ ] **Message Placeholders**: Fehlende Validation ob alle Placeholders ersetzt wurden
- [ ] **Location World Check**: Prüfen ob World existiert bevor Location geladen wird

---

## ✨ Feature Ideen

### Gameplay Features

- [ ] **Achievements System**: Achievements für besondere Leistungen (z.B. "10 Kills in einem Game")
- [ ] **Cosmetics**: Partikel-Effekte, Trail-Effekte, Victory Animations
- [ ] **Shop System**: Items/Skills mit Points kaufen
- [ ] **Daily Quests**: Tägliche Aufgaben für Belohnungen
- [ ] **Seasons/Ranks**: Seasonales Ranking System mit Rewards
- [ ] **Custom Game Modes**: FFA, Teams, Solo vs Teams, etc.
- [ ] **Power-Ups**: Spawn-bare Items in der Map (Speed Boost, Strength, etc.)
- [ ] **Loot Chests**: Zufällige Chests in der Map mit Items
- [ ] **Border Shrink**: World Border der sich über Zeit verkleinert (Battle Royale Style)
- [ ] **Weather Events**: Zufällige Wetter-Events die Gameplay beeinflussen
- [ ] **Night/Day Cycle**: Dynamischer Tag/Nacht Wechsel während Game
- [ ] **Kill Streak Rewards**: Bonus Items/Effects für Kill-Streaks
- [ ] **Spectator Features**: Spectators können teleportieren zu Spielern, Gamemode wechseln, etc.

### Kit System (Aktuell nur 2 Kits!)

- [ ] **More Kits**: Mehr Kit-Variationen entwickeln (Tank, Mage, Assassin, Support, etc.)
- [ ] **Kit Balancing**: Kit Stats analysieren und balancen
- [ ] **Custom Kits**: Admin-Commands für Custom Kit Creation
- [ ] **Kit Unlocks**: Kits mit Level/Points freischalten
- [ ] **Kit Presets**: Multiple Kit-Loadouts speichern
- [ ] **Kit Statistics**: Track welche Kits am meisten gewählt/gewonnen werden
- [ ] **Random Kit Mode**: Gamemode wo jeder Random Kit bekommt
- [ ] **Kit Abilities**: Spezielle Abilities für Kits (Rechtsklick Chest = Special Ability)
- [ ] **Kit Rarity System**: Common, Rare, Epic, Legendary Kits

### Skills System

- [ ] **More Skills**: Mehr Skill-Variationen entwickeln
- [ ] **Skill Levels**: Skills upgraden mit XP
- [ ] **Skill Combos**: Bonus wenn bestimmte Skills kombiniert werden
- [ ] **Skill Presets**: Spieler können Skill-Sets speichern
- [ ] **Random Skill Mode**: Gamemode wo jeder Random Skills bekommt
- [ ] **Skill Statistics**: Track welche Skills am häufigsten gewählt werden
- [ ] **Skill Cooldown Display**: Actionbar/Bossbar für Cooldowns

### Social Features

- [ ] **Friends System**: Freunde hinzufügen und deren Status sehen
- [ ] **Guild Wars**: Guild vs Guild Matches
- [ ] **Guild Levels**: Guild XP und Level System
- [ ] **Guild Bank**: Gemeinsame Item/Point Storage
- [ ] **Clan Tags**: Farbige Clan Tags im Chat
- [ ] **Player Profiles**: Detaillierte Profile mit Stats, Achievements, etc.
- [ ] **Leaderboards**: Top 10 für verschiedene Stats

### Administrative Features

- [ ] **Web Dashboard**: Web-Interface für Server Management
- [ ] **Advanced Stats**: Detaillierte Statistiken exportieren (Excel, CSV)
- [ ] **Replay System**: Games aufzeichnen und später ansehen
- [ ] **Report System**: Spieler melden können
- [ ] **Moderation Tools**: Mute, Tempban, etc.
- [ ] **Game Templates**: Verschiedene Game-Presets (Small, Medium, Large)
- [ ] **Auto-Balance**: Automatisches Team-Balancing
- [ ] **Spectator Broadcasting**: Spectators können Spieler-POV wählen

### Quality of Life

- [ ] **Tutorial System**: Neuen Spielern das Spiel erklären
- [ ] **Language System**: Multi-Language Support (EN, DE, etc.)
- [ ] **Hotbar Customization**: Spieler können Hotbar-Layout speichern
- [ ] **Sound Settings**: Spieler können Sounds an/ausschalten
- [ ] **Particle Settings**: Performance-Mode für schwache PCs
- [ ] **Auto-Join**: Automatisch nächstem verfügbarem Game beitreten
- [ ] **Quick Join by Player**: Einem bestimmten Spieler folgen
- [ ] **Favorite Maps**: Spieler können Lieblings-Maps markieren
- [ ] **Map Voting**: Spieler voten für nächste Map

### Performance & Optimization

- [ ] **Async World Loading**: Worlds asynchron laden
- [ ] **Database Connection Pool**: Connection Pooling für bessere Performance
- [ ] **Cache System**: Häufig verwendete Daten cachen
- [ ] **Batch Operations**: Bulk-Operations für DB Queries
- [ ] **Lazy Loading**: Nur laden was benötigt wird
- [ ] **Memory Monitoring**: Automatische Memory-Überwachung

### Integration & API

- [ ] **PlaceholderAPI Support**: Integration für andere Plugins
- [ ] **Vault Integration**: Economy Plugin Support
- [ ] **Discord Integration**: Discord Bot für Server Status
- [ ] **MySQL Support**: Alternative zu SQLite
- [ ] **Redis Support**: Für Multi-Server Setup
- [ ] **BungeeCord/Velocity Support**: Multi-Server Network
- [ ] **REST API**: HTTP API für externe Tools

---

## 🔧 Code Quality & Refactoring

### Testing

- [ ] **Unit Tests**: Tests für kritische Komponenten schreiben
- [ ] **Integration Tests**: Game-Flow Ende-zu-Ende testen
- [ ] **Load Testing**: Performance unter Last testen
- [ ] **Mock Tests**: Database/Bukkit API mocken

### Documentation

- [ ] **JavaDoc/KDoc**: Alle public APIs dokumentieren
- [ ] **Wiki**: Umfassendes Wiki für Setup & Features
- [ ] **API Documentation**: REST API dokumentieren (wenn implementiert)
- [ ] **Architecture Docs**: System-Architektur dokumentieren
- [ ] **Contributing Guide**: Guidelines für Contributors

### Refactoring

- [ ] **Error Handling**: Bessere Error Messages & Exception Handling
- [ ] **Logging System**: Strukturiertes Logging (DEBUG, INFO, WARN, ERROR)
- [ ] **Config Validation**: Startup-Validation für alle Configs
- [ ] **Dependency Injection**: Bessere Dependency Management
- [ ] **Event System**: Custom Event System für Plugin Extensions
- [ ] **State Machine**: Besseres State Management für Games

### Security

- [ ] **Input Validation**: Alle User Inputs validieren
- [ ] **SQL Injection Prevention**: Prepared Statements überall
- [ ] **Permission System**: Granulares Permission System
- [ ] **Rate Limiting**: Spam-Protection für Commands
- [ ] **Audit Logging**: Admin Actions loggen

---

## 📋 Known Issues

### Aktuell offen

- **🔴 KRITISCH**: Kits werden beim Game Start nicht vergeben! (IngameState.kt:61)
- **🔴 KRITISCH**: Kit Selector Item (Chest) nicht geschützt vor Drop/Move
- Exit Door funktioniert in allen States (sollte nur in Lobby/End sein)
- Keine Prüfung ob Hub gesetzt ist bevor Spieler beitreten
- Skills könnten in Lobby/Hub aktiviert werden
- GameOverview Compass könnte in Game verwendet werden
- Kein Default-Kit wenn Spieler keins auswählt
- Nur 2 Kits vorhanden (sehr limitiert)

### Geplante Fixes

- [ ] **🔴 SOFORT FIX**: KitManager.giveKitItems(player) in IngameState.start() hinzufügen
- [ ] **🔴 SOFORT FIX**: Kit Selector zu InventoryProtectionListener hinzufügen
- [ ] Default-Kit Logik implementieren (z.B. erstes Kit in Liste)
- [ ] Exit Door nur in Lobby/End/Hub aktivieren
- [ ] Hub-Check beim Server Start mit Warning
- [ ] Skills nur im InGame State aktivierbar machen
- [ ] Inventory Items State-basiert aktivieren/deaktivieren
- [ ] Mehr Kits erstellen (mindestens 5-6 verschiedene)

---

## 📊 Prioritäten

### High Priority (Kritisch für Stabilität)

1. World Cleanup bei Crash
2. Race Condition beim Join
3. Exit Door State-Check
4. Memory Leak Prevention

### Medium Priority (Wichtig für UX)

1. Tutorial System
2. Better Error Messages
3. Hub-Check beim Start
4. Achievements System

### Low Priority (Nice to have)

1. Cosmetics
2. Web Dashboard
3. Discord Integration
4. More Skills

---

## 🎯 Roadmap

### Version 1.2

- [ ] Bug Fixes (Kritisch)
- [ ] Exit Door State Fix
- [ ] Better Error Handling
- [ ] Tutorial System

### Version 1.3

- [ ] Achievements System
- [ ] Daily Quests
- [ ] Shop System
- [ ] More Skills

### Version 2.0

- [ ] Teams Mode
- [ ] Border Shrink
- [ ] Loot Chests
- [ ] Season System

### Version 3.0

- [ ] BungeeCord Support
- [ ] Multi-Server
- [ ] Web Dashboard
- [ ] REST API

---

## 📝 Notes

- Alle Features sollten konfigurierbar sein (config.json)
- Performance ist wichtig - keine Features die Server lahmlegen
- Backward Compatibility beachten bei DB Schema Changes
- Alle neuen Features testen bevor Release
- Community Feedback einholen für große Features
