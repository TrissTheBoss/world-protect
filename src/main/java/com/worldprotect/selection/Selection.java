package com.worldprotect.selection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a player's selection of an area in the world.
 * Can be point-based (using a wand) or free-draw.
 */
public class Selection implements ConfigurationSerializable {
    
    private final String name;
    private final UUID ownerId;
    private final String worldName;
    private final List<Location> points;
    private final SelectionType type;
    private final long createdAt;
    
    /**
     * Type of selection.
     */
    public enum SelectionType {
        POINT_BASED,    // Using wand with left/right clicks
        FREE_DRAW       // Player movement creates polygon
    }
    
    /**
     * Create a new selection.
     * @param name the selection name
     * @param ownerId the owner's UUID
     * @param world the world
     * @param type the selection type
     */
    public Selection(@NotNull String name, @NotNull UUID ownerId, @NotNull World world, 
                     @NotNull SelectionType type) {
        this.name = name;
        this.ownerId = ownerId;
        this.worldName = world.getName();
        this.points = new ArrayList<>();
        this.type = type;
        this.createdAt = System.currentTimeMillis();
    }
    
    /**
     * Create a selection from serialized data.
     * @param data the serialized data
     */
    @SuppressWarnings("unchecked")
    public Selection(@NotNull Map<String, Object> data) {
        this.name = (String) data.get("name");
        this.ownerId = UUID.fromString((String) data.get("ownerId"));
        this.worldName = (String) data.get("worldName");
        this.type = SelectionType.valueOf((String) data.get("type"));
        this.createdAt = (Long) data.get("createdAt");
        
        this.points = new ArrayList<>();
        List<Map<String, Object>> pointData = (List<Map<String, Object>>) data.get("points");
        if (pointData != null) {
            for (Map<String, Object> point : pointData) {
                World world = Bukkit.getWorld((String) point.get("world"));
                if (world != null && world.getName().equals(worldName)) {
                    Location loc = new Location(
                        world,
                        ((Number) point.get("x")).doubleValue(),
                        ((Number) point.get("y")).doubleValue(),
                        ((Number) point.get("z")).doubleValue()
                    );
                    points.add(loc);
                }
            }
        }
    }
    
    /**
     * Get the selection name.
     * @return the name
     */
    @NotNull
    public String getName() {
        return name;
    }
    
    /**
     * Get the owner's UUID.
     * @return the owner UUID
     */
    @NotNull
    public UUID getOwnerId() {
        return ownerId;
    }
    
    /**
     * Get the world name.
     * @return the world name
     */
    @NotNull
    public String getWorldName() {
        return worldName;
    }
    
