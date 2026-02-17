# Areas and Selections

Complete guide to area creation, selection tools, and spatial management in World Protect.

## 🏗️ Area Concepts

### What is an Area?
An **area** is a protected 3D volume in a Minecraft world with defined boundaries, protection flags, and ownership/membership. Areas can overlap, have different priorities, and contain owners/members with special permissions.

### Area Properties
- **Name**: Unique identifier within a world
- **World**: Minecraft world the area exists in
- **Bounds**: Minimum and maximum coordinates (cuboid)
- **Priority**: Integer (1-50) for overlap resolution (1 = highest)
- **Shape**: SQUARE, CIRCLE, TRIANGLE, or HEXAGON
- **Style**: FULL or BORDER (with border thickness)
- **Flags**: Protection rules applied within the area
- **Owners**: Players with full control over the area
- **Members**: Players with special access permissions

### Area Shapes
1. **SQUARE**: Axis-aligned rectangular area
2. **CIRCLE**: Circular area with center and radius
3. **TRIANGLE**: Triangular area defined by three points
4. **HEXAGON**: Hexagonal area (regular polygon)
5. **POLYGON**: Free-draw polygon area with unlimited points

### Area Styles
1. **FULL**: Entire shape volume is protected
2. **BORDER**: Only the border area is protected (configurable thickness)

## 🎯 Selection System

### Selection Types
World Protect supports two selection modes:

1. **Point-Based Selection (Wand Mode)**: Using a wand with left/right clicks
   - Left-click adds a point
   - Right-click adds another point
   - Need at least 2 points for basic area
   - Selection automatically completes after 2 points
   - Switch to this mode: `/wp selection mode wand`

2. **Free-Draw Selection (Draw/Points Mode)**: Create polygon with unlimited points
   - Left-click or right-click to add points at clicked blocks
   - Unlimited points (minimum 3 for valid polygon, maximum 360)
   - Polygon automatically closes (last point connects to first)
   - Real-time particle visualization of polygon edges
   - Must manually complete with `/wp selection finish` command
   - Switch to this mode: `/wp selection mode draw` or `/wp selection mode points`

### Selection Wand
The selection wand is the primary tool for defining area boundaries.

**Default Wand**: Wooden Axe (configurable)
**Usage**:
- **Left-click**: Add point at clicked block
- **Right-click**: Add another point
- **Shift + Right-click**: Remove last point

### Selection Visualization
- **Particles**: Shows selection points and boundaries
- **Outline**: Visual wireframe of selected area
- **Volume Display**: Shows block count in chat
- **Point Count**: Shows number of points added

## 📐 Area Creation

### Basic Creation
```bash
# 1. Use selection wand to define points
#    (Left-click and right-click blocks)

# 2. Create area with default values
/wp create spawn_protection
```

### Area Creation Process
1. **Make Selection**: Use wand to define area boundaries
2. **Check Completion**: Ensure selection has enough points
3. **Create Area**: Use `/wp create <name>` command
4. **Set Flags**: Configure protection rules as needed

### Area Validation
Before creation, World Protect validates:
1. **Name uniqueness**: No duplicate area names in the world
2. **Selection completeness**: Enough points for the shape
3. **World consistency**: All points in same world
4. **Permission check**: User has create permission

## 🗺️ Area Management

### Listing Areas
```bash
# List all areas
/wp list

# Area list shows:
# - Area name
# - Owners
# - World
# - Priority
# - Volume
```

### Area Information
```bash
# Detailed area information
/wp info spawn

# Shows:
# - World and priority
# - Shape and style
# - Volume and creation date
# - Owners and members
# - Boundary coordinates
```

### Area Deletion
```bash
# Delete area (owners or admins only)
/wp delete old_area
```

### Location-Based Area Inspection
```bash
# Show all areas at your current location
/wp here

# Show effective flag values at your location
/wp here flags

# Example output:
# === Areas at your location ===
# Location: (100, 64, 200) in world
# ✓ spawn - Priority: 1, Shape: SQUARE, Style: FULL (Effective)
#   arena - Priority: 5, Shape: CIRCLE, Style: BORDER
# 
# Multiple areas overlap. Highest priority area (spawn) determines effective flags.
```

## 🔄 Area Overlaps and Priority

### Priority System
When areas overlap, flags are evaluated based on priority:
- **Higher priority** areas override lower priority areas (1 = highest, 50 = lowest)
- **Same priority**: First area in alphabetical order wins
- **Atomic flags** override group flags
- **Owner/Member** status affects flag resolution

### Overlap Examples
```
Area A (priority 10): block-break = false
Area B (priority 5): block-break = true
Result at overlap: block-break = false (Area A wins - higher priority)
```

### Subject Groups
Flags can have different values for different subject groups:
- **OWNER**: Area owners have full control
- **MEMBER**: Area members have special permissions
- **NONMEMBER**: Players who are not owners or members

