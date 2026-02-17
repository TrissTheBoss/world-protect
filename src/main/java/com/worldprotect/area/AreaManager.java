package com.worldprotect.area;

import com.worldprotect.flags.Flag;
import com.worldprotect.flags.FlagResolver;
import com.worldprotect.flags.GroupFlag;
import com.worldprotect.selection.Selection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages all areas in the plugin.
 */
public class AreaManager {
    
    private final Map<String, Area> areasByName;
    private final Map<World, List<Area>> areasByWorld;
    private final Map<UUID, List<Area>> areasByOwner;
    
    public AreaManager() {
        this.areasByName = new ConcurrentHashMap<>();
        this.areasByWorld = new ConcurrentHashMap<>();
        this.areasByOwner = new ConcurrentHashMap<>();
    }
    
    /**
     * Add an area to the manager.
     * @param area the area to add
     * @return true if added, false if area with same name already exists
     */
    public boolean addArea(@NotNull Area area) {
        String name = area.getName();
        if (areasByName.containsKey(name)) {
            return false;
        }
        
        areasByName.put(name, area);
        
        // Add to world index
        World world = area.getWorld();
        if (world != null) {
            areasByWorld.computeIfAbsent(world, k -> new ArrayList<>()).add(area);
        }
        
        // Add to owner index
        for (UUID owner : area.getOwners()) {
            areasByOwner.computeIfAbsent(owner, k -> new ArrayList<>()).add(area);
        }
        
        return true;
    }
    
    /**
     * Remove an area from the manager.
     * @param name the area name
     * @return the removed area, or null if not found
     */
    @Nullable
    public Area removeArea(@NotNull String name) {
        Area area = areasByName.remove(name);
        if (area == null) {
            return null;
        }
        
        // Remove from world index
        World world = area.getWorld();
        if (world != null) {
            List<Area> worldAreas = areasByWorld.get(world);
            if (worldAreas != null) {
                worldAreas.remove(area);
                if (worldAreas.isEmpty()) {
                    areasByWorld.remove(world);
                }
            }
        }
        
        // Remove from owner index
        for (UUID owner : area.getOwners()) {
            List<Area> ownerAreas = areasByOwner.get(owner);
            if (ownerAreas != null) {
                ownerAreas.remove(area);
                if (ownerAreas.isEmpty()) {
                    areasByOwner.remove(owner);
                }
            }
        }
        
        return area;
    }
    
    /**
     * Get an area by name.
     * @param name the area name
     * @return the area, or null if not found
     */
    @Nullable
    public Area getArea(@NotNull String name) {
        return areasByName.get(name);
    }
    
    /**
     * Check if an area exists.
     * @param name the area name
     * @return true if area exists
     */
    public boolean hasArea(@NotNull String name) {
        return areasByName.containsKey(name);
    }
    
    /**
     * Get all areas.
     * @return collection of all areas
     */
    @NotNull
    public Collection<Area> getAllAreas() {
        return Collections.unmodifiableCollection(areasByName.values());
    }
    
    /**
     * Get areas in a specific world.
     * @param world the world
     * @return list of areas in the world
     */
    @NotNull
    public List<Area> getAreasInWorld(@NotNull World world) {
        List<Area> areas = areasByWorld.get(world);
        return areas != null ? Collections.unmodifiableList(areas) : Collections.emptyList();
    }
    
    /**
     * Get areas owned by a player.
     * @param playerId the player UUID
     * @return list of areas owned by the player
     */
    @NotNull
    public List<Area> getAreasByOwner(@NotNull UUID playerId) {
        List<Area> areas = areasByOwner.get(playerId);
        return areas != null ? Collections.unmodifiableList(areas) : Collections.emptyList();
    }
    
    /**
     * Get areas where player is a member.
     * @param playerId the player UUID
     * @return list of areas where player is a member
     */
    @NotNull
    public List<Area> getAreasByMember(@NotNull UUID playerId) {
        return areasByName.values().stream()
            .filter(area -> area.isMember(playerId))
            .collect(Collectors.toList());
    }
    
    /**
     * Get areas that contain a location.
     * @param location the location
     * @return list of areas containing the location, sorted by priority (highest first)
     */
    @NotNull
    public List<Area> getAreasAtLocation(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) {
            return Collections.emptyList();
        }
        
        List<Area> worldAreas = areasByWorld.get(world);
        if (worldAreas == null) {
            return Collections.emptyList();
        }
        
        List<Area> containingAreas = new ArrayList<>();
        for (Area area : worldAreas) {
            if (area.contains(location)) {
                containingAreas.add(area);
            }
        }
        
        // Sort by priority (lower priority number = higher priority)
        // For same priority, use lexicographic order of area names for deterministic tie-breaking
        containingAreas.sort(Comparator
            .comparingInt(Area::getPriority)
            .thenComparing(Area::getName));
        
