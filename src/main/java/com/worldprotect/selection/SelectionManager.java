package com.worldprotect.selection;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player selections.
 */
public class SelectionManager {
    
    private final Map<UUID, Selection> playerSelections;
    
    public SelectionManager() {
        this.playerSelections = new ConcurrentHashMap<>();
    }
    
    /**
     * Start a new selection for a player.
     * @param player the player
     * @param name the selection name
     * @param world the world
     * @param type the selection type
     * @return the new selection, or null if player already has a selection
     */
    @Nullable
    public Selection startSelection(@NotNull Player player, @NotNull String name,
                                    @NotNull World world, @NotNull Selection.SelectionType type) {
        UUID playerId = player.getUniqueId();
        if (playerSelections.containsKey(playerId)) {
            return null;
        }
        
        Selection selection = new Selection(name, playerId, world, type);
        playerSelections.put(playerId, selection);
        return selection;
    }
    
    /**
     * Get a player's current selection.
     * @param player the player
     * @return the selection, or null if player has no active selection
     */
    @Nullable
    public Selection getSelection(@NotNull Player player) {
        return playerSelections.get(player.getUniqueId());
    }
    
    /**
     * Check if a player has an active selection.
     * @param player the player
     * @return true if player has an active selection
     */
    public boolean hasSelection(@NotNull Player player) {
        return playerSelections.containsKey(player.getUniqueId());
    }
    
    /**
     * Add a point to the player's selection.
     * @param player the player
     * @param location the location to add
     * @return true if point added, false if player has no selection or world doesn't match
     */
    public boolean addPoint(@NotNull Player player, @NotNull Location location) {
        Selection selection = getSelection(player);
        if (selection == null) {
            return false;
        }
        
        return selection.addPoint(location);
    }
    
    /**
     * Remove the last point from the player's selection.
     * @param player the player
     * @return true if point removed, false if player has no selection or no points
     */
    public boolean removeLastPoint(@NotNull Player player) {
        Selection selection = getSelection(player);
        if (selection == null) {
            return false;
        }
        
        return selection.removeLastPoint();
    }
    
    /**
     * Clear all points from the player's selection.
     * @param player the player
     * @return true if cleared, false if player has no selection
     */
    public boolean clearPoints(@NotNull Player player) {
        Selection selection = getSelection(player);
        if (selection == null) {
            return false;
        }
        
        selection.clearPoints();
        return true;
    }
    
    /**
     * Complete the player's selection.
     * @param player the player
     * @return the completed selection, or null if player has no selection or selection is incomplete
     */
    @Nullable
    public Selection completeSelection(@NotNull Player player) {
        Selection selection = getSelection(player);
        if (selection == null || !selection.isComplete()) {
            return null;
        }
        
        return selection;
    }
    
    /**
     * Manually complete a FREE_DRAW selection.
     * @param player the player
     * @return true if selection was completed, false if player has no selection or selection cannot be completed
     */
    public boolean finishSelection(@NotNull Player player) {
        Selection selection = getSelection(player);
        if (selection == null) {
            return false;
        }
        
        if (selection.canBeCompleted()) {
            selection.setComplete();
            return true;
        }
        
        return false;
    }
    
    /**
     * Cancel the player's selection.
     * @param player the player
     * @return the cancelled selection, or null if player has no selection
     */
    @Nullable
    public Selection cancelSelection(@NotNull Player player) {
        return playerSelections.remove(player.getUniqueId());
    }
    
    /**
     * Get the number of points in the player's selection.
     * @param player the player
     * @return point count, or -1 if player has no selection
     */
    public int getPointCount(@NotNull Player player) {
        Selection selection = getSelection(player);
        return selection != null ? selection.getPointCount() : -1;
    }
    
    /**
     * Check if the player's selection is complete.
     * @param player the player
     * @return true if selection is complete, false if incomplete or no selection
     */
    public boolean isSelectionComplete(@NotNull Player player) {
        Selection selection = getSelection(player);
        return selection != null && selection.isComplete();
    }
    
    /**
     * Get the minimum bounds of the player's selection.
     * @param player the player
     * @return minimum bounds, or null if player has no selection or insufficient points
     */
    @Nullable
    public Location getMinBounds(@NotNull Player player) {
        Selection selection = getSelection(player);
        return selection != null ? selection.getMinBounds() : null;
    }
    
    /**
     * Get the maximum bounds of the player's selection.
     * @param player the player
     * @return maximum bounds, or null if player has no selection or insufficient points
     */
    @Nullable
    public Location getMaxBounds(@NotNull Player player) {
        Selection selection = getSelection(player);
        return selection != null ? selection.getMaxBounds() : null;
    }
    
    /**
     * Get the center of the player's selection.
     * @param player the player
     * @return center location, or null if player has no selection or insufficient points
     */
    @Nullable
    public Location getCenter(@NotNull Player player) {
        Selection selection = getSelection(player);
        return selection != null ? selection.getCenter() : null;
    }
    
    /**
     * Get the volume of the player's selection.
     * @param player the player
     * @return volume in blocks, or 0 if player has no selection or insufficient points
     */
    public double getVolume(@NotNull Player player) {
        Selection selection = getSelection(player);
        return selection != null ? selection.getVolume() : 0;
    }
    
    /**
     * Check if a location is within the player's selection.
     * @param player the player
     * @param location the location to check
     * @return true if within selection, false otherwise
     */
    public boolean contains(@NotNull Player player, @NotNull Location location) {
        Selection selection = getSelection(player);
        return selection != null && selection.contains(location);
    }
    
    /**
     * Get all active selections.
     * @return collection of all active selections
     */
    @NotNull
    public Collection<Selection> getAllSelections() {
        return Collections.unmodifiableCollection(playerSelections.values());
    }
    
    /**
     * Get active selection count.
     * @return number of active selections
     */
    public int getSelectionCount() {
        return playerSelections.size();
    }
    
    /**
     * Clear all selections.
     */
    public void clearAll() {
        playerSelections.clear();
    }
    
    /**
     * Remove selections for offline players.
     */
    public void cleanupOfflineSelections() {
        Iterator<Map.Entry<UUID, Selection>> iterator = playerSelections.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Selection> entry = iterator.next();
            Selection selection = entry.getValue();
            if (!selection.isOwnerOnline()) {
                iterator.remove();
            }
        }
    }
    
    /**
     * Get selections by world.
     * @param world the world
     * @return list of selections in the world
     */
    @NotNull
    public List<Selection> getSelectionsInWorld(@NotNull World world) {
        List<Selection> result = new ArrayList<>();
        for (Selection selection : playerSelections.values()) {
            if (selection.getWorldName().equals(world.getName())) {
                result.add(selection);
            }
        }
        return result;
    }
}