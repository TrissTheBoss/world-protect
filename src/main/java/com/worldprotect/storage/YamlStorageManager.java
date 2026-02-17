package com.worldprotect.storage;

import com.worldprotect.area.Area;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * YAML file-based storage manager.
 */
public class YamlStorageManager implements StorageManager {
    
    private final File dataFolder;
    private final Map<String, Area> cache;
    private final ExecutorService executor;
    
    public YamlStorageManager(@NotNull File dataFolder) {
        this.dataFolder = dataFolder;
        this.cache = new HashMap<>();
        this.executor = Executors.newSingleThreadExecutor();
    }
    
    @Override
    public CompletableFuture<Void> initialize() {
        return CompletableFuture.runAsync(() -> {
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.runAsync(() -> {
            executor.shutdown();
            cache.clear();
        });
    }
    
    @Override
    public CompletableFuture<Void> saveArea(@NotNull Area area) {
        return CompletableFuture.runAsync(() -> {
            try {
                String name = area.getName();
                File file = new File(dataFolder, name + ".yml");
                YamlConfiguration config = new YamlConfiguration();
                
                // Serialize area to configuration
                Map<String, Object> serialized = area.serialize();
                for (Map.Entry<String, Object> entry : serialized.entrySet()) {
                    config.set(entry.getKey(), entry.getValue());
                }
                
                config.save(file);
                cache.put(name, area);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save area: " + area.getName(), e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Area> loadArea(@NotNull String name) {
        return CompletableFuture.supplyAsync(() -> {
            // Check cache first
            Area cached = cache.get(name);
            if (cached != null) {
                return cached;
            }
            
            File file = new File(dataFolder, name + ".yml");
            if (!file.exists()) {
                return null;
            }
            
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                Map<String, Object> data = new HashMap<>();
                
                // Convert configuration to map
                for (String key : config.getKeys(true)) {
                    Object value = config.get(key);
                    if (value instanceof ConfigurationSection) {
                        continue; // Skip sections, we only want leaf values
                    }
                    data.put(key, value);
                }
                
                Area area = Area.deserialize(data);
                cache.put(name, area);
                return area;
            } catch (Exception e) {
                throw new RuntimeException("Failed to load area: " + name, e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> deleteArea(@NotNull String name) {
        return CompletableFuture.runAsync(() -> {
            File file = new File(dataFolder, name + ".yml");
            if (file.exists()) {
                file.delete();
            }
            cache.remove(name);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Collection<Area>> loadAllAreas() {
        return CompletableFuture.supplyAsync(() -> {
            List<Area> areas = new ArrayList<>();
            
            if (!dataFolder.exists()) {
                return areas;
            }
            
            File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files == null) {
                return areas;
            }
            
            for (File file : files) {
                try {
                    String name = file.getName().replace(".yml", "");
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    Map<String, Object> data = new HashMap<>();
                    
                    for (String key : config.getKeys(true)) {
                        Object value = config.get(key);
                        if (value instanceof ConfigurationSection) {
                            continue;
                        }
                        data.put(key, value);
                    }
                    
                    Area area = Area.deserialize(data);
                    areas.add(area);
                    cache.put(name, area);
                } catch (Exception e) {
                    System.err.println("Failed to load area from file: " + file.getName());
                    e.printStackTrace();
                }
            }
            
            return areas;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Boolean> areaExists(@NotNull String name) {
        return CompletableFuture.supplyAsync(() -> {
            if (cache.containsKey(name)) {
                return true;
            }
            
            File file = new File(dataFolder, name + ".yml");
            return file.exists();
        }, executor);
    }
    
    @Override
    public CompletableFuture<Integer> getAreaCount() {
        return CompletableFuture.supplyAsync(() -> {
            if (!dataFolder.exists()) {
                return 0;
            }
            
            File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
            return files != null ? files.length : 0;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> backup() {
        return CompletableFuture.runAsync(() -> {
            File backupFolder = new File(dataFolder.getParentFile(), "backups");
            if (!backupFolder.exists()) {
                backupFolder.mkdirs();
            }
            
            String timestamp = String.valueOf(System.currentTimeMillis());
            File backupFile = new File(backupFolder, "areas_backup_" + timestamp + ".zip");
            
            // Simple backup implementation - just copy all YAML files
            // In a real implementation, you would use proper zip compression
            try {
                // For now, just create an empty backup file
                backupFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create backup", e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> restore() {
        return CompletableFuture.runAsync(() -> {
            File backupFolder = new File(dataFolder.getParentFile(), "backups");
            if (!backupFolder.exists()) {
                return;
            }
            
            // Find latest backup
            File[] backupFiles = backupFolder.listFiles((dir, name) -> name.startsWith("areas_backup_") && name.endsWith(".zip"));
            if (backupFiles == null || backupFiles.length == 0) {
                return;
            }
            
            Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified).reversed());
            File latestBackup = backupFiles[0];
            
            // Simple restore implementation
            // In a real implementation, you would extract the zip and restore files
            System.out.println("Restoring from backup: " + latestBackup.getName());
        }, executor);
    }
    
    /**
     * Clear the cache.
     */
    public void clearCache() {
        cache.clear();
    }
    
    /**
     * Get the cache size.
     * @return cache size
     */
    public int getCacheSize() {
        return cache.size();
    }
}