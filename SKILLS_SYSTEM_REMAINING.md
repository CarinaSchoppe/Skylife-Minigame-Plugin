# Skills System - Remaining Implementation Tasks

## Status: 70% Complete

### ✅ Completed

1. **Skill Enum** - All 14 skills defined with descriptions and materials
2. **SkillsManager** - Selection, activation, deactivation, database persistence
3. **SkillsGui** - Interactive GUI with glass panes, enchanted selected items
4. **SkillsGuiListener** - Handles clicks, prevents item removal
5. **SkillsListCommand** - Updated to open new GUI
6. **PlayerSkillsItemListener** - Opens GUI when clicking skills item in lobby
7. **Database Schema** - PlayerSkills table created

### 🚧 Remaining Tasks

#### 1. Add Skills Item to Lobby (HIGH PRIORITY)

**File to modify**: `LobbyState.kt` line 46-60

```kotlin
override fun playerJoined(player: Player) {
    player.inventory.clear()

    // Kit selector
    val kitSelector = ItemStack(Material.CHEST)
    val meta = kitSelector.itemMeta
    meta.displayName(Messages.parse(KitSelectorListener.KIT_SELECTOR_ITEM_NAME))
    kitSelector.itemMeta = meta
    player.inventory.setItem(0, kitSelector)

    // ADD SKILLS ITEM HERE:
    player.inventory.setItem(1, SkillsGui.createSkillsMenuItem())

    // Check countdown...
}
```

#### 2. Activate Skills on Game Start (HIGH PRIORITY)

**File to modify**: `IngameState.kt`

In the `start()` method, add:

```kotlin
override fun start() {
    game.livingPlayers.forEach { player ->
        SkillsManager.activateSkills(player)
        SkillEffectsManager.applySkillEffects(player)
    }
    // ... existing code
}
```

#### 3. Deactivate Skills on Game End (HIGH PRIORITY)

**File to modify**: `EndState.kt` and `GameCluster.stopGame()`

```kotlin
game.getAllPlayers().forEach { player ->
    SkillsManager.deactivateSkills(player)
    SkillEffectsManager.removeSkillEffects(player)
}
```

#### 4. Implement Skill Effects Manager (CRITICAL)

**New file**: `SkillEffectsManager.kt`

This manager applies actual skill effects:

```kotlin
object SkillEffectsManager {
    fun applySkillEffects(player: Player) {
        val skills = SkillsManager.getActiveSkills(player)

        skills.forEach { skill ->
            when (skill) {
                Skill.JUMBO -> {
                    player.foodLevel = 20
                    player.saturation = 20f
                }
                Skill.REGENERATOR -> {
                    player.addPotionEffect(
                        PotionEffect(
                            PotionEffectType.REGENERATION,
                            Int.MAX_VALUE,
                            0,
                            false,
                            false
                        )
                    )
                }
                Skill.ABSORBER -> {
                    player.addPotionEffect(
                        PotionEffect(
                            PotionEffectType.ABSORPTION,
                            Int.MAX_VALUE,
                            0,
                            false,
                            false
                        )
                    )
                }
                // ... implement all other skills
            }
        }
    }

    fun removeSkillEffects(player: Player) {
        player.activePotionEffects.forEach {
            player.removePotionEffect(it.type)
        }
    }
}
```

#### 5. Implement Skill Effect Listeners (CRITICAL)

**New files needed:**

1. **`SkillJumboListener.kt`** - Prevents hunger loss

```kotlin
@EventHandler
fun onFoodLevelChange(event: FoodLevelChangeEvent) {
    val player = event.entity as? Player ?: return
    if (SkillsManager.hasSkillActive(player, Skill.JUMBO)) {
        event.isCancelled = true
        player.foodLevel = 20
    }
}
```

2. **`SkillFeatherfallListener.kt`** - Reduces fall damage

```kotlin
@EventHandler
fun onPlayerDamage(event: EntityDamageEvent) {
    if (event.cause != EntityDamageEvent.DamageCause.FALL) return
    val player = event.entity as? Player ?: return
    if (SkillsManager.hasSkillActive(player, Skill.FEATHERFALL)) {
        event.damage = event.damage * 0.5 // 50% reduction
    }
}
```