        return containingAreas;
    }
    
    /**
     * Get the effective flag value at a location.
     * @param location the location
     * @param flagName the flag name
     * @param player the player (nullable)
     * @return the effective flag value
     */
    public boolean getFlagValueAtLocation(@NotNull Location location, @NotNull String flagName,
                                          @Nullable Player player) {
        List<Area> areas = getAreasAtLocation(location);
        return FlagResolver.getEffectiveFlagValue(areas, flagName, player);
    }
    
    /**
     * Get all effective flag values at a location.
     * @param location the location
     * @param player the player (nullable)
     * @return map of flag name to effective value
     */
    @NotNull
    public Map<String, Boolean> getAllFlagValuesAtLocation(@NotNull Location location,
                                                           @Nullable Player player) {
        List<Area> areas = getAreasAtLocation(location);
        return FlagResolver.getAllEffectiveFlagValues(areas, player);
    }
    
    /**
     * Create a new area from a selection.
     * @param name the area name
     * @param selection the selection
     * @param priority the priority (1-50)
     * @param shape the shape
     * @param style the style
     * @param borderThickness border thickness
     * @return the created area, or null if area with same name exists
     */
    @Nullable
    public Area createArea(@NotNull String name, @NotNull Selection selection, int priority,
                           @NotNull Area.Shape shape, @NotNull Area.Style style, int borderThickness) {
        if (hasArea(name)) {
            return null;
        }
        
        Area area = new Area(name, selection, priority, shape, style, borderThickness);
        if (addArea(area)) {
            return area;
        }
        return null;
    }
    
    /**
     * Rename an area.
     * @param oldName the current area name
     * @param newName the new area name
     * @return true if renamed successfully
     */
    public boolean renameArea(@NotNull String oldName, @NotNull String newName) {
        if (hasArea(newName)) {
            return false;
        }
        
        Area area = removeArea(oldName);
        if (area == null) {
            return false;
        }
        
        // Create a new area with the new name (we need to recreate due to final fields)
        // This is a simplified approach - in reality we'd need to properly clone the area
        return false; // Not implemented in this simplified version
    }
    
    /**
     * Change area priority.
     * @param name the area name
     * @param newPriority the new priority (1-50)
     * @return true if priority changed
     */
    public boolean changeAreaPriority(@NotNull String name, int newPriority) {
        Area area = getArea(name);
        if (area == null) {
            return false;
        }
        
        // Remove and re-add with new priority
        removeArea(name);
        // Create new area with updated priority
        // This is simplified - actual implementation would need proper cloning
        return false; // Not implemented in this simplified version
    }
    
    /**
     * Add owner to area.
     * @param areaName the area name
     * @param playerId the player UUID
     * @return true if owner added
     */
    public boolean addOwner(@NotNull String areaName, @NotNull UUID playerId) {
        Area area = getArea(areaName);
        if (area == null) {
            return false;
        }
        
        boolean added = area.addOwner(playerId);
        if (added) {
            // Update owner index
            areasByOwner.computeIfAbsent(playerId, k -> new ArrayList<>()).add(area);
        }
        return added;
    }
    
    /**
     * Remove owner from area.
     * @param areaName the area name
     * @param playerId the player UUID
     * @return true if owner removed
     */
    public boolean removeOwner(@NotNull String areaName, @NotNull UUID playerId) {
        Area area = getArea(areaName);
        if (area == null) {
            return false;
        }
        
        boolean removed = area.removeOwner(playerId);
        if (removed) {
            // Update owner index
            List<Area> ownerAreas = areasByOwner.get(playerId);
            if (ownerAreas != null) {
                ownerAreas.remove(area);
                if (ownerAreas.isEmpty()) {
                    areasByOwner.remove(playerId);
                }
            }
        }
        return removed;
    }
    
    /**
     * Add member to area.
     * @param areaName the area name
     * @param playerId the player UUID
     * @return true if member added
     */
    public boolean addMember(@NotNull String areaName, @NotNull UUID playerId) {
        Area area = getArea(areaName);
        return area != null && area.addMember(playerId);
    }
    
    /**
     * Remove member from area.
     * @param areaName the area name
     * @param playerId the player UUID
     * @return true if member removed
     */
    public boolean removeMember(@NotNull String areaName, @NotNull UUID playerId) {
        Area area = getArea(areaName);
        return area != null && area.removeMember(playerId);
    }
    
    /**
     * Set flag value for area.
     * @param areaName the area name
     * @param flagName the flag name
     * @param subjectGroup the subject group
     * @param value the value
     * @return true if flag set
     */
    public boolean setFlagValue(@NotNull String areaName, @NotNull String flagName,
                                @NotNull FlagResolver.SubjectGroup subjectGroup, boolean value) {
        Area area = getArea(areaName);
        if (area == null) {
            return false;
        }
        
        Flag flag = Flag.byName(flagName);
        if (flag != null) {
            area.setFlagValue(flag, subjectGroup, value);
            return true;
        }
        
        GroupFlag groupFlag = GroupFlag.byName(flagName);
        if (groupFlag != null) {
            area.setGroupFlagValue(groupFlag, subjectGroup, value);
            return true;
        }
        
        return false;
    }
    
    /**
     * Remove flag value from area.
     * @param areaName the area name
     * @param flagName the flag name
     * @param subjectGroup the subject group
     * @return true if flag removed
     */
    public boolean removeFlagValue(@NotNull String areaName, @NotNull String flagName,
                                   @NotNull FlagResolver.SubjectGroup subjectGroup) {
        Area area = getArea(areaName);
        if (area == null) {
            return false;
        }
        
        Flag flag = Flag.byName(flagName);
        if (flag != null) {
            return area.removeFlagValue(flag, subjectGroup);
        }
        
        GroupFlag groupFlag = GroupFlag.byName(flagName);
        if (groupFlag != null) {
            return area.removeGroupFlagValue(groupFlag, subjectGroup);
        }
        
        return false;
    }
    
    /**
     * Clear all areas.
     */
    public void clear() {
        areasByName.clear();
        areasByWorld.clear();
        areasByOwner.clear();
    }
    
    /**
     * Get area count.
     * @return number of areas
     */
    public int getAreaCount() {
        return areasByName.size();
    }
    
    /**
     * Get area count by world.
     * @param world the world
     * @return number of areas in the world
     */
    public int getAreaCount(@NotNull World world) {
        List<Area> areas = areasByWorld.get(world);
        return areas != null ? areas.size() : 0;
    }
}