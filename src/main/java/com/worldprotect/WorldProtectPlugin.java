package com.worldprotect;

import com.worldprotect.area.AreaManager;
import com.worldprotect.selection.SelectionManager;
import com.worldprotect.storage.StorageManager;
import com.worldprotect.storage.YamlStorageManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Level;

/**
 * Main plugin class for World Protect.
 * 
 * <p>World Protect is a modern, high-performance world protection plugin
 * for Minecraft Paper/Folia servers. It provides granular region-based
 * protection with a comprehensive flag system, intuitive selection tools,
 * and full Folia compatibility.</p>
 * 
 * @version 1.0.0-SNAPSHOT
 * @since 1.0.0
 */
public class WorldProtectPlugin extends JavaPlugin {
    
    private static WorldProtectPlugin instance;
    private AreaManager areaManager;
    private SelectionManager selectionManager;
    private StorageManager storageManager;
    
    @Override
    public void onLoad() {
        instance = this;
        getLogger().info("World Protect loading...");
        
        // Load configuration
        saveDefaultConfig();
        
        // Initialize components synchronously (lightweight initialization)
        // Heavy async initialization will happen in onEnable()
        this.areaManager = new AreaManager();
        this.selectionManager = new SelectionManager();
        
        // Initialize storage manager (but don't load data yet)
        File dataFolder = new File(getDataFolder(), "areas");
        this.storageManager = new YamlStorageManager(dataFolder);
    }
    
    @Override
    public void onEnable() {
        getLogger().info("World Protect enabling...");
        
        // Initialize storage asynchronously
        storageManager.initialize().thenRun(() -> {
            // Register commands
            registerCommands();
            
            // Register event listeners
            registerEvents();
            
            // Initialize API
            initializeAPI();
            
            // Start metrics
            startMetrics();
            
            // Load data asynchronously
            loadDataAsync();
            
            getLogger().info("World Protect enabled successfully!");
            getLogger().info("Version: " + getDescription().getVersion());
            getLogger().info("Author: " + getDescription().getAuthors());
        }).exceptionally(throwable -> {
            getLogger().severe("Failed to enable World Protect: " + throwable.getMessage());
            throwable.printStackTrace();
            return null;
        });
    }
    
    @Override
    public void onDisable() {
        getLogger().info("World Protect disabling...");
        
        // Save data synchronously during shutdown
        // This is acceptable since the server is shutting down
        try {
            saveData();
        } catch (Exception e) {
            getLogger().severe("Failed to save data during shutdown: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Cleanup resources
        try {
            cleanup();
        } catch (Exception e) {
            getLogger().severe("Failed to cleanup resources during shutdown: " + e.getMessage());
            e.printStackTrace();
        }
        
        getLogger().info("World Protect disabled successfully!");
    }
    
    /**
     * Initialize plugin components.
     */
    private void initializeComponents() {
        // Initialize managers
        this.areaManager = new AreaManager();
        this.selectionManager = new SelectionManager();
        
        // Initialize storage
        File dataFolder = new File(getDataFolder(), "areas");
        this.storageManager = new YamlStorageManager(dataFolder);
        
        // Initialize storage
        storageManager.initialize().join();
        
        getLogger().info("Components initialized");
    }
    
    /**
     * Register plugin commands.
     */
    private void registerCommands() {
        // Register main command
        com.worldprotect.command.WorldProtectCommand mainCommand = new com.worldprotect.command.WorldProtectCommand(this);
        getCommand("wp").setExecutor(mainCommand);
        getCommand("wp").setTabCompleter(mainCommand);
        
        getLogger().info("Commands registered");
    }
    
    /**
     * Register event listeners.
     */
    private void registerEvents() {
        // Register selection listener
        getServer().getPluginManager().registerEvents(
            new com.worldprotect.listener.SelectionListener(this), this);
        
        // Register protection listener
        getServer().getPluginManager().registerEvents(
            new com.worldprotect.listener.ProtectionListener(this), this);
        
        getLogger().info("Event listeners registered");
    }
    
    /**
     * Initialize public API.
     */
    private void initializeAPI() {
        // TODO: Initialize public API
        // - Register service provider
        // - Setup API instance
        
        getLogger().info("API initialized");
    }
    
    /**
     * Start metrics collection.
     */
    private void startMetrics() {
        // TODO: Start bStats metrics
        // - Configure metrics
        // - Start collection
        
        if (getConfig().getBoolean("plugin.metrics", true)) {
            getLogger().info("Metrics enabled");
        }
    }
    
    /**
     * Load plugin data asynchronously.
     */
    private void loadDataAsync() {
        getLogger().info("Loading area data...");
        
        storageManager.loadAllAreas().thenAccept(areas -> {
            for (com.worldprotect.area.Area area : areas) {
                areaManager.addArea(area);
            }
            getLogger().info("Loaded " + areas.size() + " areas");
        }).exceptionally(throwable -> {
            getLogger().severe("Failed to load area data: " + throwable.getMessage());
            throwable.printStackTrace();
            return null;
        });
    }
    
    /**
     * Load plugin data (synchronous version for compatibility).
     */
    private void loadData() {
        getLogger().info("Loading area data...");
        
        storageManager.loadAllAreas().thenAccept(areas -> {
            for (com.worldprotect.area.Area area : areas) {
                areaManager.addArea(area);
            }
            getLogger().info("Loaded " + areas.size() + " areas");
        }).join();
    }
    
    /**
     * Save plugin data.
     */
    private void saveData() {
        getLogger().info("Saving area data...");
        
        for (com.worldprotect.area.Area area : areaManager.getAllAreas()) {
            storageManager.saveArea(area).join();
        }
        
        getLogger().info("Data saved");
    }
    
    /**
     * Cleanup resources.
     */
    private void cleanup() {
        if (storageManager != null) {
            storageManager.shutdown().join();
        }
        
        getLogger().info("Resources cleaned up");
    }
    
    /**
     * Get the plugin instance.
     * 
     * @return the plugin instance
     */
    @NotNull
    public static WorldProtectPlugin getInstance() {
        if (instance == null) {
            throw new IllegalStateException("WorldProtectPlugin not initialized");
        }
        return instance;
    }
    
    /**
     * Get the area manager.
     * 
     * @return the area manager
     */
    @NotNull
    public AreaManager getAreaManager() {
        return areaManager;
    }
    
    /**
     * Get the selection manager.
     * 
     * @return the selection manager
     */
    @NotNull
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }
    
    /**
     * Get the storage manager.
     * 
     * @return the storage manager
     */
    @NotNull
    public StorageManager getStorageManager() {
        return storageManager;
    }
    
    /**
     * Check if debug mode is enabled.
     * 
     * @return true if debug mode is enabled
     */
    public boolean isDebugEnabled() {
        return getConfig().getBoolean("plugin.debug", false);
    }
    
    /**
     * Log a debug message if debug mode is enabled.
     * 
     * @param message the debug message
     */
    public void debug(String message) {
        if (isDebugEnabled()) {
            getLogger().log(Level.INFO, "[DEBUG] " + message);
        }
    }
    
    /**
     * Log a warning message.
     * 
     * @param message the warning message
     */
    public void warn(String message) {
        getLogger().log(Level.WARNING, message);
    }
    
    /**
     * Log an error message.
     * 
     * @param message the error message
     * @param throwable the exception (optional)
     */
    public void error(String message, Throwable throwable) {
        if (throwable != null) {
            getLogger().log(Level.SEVERE, message, throwable);
        } else {
            getLogger().log(Level.SEVERE, message);
        }
    }
}
