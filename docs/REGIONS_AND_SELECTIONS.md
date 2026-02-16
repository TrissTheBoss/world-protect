# Regions and Selections

Complete guide to region creation, selection tools, and spatial management in World Protect.

## 🏗️ Region Concepts

### What is a Region?
A **region** is a protected 3D volume in a Minecraft world with defined boundaries and protection rules (flags). Regions can overlap, have different priorities, and contain members with special permissions.

### Region Properties
- **Name**: Unique identifier (alphanumeric, underscores)
- **World**: Minecraft world the region exists in
- **Bounds**: Minimum and maximum coordinates (cuboid)
- **Priority**: Integer (0-1000) for overlap resolution
- **Flags**: Protection rules applied within the region
- **Members**: Players with special access permissions
- **Owner**: Creator of the region (optional)

### Region Types
1. **Cuboid Regions**: Standard rectangular regions (most common)
2. **Polygonal Regions**: Complex shapes (future feature)
3. **Global Regions**: World-wide protection (special case)
4. **Parent/Child Regions**: Hierarchical regions with inheritance

## 🎯 Selection System

### Selection Wand
The selection wand is the primary tool for defining region boundaries.

**Default Wand**: Golden Axe (configurable)
**Usage**:
- **Right-click**: Set position 1 (primary corner)
- **Left-click**: Set position 2 (opposite corner)
- **Shift + Right-click**: Clear selection

### Visual Feedback
- **Particles**: Shows selection boundaries
- **Outline**: Visual wireframe of selected area
- **Volume Display**: Shows block count in chat
- **Color Coding**: Different colors for pos1 and pos2

### Selection Commands
```bash
# Manual position setting
/wp pos1          # Set position 1 at current location
/wp pos2          # Set position 2 at current location

# Selection manipulation
/wp expand <direction> <amount>    # Expand selection
/wp contract <direction> <amount>  # Contract selection
/wp shift <direction> <amount>     # Move selection
/wp outset <amount>                # Expand all directions
/wp inset <amount>                 # Contract all directions

# Selection info
/wp sel info      # Show selection information
/wp sel clear     # Clear current selection
/wp sel chunk     # Select current chunk
```

### Selection Directions
- **Cardinal**: north, south, east, west
- **Vertical**: up, down
- **Combined**: horizontal, vertical, all

## 📐 Region Creation

### Basic Creation
```bash
# 1. Get selection wand
/wp wand

# 2. Select area (right-click and left-click blocks)

# 3. Create region
/wp create spawn_protection
```

### Advanced Creation Options
```bash
# Create with specific priority
/wp create shop_area -p 50

# Create with initial flags
/wp create arena -f "pvp=allow,block-break=deny"

# Create from current chunk
/wp create chunk_region --chunk

# Create with custom boundaries
/wp create custom_region --min 0,64,0 --max 100,128,100
```

### Region Validation
Before creation, World Protect validates:
1. **Name uniqueness**: No duplicate region names
2. **Boundary validity**: Proper min/max coordinates
3. **Volume limits**: Within configured maximum/minimum
4. **World consistency**: Both positions in same world
5. **Permission check**: User has create permission

## 🗺️ Region Management

### Listing Regions
```bash
# List all regions
/wp list

# List regions in specific world
/wp list world_nether

# List with filters
/wp list --owner "Notch"
/wp list --priority 100
/wp list --volume 1000

# Detailed list
/wp list --verbose
```

### Region Information
```bash
# Basic info
/wp info spawn

# Detailed info with flags
/wp info spawn --flags

# Info with members
/wp info spawn --members

# Info with boundaries
/wp info spawn --bounds
```

### Region Modification
```bash
# Redefine boundaries from current selection
/wp redefine spawn

# Change region priority
/wp priority spawn 100

# Rename region
/wp rename spawn new_spawn

# Clone region
/wp clone spawn spawn_copy
```

### Region Deletion
```bash
# Delete region
/wp delete old_region

# Force delete (bypass checks)
/wp delete region --force

# Delete all regions in world
/wp delete --world world_nether --all
```

## 🔄 Region Overlaps and Priority

### Priority System
When regions overlap, flags are evaluated based on priority:
- **Higher priority** regions override lower priority regions
- **Same priority**: Most restrictive flag wins (deny > allow)
- **Priority range**: 0 (lowest) to 1000 (highest)

### Overlap Examples
```
Region A (priority 50): block-break = deny
Region B (priority 100): block-break = allow
Result at overlap: block-break = allow (Region B wins)
```

### Inheritance System
Regions can inherit flags from parent regions:
```bash
# Create parent region
/wp create parent -f "block-break=deny"

# Create child region with inheritance
/wp create child --parent parent -f "pvp=deny"

# Child inherits block-break=deny from parent
# Child adds its own pvp=deny flag
```

## 👥 Member Management

### Member Permissions
- **Owner**: Full control (create, delete, modify, add members)
- **Member**: Special access (bypass some flags)
- **Guest**: Limited access (view only)

