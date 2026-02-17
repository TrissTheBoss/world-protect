# Flag Reference

Complete reference for all protection flags available in World Protect.

## 📋 Flag Overview

Flags are the core protection mechanism in World Protect. Each flag controls a specific aspect of gameplay within a protected region.

### Flag Value Types
- **allow**: Action is allowed
- **deny**: Action is denied
- **default**: Use the flag's default value

**Note**: For backward compatibility, `true`/`false`, `on`/`off`, and `1`/`0` are also accepted but will be displayed as `allow`/`deny`.

### Subject Groups
Flags can have different values for different subject groups:
- **OWNER**: Area owners have full control
- **MEMBER**: Area members have special permissions
- **NONMEMBER**: Players who are not owners or members

### Flag Priority
When multiple areas overlap, flags are evaluated in this order:
1. Areas sorted by priority (1 = highest, 50 = lowest)
2. Atomic flag values override group flag values
3. Higher priority areas override lower priority areas
4. For ties, the first area in alphabetical order wins

### Group Flags
Group flags toggle multiple atomic flags at once:
- `environment-all`: All environment flags
- `explosions-all`: All explosion flags
- `interactions-all`: All interaction flags
- `build-all`: All building flags
- `mob-all`: All mob-related flags

## 🏗️ Block Protection Flags

### `block-break`
**Description**: Controls breaking of blocks.

**Values**: allow, deny, default  
**Default**: allow  
**Category**: Blocks & Containers  
**Events**: BlockBreakEvent

**Examples**:
```bash
/wp flags spawn block-break deny
/wp flags build_area block-break allow
```

### `block-place`
**Description**: Controls placing of blocks.

**Values**: allow, deny, default  
**Default**: allow  
**Category**: Blocks & Containers  
**Events**: BlockPlaceEvent

### `use`
**Description**: Controls use of doors, buttons, levers, etc.

**Values**: allow, deny, default  
**Default**: allow  
**Category**: Blocks & Containers  
**Events**: PlayerInteractEvent

### `container-access`
**Description**: Controls access to containers (chests, furnaces, etc.).

**Values**: allow, deny, default  
**Default**: allow  
**Category**: Blocks & Containers  
**Events**: InventoryOpenEvent

## ⚔️ Player Combat Flags

### `pvp`
**Description**: Controls player vs player combat.

**Values**: allow, deny, default  
**Default**: deny  
**Category**: Player Combat  
**Events**: EntityDamageByEntityEvent

**Examples**:
```bash
/wp flags arena pvp allow
/wp flags spawn pvp deny
```

## 🧟 Mob & Entity Flags

### `mob-damage-players`
**Description**: Controls whether mobs can damage players.

**Values**: allow, deny, default  
**Default**: allow  
**Category**: Mobs & Entities  
**Events**: EntityDamageByEntityEvent

### `mob-spawning`
**Description**: Controls mob spawning.

**Values**: true, false, default  
**Default**: true  
**Category**: Mobs & Entities  
**Events**: CreatureSpawnEvent

## 💥 Explosion Flags

### `tnt`
**Description**: Controls TNT explosions.

**Values**: true, false, default  
**Default**: true  
**Category**: Explosions  
**Events**: EntityExplodeEvent

### `creeper-explosion`
**Description**: Controls creeper explosions.

**Values**: true, false, default  
**Default**: true  
**Category**: Explosions  
**Events**: EntityExplodeEvent

### `ghast-fireball`
**Description**: Controls ghast fireball explosions.

**Values**: true, false, default  
**Default**: true  
**Category**: Explosions  
**Events**: EntityExplodeEvent

## 🌍 Environment Flags

### `fire-spread`
**Description**: Controls fire spread.

**Values**: true, false, default  
**Default**: true  
**Category**: Environment  
**Events**: BlockSpreadEvent

### `lava-flow`
**Description**: Controls lava flow.

**Values**: true, false, default  
**Default**: true  
**Category**: Environment  
**Events**: BlockFromToEvent

### `water-flow`
**Description**: Controls water flow.

**Values**: true, false, default  
**Default**: true  
**Category**: Environment  
**Events**: BlockFromToEvent

### `lightning`
**Description**: Controls lightning strikes.

**Values**: true, false, default  
**Default**: true  
**Category**: Environment  
**Events**: LightningStrikeEvent

### `snow-fall`
**Description**: Controls snow accumulation.

**Values**: true, false, default  
**Default**: true  
**Category**: Environment  
**Events**: BlockFormEvent

### `snow-melt`
**Description**: Controls snow melting.

**Values**: true, false, default  
**Default**: true  
**Category**: Environment  
**Events**: BlockFadeEvent

