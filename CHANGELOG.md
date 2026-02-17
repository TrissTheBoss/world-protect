# Changelog

All notable changes to World Protect will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- `/wp selection finish` command for completing polygon selections
- "points" as an alias for "draw" mode in `/wp selection mode` command
- Maximum point limit of 360 for polygon selections to prevent abuse

### Changed
- Polygon selection now allows unlimited points (previously limited to 3)
- Areas created from multi-point selections now automatically detect POLYGON shape
- Updated tab completion for `/wp selection mode` with options: wand, draw, points
- Enhanced selection messages for polygon mode

### Fixed
- **Point limit bug**: Selection system now allows unlimited points in draw/polygon mode
- **Shape recognition bug**: Areas created from multi-point selections now correctly show as POLYGON shape instead of SQUARE
- **Command syntax**: `/wp selection mode` now suggests `draw` and `points` as valid options in tab completion
- Selection completion logic for FREE_DRAW mode now requires manual `/wp selection finish` command
- **Polygon boundary offset bug**: Polygon areas now protect the exact blocks you select, not one block inward
  - Selection points now use block center coordinates (e.g., block at (10, 64, 20) stores point (10.5, 64, 20.5))
  - Point-in-polygon algorithm uses consistent coordinate system for accurate boundary checks
  - Backward compatible: Existing polygon areas with integer coordinates are automatically converted to block centers
  - AABB bounds adjusted to account for block center coordinates

## [0.2.0] - 2026-02-17

### Added
- **Draw-mode polygon selection** with unlimited points and automatic closing
- **Polygon shape** for areas with advanced computational geometry
- **Point-in-polygon algorithm** using ray-casting for accurate containment checks
- **Distance-to-boundary algorithm** for polygon BORDER style with accurate border zones
- **Selection mode switching**: `/wp selection mode draw` and `/wp selection mode wand`
- **Real-time particle visualization** of polygon edges during selection
- **PolygonGeometry utility class** with robust geometry algorithms
- **Enhanced area creation** with shape, style, border thickness, and priority parameters
- **Updated documentation** with comprehensive polygon selection guide

### Changed
- **Enhanced `/wp create` command** to accept shape, style, border, and priority parameters
- **Updated selection system** to support both wand mode (2-point) and draw mode (unlimited points)
- **Improved geometry utilities** with polygon-specific calculations
- **Enhanced area validation** for polygon shapes (minimum 3 points required)
- **Updated all documentation** to reflect new polygon features

### Fixed
- **Geometry calculations** for all shape types
- **Area serialization** to properly save polygon points
- **Command validation** for polygon shape requirements

## [0.1.3] - 2026-02-17

### Added
- Complete implementation of `/wp here` command family
- `/wp circle` command for creating circular areas
- Full shape implementations (square, circle, triangle, hexagon)
- Full style implementations (full, border)
- Area visualization with `/wp here visualize`

### Changed
- Selection wand now properly resets volume calculations
- Improved tab completion for all commands
- Enhanced error messages with suggestions

### Fixed
- Selection wand volume calculation bug
- Flag value parsing for allow/deny terminology
- Build compilation issues

## [0.1.2] - 2026-02-17

### Added
- `/wp here` command to show all areas at player's current location
- `/wp here flags` command to show effective flag values at location
- Tab completion for area names, flag names, and allow/deny values
- Improved error messages with suggestions for invalid area/flag names

### Changed
- **BREAKING**: Flag values now use `allow`/`deny` terminology instead of `true`/`false`
  - Backward compatible: `true`/`false`, `on`/`off`, `1`/`0` still accepted but displayed as `allow`/`deny`
  - Updated all documentation to reflect new terminology
- Selection wand volume calculation now correctly resets when starting new selections
- Priority system for overlapping areas now fully implemented and documented
- All shape and style implementations completed (square, circle, triangle, hexagon, full/border)

### Fixed
- Selection wand volume bug where previous selections affected new ones
- Flag command parsing to properly handle allow/deny values
- Documentation alignment with actual implementation
- Build compilation errors and warnings

## [0.1.1] - 2026-02-17

### Fixed
- `/flags` command now correctly applies flag changes when used with 3 arguments (`/wp flags <area> <flag> <value>`)
- Previously, `/wp flags area flag value` would only list flags instead of setting them
- Tab completion now works for both `/wp flags` and `/wp flag` commands
- Updated command documentation to reflect that `/wp flags` can both list and set flags

## [1.0.0] - Planned Release

### Planned Features
- Region creation, deletion, and management
- Comprehensive flag system (50+ protection flags)
- Visual selection tools with wand
- Priority-based region overlap handling
- SQLite and MySQL storage support
- Full Folia compatibility
- Permission system integration
- Configuration GUI (web-based optional)
- API for developers
- Import/export functionality
- WorldEdit compatibility layer

---

## Versioning Scheme

- **MAJOR** version for incompatible API changes
- **MINOR** version for new functionality in a backward compatible manner  
- **PATCH** version for backward compatible bug fixes

## Release Process

1. Create release branch from `main`
2. Update version in `gradle.properties` and `plugin.yml`
3. Run full test suite
4. Update CHANGELOG.md with release notes
5. Create GitHub release with tag `vX.Y.Z`
6. Merge release branch back to `main`
7. Deploy to GitHub Packages/Maven repository

## Backward Compatibility

World Protect follows these compatibility guidelines:

- **API**: Breaking changes only in MAJOR versions
- **Configuration**: Automatic migration for minor changes
- **Database**: Migration scripts provided for schema changes
- **Commands**: Deprecated commands remain functional for one major version

## Upgrade Notes

When upgrading between versions:

1. Always backup your `plugins/WorldProtect/` directory
2. Check the changelog for breaking changes
3. Test in a staging environment first
4. Follow any migration instructions provided

---

*This changelog format was inspired by [Keep a Changelog](https://keepachangelog.com/).*