### Member Commands
```bash
# Add member
/wp member add spawn Notch
/wp member add shop Alex owner

# Remove member
/wp member remove spawn Notch

# List members
/wp member list spawn

# Set member permission
/wp member set spawn Alex member
```

### Member Benefits
Members can bypass certain flags:
- `block-break=members`: Only members can break blocks
- `entry=members`: Only members can enter
- `container-access=members`: Only members access containers

## 🎨 Visual Tools

### Boundary Visualization
```bash
# Show region boundaries
/wp visualize spawn

# Custom visualization
/wp visualize spawn --particle REDSTONE --color GREEN --interval 10

# Hide boundaries
/wp visualize spawn --hide

# Visualize all regions in world
/wp visualize --world world_nether
```

### Selection Visualization
```bash
# Show selection particles
/wp sel visualize

# Change particle type
/wp sel visualize --particle VILLAGER_HAPPY

# Adjust particle density
/wp sel visualize --density 5
```

### Map Integration
```bash
# Show regions on dynmap (if installed)
/wp map show spawn

# Hide regions on map
/wp map hide spawn

# Update map markers
/wp map update
```

## 📊 Spatial Indexing

### How Region Lookup Works
World Protect uses spatial indexing for fast region lookup:

1. **Grid Index**: World divided into configurable grid cells
2. **Region Registration**: Regions registered in all overlapping cells
3. **Location Query**: Lookup checks only relevant grid cells
4. **Result Cache**: Frequently accessed locations cached

### Performance Optimization
- **Grid Size**: Configurable cell size (default: 16 chunks)
- **Cache Size**: LRU cache for lookup results
- **Async Processing**: Offload heavy spatial queries
- **Lazy Loading**: Regions loaded on-demand

### Index Statistics
```bash
# Show spatial index stats
/wp debug index

# Rebuild spatial index
/wp debug rebuild-index

# Test lookup performance
/wp debug benchmark 1000
```

## 🛠️ Advanced Features

### Region Shapes
```bash
# Create cylindrical region
/wp create cylinder_region --shape cylinder --radius 10 --height 20

# Create spherical region
/wp create sphere_region --shape sphere --radius 15

# Create polygonal region
/wp create poly_region --shape poly --points "0,0, 10,0, 10,10, 0,10"
```

### Temporary Regions
```bash
# Create temporary region (expires after time)
/wp create temp_region --duration 1h

# Create event region (auto-deletes after event)
/wp create event_region --expire-on-empty
```

### Region Templates
```bash
# Save region as template
/wp template save spawn protection_template

# Create region from template
/wp create new_spawn --template protection_template

# List available templates
/wp template list
```

### Import/Export
```bash
# Export regions to file
/wp export regions.json
/wp export regions.yml

# Import regions from file
/wp import regions.json
/wp import regions.yml --merge

# Export specific regions
/wp export --region spawn --region shop
```

## ⚠️ Common Issues and Solutions

### Selection Problems
**Issue**: "Positions are in different worlds"
**Solution**: Ensure both positions are in the same world

**Issue**: "Selection too large"
**Solution**: Check `max-region-volume` in config, reduce selection size

**Issue**: "No selection made"
**Solution**: Use `/wp pos1` and `/wp pos2` or get wand with `/wp wand`

### Region Conflicts
**Issue**: "Region name already exists"
**Solution**: Choose a different name or delete existing region

**Issue**: "Cannot modify region"
**Solution**: Check ownership or get `worldprotect.admin` permission

**Issue**: "Flags not working in overlap"
**Solution**: Check region priorities with `/wp info <region>`

### Performance Issues
**Issue**: "Lag when many regions exist"
**Solution**: Increase grid cell size, disable boundary visualization

**Issue**: "Slow region lookup"
**Solution**: Rebuild spatial index with `/wp debug rebuild-index`

**Issue**: "High memory usage"
**Solution**: Reduce cache sizes, disable unused features

## 📈 Best Practices

### Region Design
1. **Keep regions simple**: Use cuboids when possible
2. **Use appropriate priorities**: Reserve high priorities for critical areas
3. **Group related regions**: Use similar naming conventions
4. **Document regions**: Add comments or notes for complex setups

### Performance Optimization
1. **Limit region count**: Combine small regions when possible
2. **Use appropriate grid size**: Larger worlds need larger grid cells
3. **Disable visualization**: In production for better performance
4. **Use caching**: Enable and tune cache settings

### Maintenance
1. **Regular cleanup**: Delete unused regions
2. **Backup regions**: Export before major changes
3. **Monitor performance**: Use `/wp debug` commands
4. **Update configurations**: Keep configs optimized for your server

## 🔄 Auto-Generated Documentation

*Note: The selection and region command sections are auto-generated from the command registry. When new selection commands are added, run the documentation sync process.*

**Last Generated**: 2024-01-01  
**Selection Commands**: 12  
**Region Commands**: 18  
**Member Commands**: 6

---

*For technical implementation details, see `ARCHITECTURE.md`. For command reference, see `COMMANDS_AND_PERMISSIONS.md`.*