## 👥 Owner and Member Management

### Owner Permissions
- **Full control**: Create, delete, modify area
- **Flag management**: Set protection flags
- **Member management**: Add/remove members
- **Ownership transfer**: Add/remove other owners

### Member Benefits
Members can have different flag values than nonmembers:
- Owners may bypass block protection
- Members may have special interaction permissions
- Nonmembers follow standard protection rules

### Automatic Ownership
- Area creator automatically becomes owner
- Owners can add other owners
- Owners can add/remove members

## 📊 Spatial Indexing

### How Area Lookup Works
World Protect uses efficient spatial indexing for fast area lookup:

1. **World-based Indexing**: Areas indexed by world
2. **Boundary Checking**: Simple AABB (axis-aligned bounding box) checks
3. **Priority Sorting**: Areas sorted by priority for overlap resolution
4. **Caching**: Frequently accessed locations cached

### Performance Optimization
- **World Separation**: Areas indexed per world for faster lookups
- **Priority Sorting**: Pre-sorted by priority for quick resolution
- **Simple Geometry**: Efficient boundary calculations
- **Minimal Overhead**: Lightweight data structures

## 🛠️ Advanced Features

### Shape Support
Areas support multiple geometric shapes:
- **SQUARE**: Simple rectangular areas (most common)
- **CIRCLE**: Circular protection zones
- **TRIANGLE**: Triangular areas for complex layouts
- **HEXAGON**: Hexagonal areas for grid-based systems
- **POLYGON**: Free-draw polygons with unlimited points

### Polygon Geometry
Polygon areas use advanced computational geometry:
- **Point-in-Polygon**: Ray-casting algorithm for accurate containment checks
- **Distance-to-Boundary**: Calculates distance to nearest polygon edge for BORDER style
- **Automatic Closing**: Last point automatically connects to first point
- **Unlimited Points**: No hard limit (soft maximum of 360 for performance)
- **2D Projection**: Polygon defined in XZ plane, full Y height
- **Coordinate System**: Polygon points use block center coordinates (e.g., block at (10, 64, 20) stores point (10.5, 64, 20.5)) for consistent boundary alignment

### Border Style
BORDER style creates protected borders only:
- **Border Thickness**: Configurable width of protected border
- **Interior Access**: Players can access interior freely
- **Perimeter Protection**: Only border area is protected
- **Polygon Borders**: Distance-to-edge calculation for accurate border zones

### Group Flags
Toggle multiple flags at once with group flags:
- `environment-all`: All environment flags
- `explosions-all`: All explosion flags
- `interactions-all`: All interaction flags
- `build-all`: All building flags
- `mob-all`: All mob-related flags

## ⚠️ Common Issues and Solutions

### Selection Problems
**Issue**: "Not enough points in selection"
**Solution**: Add more points with wand (2+ for point-based, 3+ for free-draw)

**Issue**: "Points in different worlds"
**Solution**: Ensure all selection points are in same world

**Issue**: "Selection too large"
**Solution**: Consider creating multiple smaller areas

### Area Conflicts
**Issue**: "Area name already exists"
**Solution**: Choose a different name or delete existing area

**Issue**: "Cannot modify area"
**Solution**: Check ownership or get `worldprotect.admin` permission

**Issue**: "Flags not working in overlap"
**Solution**: Check area priorities and subject groups

### Performance Issues
**Issue**: "Many areas causing lag"
**Solution**: Combine small areas when possible, use appropriate priorities

**Issue**: "Slow area lookup"
**Solution**: Ensure areas are properly sized and distributed

**Issue**: "High memory usage"
**Solution**: Use simpler shapes and fewer flags when possible

## 📈 Best Practices

### Area Design
1. **Keep areas simple**: Use SQUARE shape when possible
2. **Use appropriate priorities**: Reserve high priorities (1-10) for critical areas
3. **Group related areas**: Use similar naming conventions
4. **Document complex setups**: Note area purposes and special rules

### Performance Optimization
1. **Limit area count**: Combine small areas when possible
2. **Use simple shapes**: SQUARE is most efficient
3. **Optimize priorities**: Use meaningful priority values
4. **Use group flags**: Apply multiple flags at once

### Maintenance
1. **Regular cleanup**: Delete unused areas
2. **Monitor performance**: Check area counts and sizes
3. **Update configurations**: Keep priorities optimized
4. **Document changes**: Note area modifications

## 🔄 Auto-Generated Documentation

*Note: This document is auto-generated from the Area and Selection classes. When new features are added, the documentation will be updated automatically.*

**Last Generated**: 2026-02-17  
**Area Shapes**: 5  
**Area Styles**: 2  
**Selection Modes**: 2

---

*For technical implementation details, see `ARCHITECTURE.md`. For command reference, see `COMMANDS_AND_PERMISSIONS.md`. For flag reference, see `FLAGS.md`.*