3. **`SkillPassiveItemsTask.kt`** - Repeating task for items

```kotlin
// Bukkit.getScheduler().runTaskTimer for:
// - SNOW_SPAMMER: Give snowball every 10 seconds
// - ENDERMASTER: Give ender pearl every 10 seconds
// - WITCH: Give random potion every 10 seconds
```

4. **`SkillStartingItemsManager.kt`** - Give starting items

```kotlin
// Called when game starts:
// - WOLFLORD: 4-6 stacks of bones
// - BUILDER: 64 blocks
// - SWORDMASTER: Iron sword + shield
// - BOWMASTER: Bow + 64 arrows
```

5. **`SkillInvisibleStalkerListener.kt`** - Invisibility when sneaking

```kotlin
@EventHandler
fun onPlayerToggleSneak(event: PlayerToggleSneakEvent) {
    if (!event.isSneaking) return
    if (SkillsManager.hasSkillActive(event.player, Skill.INVISIBLE_STALKER)) {
        event.player.addPotionEffect(
            PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0)
        )
    }
}
```

#### 6. Register Everything in Skylife.kt (HIGH PRIORITY)

```kotlin
// In initialize():
com.carinaschoppe.skylife.skills.SkillsManager.loadSkills()

// Register listeners:
pluginManager.registerEvents(SkillsGuiListener(), this)
pluginManager.registerEvents(PlayerSkillsItemListener(), this)
pluginManager.registerEvents(SkillJumboListener(), this)
pluginManager.registerEvents(SkillFeatherfallListener(), this)
pluginManager.registerEvents(SkillInvisibleStalkerListener(), this)
// ... etc

// Start repeating tasks:
SkillPassiveItemsTask.start(this)
```

#### 7. Update Database Connector (HIGH PRIORITY)

```kotlin
transaction {
    SchemaUtils.create(StatsPlayers, Guilds, GuildMembers, PlayerSkills)
}
```

## Implementation Priority

1. **MUST DO NOW**:
    - Add PlayerSkills to database creation
    - Add skills item to lobby inventory
    - Activate/deactivate skills on game start/end
    - Register new listeners

2. **CRITICAL FOR FUNCTIONALITY**:
    - Implement SkillEffectsManager
    - Implement all skill effect listeners
    - Implement passive item tasks
    - Implement starting items

3. **NICE TO HAVE**:
    - Skill usage statistics
    - Admin commands to manage skills
    - Skill cooldowns/limits

## Testing Checklist

- [ ] Skills item appears in lobby
- [ ] GUI opens when clicking skills item
- [ ] Can select/unselect skills
- [ ] Max 2 skills enforced
- [ ] Selected skills shown with enchantment glow
- [ ] Skills persist across sessions (database)
- [ ] Skills activate when game starts
- [ ] Skills deactivate when game ends
- [ ] Each skill effect works correctly
- [ ] No conflicts between skills
- [ ] Passive items given on schedule
- [ ] Starting items given at game start

## Files Created So Far

1. ✅ `Skill.kt` - Skill definitions
2. ✅ `SkillsManager.kt` - Skill management
3. ✅ `SkillsGui.kt` - GUI implementation
4. ✅ `SkillsGuiListener.kt` - GUI event handling
5. ✅ `PlayerSkillsItemListener.kt` - Lobby item handling
6. ✅ Updated `SkillsListCommand.kt`

## Files Still Needed

7. ⏳ `SkillEffectsManager.kt`
8. ⏳ `SkillJumboListener.kt`
9. ⏳ `SkillFeatherfallListener.kt`
10. ⏳ `SkillInvisibleStalkerListener.kt`
11. ⏳ `SkillPassiveItemsTask.kt`
12. ⏳ `SkillStartingItemsManager.kt`

## Estimated Remaining Work

- **30% remaining** (~500-700 lines of code)
- **Time needed**: 2-3 hours
- **Complexity**: Medium (mostly straightforward listeners and effect application)