### `ice-form`
**Description**: Controls ice formation.

**Values**: true, false, default  
**Default**: true  
**Category**: Environment  
**Events**: BlockFormEvent

### `ice-melt`
**Description**: Controls ice melting.

**Values**: true, false, default  
**Default**: true  
**Category**: Environment  
**Events**: BlockFadeEvent

## 📦 Item & Vehicle Flags

### `item-drop`
**Description**: Controls item dropping.

**Values**: true, false, default  
**Default**: true  
**Category**: Items & Vehicles  
**Events**: PlayerDropItemEvent

### `item-pickup`
**Description**: Controls item pickup.

**Values**: true, false, default  
**Default**: true  
**Category**: Items & Vehicles  
**Events**: EntityPickupItemEvent

### `vehicle-place`
**Description**: Controls vehicle placement.

**Values**: true, false, default  
**Default**: true  
**Category**: Items & Vehicles  
**Events**: VehicleCreateEvent

### `vehicle-destroy`
**Description**: Controls vehicle destruction.

**Values**: true, false, default  
**Default**: true  
**Category**: Items & Vehicles  
**Events**: VehicleDestroyEvent

## 📊 Flag Categories Summary

| Category | Flag Count | Description |
|----------|------------|-------------|
| Player Combat | 1 | Player vs player combat |
| Mobs & Entities | 2 | Mob damage and spawning |
| Explosions | 3 | TNT, creeper, and ghast explosions |
| Environment | 8 | Fire, fluids, weather, and ice effects |
| Blocks & Containers | 4 | Block breaking, placing, and interactions |
| Items & Vehicles | 4 | Item dropping/pickup and vehicle handling |

## 🎯 Common Flag Combinations

### Spawn Protection
```bash
/wp flags spawn block-break false
/wp flags spawn block-place false
/wp flags spawn pvp false
/wp flags spawn mob-spawning false
/wp flags spawn explosions-all false
/wp flags spawn environment-all false
```

### Build Area
```bash
/wp flags build_area block-break true
/wp flags build_area block-place true
/wp flags build_area pvp false
/wp flags build_area build-all true
```

### PvP Arena
```bash
/wp flags arena pvp true
/wp flags arena block-break false
/wp flags arena block-place false
/wp flags arena explosions-all true
```

### Shop Area
```bash
/wp flags shops block-break false
/wp flags shops block-place false
/wp flags shops container-access true
/wp flags shops pvp false
/wp flags shops interactions-all true
```

## ⚙️ Flag Configuration

### Default Flag Values
Flags can have default values configured in `config.yml`:
```yaml
flags:
  defaults:
    block-break: true
    block-place: true
    pvp: false
    mob-spawning: true
    fire-spread: true
```

### Subject Group Defaults
Configure different defaults for owners, members, and nonmembers:
```yaml
flags:
  subject-defaults:
    owner:
      block-break: true
      block-place: true
    member:
      block-break: true
      block-place: true
    nonmember:
      block-break: false
      block-place: false
```

### Group Flag Configuration
Customize which flags are included in group flags:
```yaml
flags:
  groups:
    environment-all:
      - fire-spread
      - lava-flow
      - water-flow
      - lightning
      - snow-fall
      - snow-melt
      - ice-form
      - ice-melt
    build-all:
      - block-break
      - block-place
```

## 🔄 Auto-Generated Documentation

*Note: This document is auto-generated from the Flag and GroupFlag enums in the codebase. When new flags are added, the documentation will be updated automatically.*

**Last Generated**: 2026-02-17  
**Total Atomic Flags**: 22  
**Total Group Flags**: 5  
**Categories**: 6

### Flag Metadata Structure
```java
public enum Flag {
    PVP("pvp", "Controls player vs player combat", false),
    BLOCK_BREAK("block-break", "Controls breaking of blocks", true),
    // ... other flags
}
```

### Group Flag Structure
```java
public enum GroupFlag {
    ENVIRONMENT_ALL("environment-all", "All environment flags",
        Arrays.asList(Flag.FIRE_SPREAD, Flag.LAVA_FLOW, Flag.WATER_FLOW, Flag.LIGHTNING,
            Flag.SNOW_FALL, Flag.SNOW_MELT, Flag.ICE_FORM, Flag.ICE_MELT)),
    // ... other group flags
}
```

### Documentation Sync Process
1. Read `Flag.java` and `GroupFlag.java` enums
2. Extract flag metadata (name, description, default, category)
3. Generate Markdown tables and examples
4. Update this file while preserving manual content

---

*For flag implementation details and event handling, see `ARCHITECTURE.md` and `DEVELOPMENT_NOTES.md`.*
