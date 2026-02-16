# Flag Reference

Complete reference for all protection flags available in World Protect.

## 📋 Flag Overview

Flags are the core protection mechanism in World Protect. Each flag controls a specific aspect of gameplay within a protected region.

### Flag Value Types
- **allow**: Action is permitted
- **deny**: Action is prevented
- **passthrough**: Inherit from parent region or global default
- **members**: Only region members can perform action
- **non-members**: Only non-members are affected
- **custom**: Flag-specific custom values

### Flag Priority
When multiple regions overlap, flags are evaluated in this order:
1. Region with highest priority
2. Most specific flag value (non-passthrough)
3. Global defaults

## 🏗️ Block Protection Flags

### `block-break`
**Description**: Controls breaking of blocks.

**Values**: allow, deny, members, non-members  
**Default**: deny  
**Category**: Block  
**Events**: BlockBreakEvent

**Examples**:
```bash
/wp flag spawn block-break deny
/wp flag build_area block-break members
```

### `block-place`
**Description**: Controls placing of blocks.

**Values**: allow, deny, members, non-members  
**Default**: deny  
**Category**: Block  
**Events**: BlockPlaceEvent

### `block-interact`
**Description**: Controls interactions with blocks (buttons, levers, doors).

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Block  
**Events**: PlayerInteractEvent

### `use`
**Description**: General use of items and blocks.

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Block  
**Events**: PlayerInteractEvent

### `container-access`
**Description**: Access to containers (chests, furnaces, etc.).

**Values**: allow, deny, members, non-members  
**Default**: deny  
**Category**: Block  
**Events**: InventoryOpenEvent

## ⚔️ Combat & PvP Flags

### `pvp`
**Description**: Player vs player combat.

**Values**: allow, deny  
**Default**: deny  
**Category**: Combat  
**Events**: EntityDamageByEntityEvent

**Examples**:
```bash
/wp flag arena pvp allow
/wp flag spawn pvp deny
```

### `damage-players`
**Description**: Any damage to players.

**Values**: allow, deny  
**Default**: deny  
**Category**: Combat  
**Events**: EntityDamageEvent

### `damage-animals`
**Description**: Damage to animals.

**Values**: allow, deny  
**Default**: deny  
**Category**: Combat  
**Events**: EntityDamageEvent

### `damage-monsters`
**Description**: Damage to monsters.

**Values**: allow, deny  
**Default**: allow  
**Category**: Combat  
**Events**: EntityDamageEvent

### `damage-ambient`
**Description**: Damage to ambient creatures (bats, etc.).

**Values**: allow, deny  
**Default**: deny  
**Category**: Combat  
**Events**: EntityDamageEvent

## 🧟 Entity Flags

### `mob-spawning`
**Description**: Natural mob spawning.

**Values**: allow, deny  
**Default**: deny  
**Category**: Entity  
**Events**: CreatureSpawnEvent

### `animal-spawning`
**Description**: Natural animal spawning.

**Values**: allow, deny  
**Default**: allow  
**Category**: Entity  
**Events**: CreatureSpawnEvent

### `item-drop`
**Description**: Dropping of items.

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Entity  
**Events**: PlayerDropItemEvent

### `item-pickup`
**Description**: Picking up items.

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Entity  
**Events**: EntityPickupItemEvent

### `vehicle-place`
**Description**: Placing vehicles (boats, minecarts).

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Entity  
**Events**: VehicleCreateEvent

### `vehicle-destroy`
**Description**: Destroying vehicles.

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Entity  
**Events**: VehicleDestroyEvent

## 🚶 Movement Flags

### `entry`
**Description**: Entering the region.

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Movement  
**Events**: PlayerMoveEvent

**Examples**:
```bash
/wp flag vip_area entry members
/wp flag restricted entry deny
```

### `exit`
**Description**: Leaving the region.

**Values**: allow, deny  
**Default**: allow  
**Category**: Movement  
**Events**: PlayerMoveEvent

### `teleport`
**Description**: Teleporting into or within the region.

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Movement  
**Events**: PlayerTeleportEvent

### `enderpearl`
**Description**: Using ender pearls.

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Movement  
**Events**: PlayerTeleportEvent

### `flight`
**Description**: Flying (creative mode or with elytra).

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Movement  
**Events**: PlayerToggleFlightEvent

## 🔥 Environmental Flags

### `fire-spread`
**Description**: Fire spreading.

**Values**: allow, deny  
**Default**: deny  
**Category**: Environment  
**Events**: BlockSpreadEvent

### `lava-fire`
**Description**: Fire caused by lava.

**Values**: allow, deny  
**Default**: deny  
**Category**: Environment  
**Events**: BlockIgniteEvent

### `lightning`
**Description**: Lightning strikes.

**Values**: allow, deny  
**Default**: deny  
**Category**: Environment  
**Events**: LightningStrikeEvent

### `ice-form`
**Description**: Ice formation.

**Values**: allow, deny  
**Default**: allow  
**Category**: Environment  
**Events**: BlockFormEvent

### `ice-melt`
**Description**: Ice melting.

**Values**: allow, deny  
**Default**: allow  
**Category**: Environment  
**Events**: BlockFadeEvent

### `snow-fall`
**Description**: Snow accumulation.

**Values**: allow, deny  
**Default**: allow  
**Category**: Environment  
**Events**: BlockFormEvent

### `snow-melt`
**Description**: Snow melting.