    /**
     * Get the world.
     * @return the world, or null if not loaded
     */
    @Nullable
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }
    
    /**
     * Get the selection points.
     * @return immutable list of points
     */
    @NotNull
    public List<Location> getPoints() {
        return Collections.unmodifiableList(points);
    }
    
    /**
     * Get the selection type.
     * @return the selection type
     */
    @NotNull
    public SelectionType getType() {
        return type;
    }
    
    /**
     * Get creation timestamp.
     * @return creation timestamp in milliseconds
     */
    public long getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Add a point to the selection.
     * @param location the location to add
     * @return true if added, false if world doesn't match or maximum points reached
     */
    public boolean addPoint(@NotNull Location location) {
        if (!location.getWorld().getName().equals(worldName)) {
            return false;
        }
        // Maximum 360 points for polygon selections to prevent abuse
        if (type == SelectionType.FREE_DRAW && points.size() >= 360) {
            return false;
        }
        points.add(location.clone());
        return true;
    }
    
    /**
     * Remove the last point.
     * @return true if removed, false if no points
     */
    public boolean removeLastPoint() {
        if (points.isEmpty()) {
            return false;
        }
        points.remove(points.size() - 1);
        return true;
    }
    
    /**
     * Clear all points.
     */
    public void clearPoints() {
        points.clear();
    }
    
    /**
     * Get the number of points.
     * @return point count
     */
    public int getPointCount() {
        return points.size();
    }
    
    private boolean manuallyCompleted = false;
    
    /**
     * Check if the selection is complete (has enough points for its type).
     * @return true if complete
     */
    public boolean isComplete() {
        if (manuallyCompleted) {
            return true;
        }
        switch (type) {
            case POINT_BASED:
                return points.size() >= 2; // Need at least 2 points for area
            case FREE_DRAW:
                return false; // FREE_DRAW selections require manual completion via finish command
            default:
                return false;
        }
    }
    
    /**
     * Manually mark this selection as complete.
     * Used for FREE_DRAW selections that require explicit completion.
     */
    public void setComplete() {
        if (type == SelectionType.FREE_DRAW && points.size() >= 3) {
            manuallyCompleted = true;
        }
    }
    
    /**
     * Check if selection can be manually completed (has enough points).
     * @return true if can be completed
     */
    public boolean canBeCompleted() {
        return type == SelectionType.FREE_DRAW && points.size() >= 3;
    }
    
    /**
     * Get the minimum bounds of the selection.
     * @return min location, or null if insufficient points
     */
    @Nullable
    public Location getMinBounds() {
        if (points.isEmpty()) {
            return null;
        }
        
        Location first = points.get(0);
        double minX = first.getX();
        double minY = first.getY();
        double minZ = first.getZ();
        
        for (Location point : points) {
            minX = Math.min(minX, point.getX());
            minY = Math.min(minY, point.getY());
            minZ = Math.min(minZ, point.getZ());
        }
        
        return new Location(first.getWorld(), minX, minY, minZ);
    }
    
    /**
     * Get the maximum bounds of the selection.
     * @return max location, or null if insufficient points
     */
    @Nullable
    public Location getMaxBounds() {
        if (points.isEmpty()) {
            return null;
        }
        
        Location first = points.get(0);
        double maxX = first.getX();
        double maxY = first.getY();
        double maxZ = first.getZ();
        
        for (Location point : points) {
            maxX = Math.max(maxX, point.getX());
            maxY = Math.max(maxY, point.getY());
            maxZ = Math.max(maxZ, point.getZ());
        }
        
        return new Location(first.getWorld(), maxX, maxY, maxZ);
    }
    
    /**
     * Get the center of the selection.
     * @return center location, or null if insufficient points
     */
    @Nullable
    public Location getCenter() {
        Location min = getMinBounds();
        Location max = getMaxBounds();
        
        if (min == null || max == null) {
            return null;
        }
        
        return new Location(
            min.getWorld(),
            (min.getX() + max.getX()) / 2,
            (min.getY() + max.getY()) / 2,
            (min.getZ() + max.getZ()) / 2
        );
    }
    
    /**
     * Calculate the volume of the selection.
     * @return volume in blocks, or 0 if insufficient points
     */
    public double getVolume() {
        Location min = getMinBounds();
        Location max = getMaxBounds();
        
        if (min == null || max == null) {
            return 0;
        }
        
        double width = Math.abs(max.getX() - min.getX()) + 1;
        double height = Math.abs(max.getY() - min.getY()) + 1;
        double depth = Math.abs(max.getZ() - min.getZ()) + 1;
        
        return width * height * depth;
    }
    
    /**
     * Check if a location is within the selection bounds.
     * @param location the location to check
     * @return true if within bounds
     */
    public boolean contains(@NotNull Location location) {
        if (!location.getWorld().getName().equals(worldName)) {
            return false;
        }
        
        Location min = getMinBounds();
        Location max = getMaxBounds();
        
        if (min == null || max == null) {
            return false;
        }
        
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        
        return x >= min.getX() && x <= max.getX() &&
               y >= min.getY() && y <= max.getY() &&
               z >= min.getZ() && z <= max.getZ();
    }
    
    /**
     * Check if the owner is online.
     * @return true if owner is online
     */
    public boolean isOwnerOnline() {
        Player player = Bukkit.getPlayer(ownerId);
        return player != null && player.isOnline();
    }
    
    /**
     * Get the owner as a Player.
     * @return the player, or null if offline
     */
    @Nullable
    public Player getOwner() {
        return Bukkit.getPlayer(ownerId);
    }
    
    // ConfigurationSerializable implementation
    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("ownerId", ownerId.toString());
        data.put("worldName", worldName);
        data.put("type", type.name());
        data.put("createdAt", createdAt);
        
        List<Map<String, Object>> pointData = new ArrayList<>();
        for (Location point : points) {
            Map<String, Object> pointMap = new HashMap<>();
            pointMap.put("world", point.getWorld().getName());
            pointMap.put("x", point.getX());
            pointMap.put("y", point.getY());
            pointMap.put("z", point.getZ());
            pointData.add(pointMap);
        }
        data.put("points", pointData);
        
        return data;
    }
    
    /**
     * Deserialize a Selection from configuration data.
     * @param data the configuration data
     * @return the Selection
     */
    @NotNull
    public static Selection deserialize(@NotNull Map<String, Object> data) {
        return new Selection(data);
    }
}