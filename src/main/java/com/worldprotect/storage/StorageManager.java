package com.worldprotect.storage;

import com.worldprotect.area.Area;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for area storage management.
 */
public interface StorageManager {
    
    /**
     * Initialize the storage system.
     * @return completable future that completes when initialization is done
     */
    CompletableFuture<Void> initialize();
    
    /**
     * Shutdown the storage system.
     * @return completable future that completes when shutdown is done
     */
    CompletableFuture<Void> shutdown();
    
    /**
     * Save an area.
     * @param area the area to save
     * @return completable future that completes when save is done
     */
    CompletableFuture<Void> saveArea(@NotNull Area area);
    
    /**
     * Load an area by name.
     * @param name the area name
     * @return completable future with the loaded area, or null if not found
     */
    CompletableFuture<Area> loadArea(@NotNull String name);
    
    /**
     * Delete an area.
     * @param name the area name
     * @return completable future that completes when deletion is done
     */
    CompletableFuture<Void> deleteArea(@NotNull String name);
    
    /**
     * Load all areas.
     * @return completable future with collection of all areas
     */
    CompletableFuture<Collection<Area>> loadAllAreas();
    
    /**
     * Check if an area exists.
     * @param name the area name
     * @return completable future with true if area exists
     */
    CompletableFuture<Boolean> areaExists(@NotNull String name);
    
    /**
     * Get the number of stored areas.
     * @return completable future with area count
     */
    CompletableFuture<Integer> getAreaCount();
    
    /**
     * Backup the storage.
     * @return completable future that completes when backup is done
     */
    CompletableFuture<Void> backup();
    
    /**
     * Restore from backup.
     * @return completable future that completes when restore is done
     */
    CompletableFuture<Void> restore();
}