**Values**: allow, deny  
**Default**: allow  
**Category**: Environment  
**Events**: BlockFadeEvent

## 🎮 Gameplay Flags

### `sleep`
**Description**: Sleeping in beds.

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Gameplay  
**Events**: PlayerBedEnterEvent

### `respawn-anchor`
**Description**: Using respawn anchors.

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Gameplay  
**Events**: PlayerRespawnAnchorEvent

### `chorus-fruit`
**Description**: Using chorus fruit.

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Gameplay  
**Events**: PlayerTeleportEvent

### `elytra`
**Description**: Using elytra.

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Gameplay  
**Events**: EntityToggleGlideEvent

### `trident`
**Description**: Using tridents (loyalty, riptide).

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Gameplay  
**Events**: ProjectileLaunchEvent

## 🎨 Visual & Effect Flags

### `potion-effects`
**Description**: Applying potion effects.

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Effects  
**Events**: PotionSplashEvent, AreaEffectCloudApplyEvent

### `explosions`
**Description**: Explosions (TNT, creepers, beds in nether).

**Values**: allow, deny  
**Default**: deny  
**Category**: Effects  
**Events**: EntityExplodeEvent, BlockExplodeEvent

### `fireworks`
**Description**: Using fireworks.

**Values**: allow, deny, members, non-members  
**Default**: allow  
**Category**: Effects  
**Events**: FireworkExplodeEvent

### `particles`
**Description**: Particle effects.

**Values**: allow, deny  
**Default**: allow  
**Category**: Effects  
**Events**: Various

## 🏗️ Redstone & Mechanics

### `redstone`
**Description**: Redstone activation.

**Values**: allow, deny  
**Default**: allow  
**Category**: Mechanics  
**Events**: BlockRedstoneEvent

### `pistons`
**Description**: Piston extension/retraction.

**Values**: allow, deny  
**Default**: allow  
**Category**: Mechanics  
**Events**: BlockPistonExtendEvent, BlockPistonRetractEvent

### `dispensers`
**Description**: Dispenser activation.

**Values**: allow, deny  
**Default**: allow  
**Category**: Mechanics  
**Events**: BlockDispenseEvent

### `droppers`
**Description**: Dropper activation.

**Values**: allow, deny  
**Default**: allow  
**Category**: Mechanics  
**Events**: BlockDispenseEvent

### `hoppers`
**Description**: Hopper item transfer.

**Values**: allow, deny  
**Default**: allow  
**Category**: Mechanics  
**Events**: InventoryMoveItemEvent

## 📊 Flag Categories Summary

| Category | Flag Count | Description |
|----------|------------|-------------|
| Block | 5 | Block interactions and modifications |
| Combat | 5 | PvP and damage-related flags |
| Entity | 6 | Mob spawning and entity interactions |
| Movement | 5 | Player movement and teleportation |
| Environment | 7 | Environmental effects and changes |
| Gameplay | 5 | Game mechanics and items |
| Effects | 4 | Visual and sound effects |
| Mechanics | 5 | Redstone and mechanical devices |

## 🎯 Common Flag Combinations

### Spawn Protection
```bash
/wp flag spawn block-break deny
/wp flag spawn block-place deny
/wp flag spawn pvp deny
/wp flag spawn mob-spawning deny
/wp flag spawn explosions deny
/wp flag spawn fire-spread deny
```

### Build Area
```bash
/wp flag build_area block-break members
/wp flag build_area block-place members
/wp flag build_area pvp deny
/wp flag build_area entry allow
```

### PvP Arena
```bash
/wp flag arena pvp allow
/wp flag arena block-break deny
/wp flag arena block-place deny
/wp flag arena explosions allow
/wp flag arena entry allow
```

### Shop Area
```bash
/wp flag shops block-break deny
/wp flag shops block-place deny
/wp flag shops container-access members
/wp flag shops pvp deny
/wp flag shops entry allow
```

## ⚙️ Flag Configuration

### Default Flag Values
Flags can have default values configured in `config.yml`:
```yaml
flags:
  defaults:
    block-break: deny
    block-place: deny
    pvp: deny
    entry: allow
    mob-spawning: deny
```

### Flag Aliases
Create shorter aliases for commonly used flags:
```yaml
flags:
  aliases:
    build: block-break,block-place
    protect: block-break,block-place,pvp
    safe: block-break,block-place,pvp,mob-spawning
```

### Flag Inheritance
Flags support inheritance through the `passthrough` value:
```bash
# Region inherits from global defaults
/wp flag region block-break passthrough

# Child region inherits from parent
/wp flag child_region pvp passthrough
```

## 🔄 Auto-Generated Documentation

*Note: This document is partially auto-generated from the Flag enum in the codebase. When new flags are added, run the documentation sync process to update this file.*

**Last Generated**: 2024-01-01  
**Total Flags**: 42  
**Categories**: 8

### Flag Metadata Structure
```java
public enum Flag {
    BLOCK_BREAK(
        "block-break",
        "Controls breaking of blocks",
        FlagValue.DENY,
        FlagCategory.BLOCK,
        BlockBreakEvent.class
    ),
    // ... other flags
}
```

### Documentation Sync Process
1. Read `Flag.java` enum
2. Extract flag metadata (name, description, default, category)
3. Generate Markdown tables and examples
4. Update this file while preserving manual content

---

*For flag implementation details and event handling, see `ARCHITECTURE.md` and `DEVELOPMENT_NOTES.md`.*