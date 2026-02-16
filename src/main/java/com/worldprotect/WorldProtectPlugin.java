package com.worldprotect;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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
    
    @Override
    public void onLoad() {
        instance = this;
        getLogger().info("World Protect loading...");
        
        // Load configuration
        saveDefaultConfig();
        
        // Initialize components
        initializeComponents();
    }
    
    @Override
    public void onEnable() {
        getLogger().info("World Protect enabling...");
        
        // Register commands
        registerCommands();
        
        // Register event listeners
        registerEvents();
        
        // Initialize API
        initializeAPI();
        
        // Start metrics
        startMetrics();
        
        getLogger().info("World Protect enabled successfully!");
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info("Author: " + getDescription().getAuthors());
    }
    
    @Override
    public void onDisable() {
        getLogger().info("World Protect disabling...");
        
        // Save data
        saveData();
        
        // Cleanup resources
        cleanup();
        
        getLogger().info("World Protect disabled successfully!");
    }
    
    /**
     * Initialize plugin components.
     */
    private void initializeComponents() {
        // TODO: Initialize core components
        // - RegionManager
        // - FlagManager
        // - CommandRegistry
        // - StorageManager
        // - SelectionManager
        
        getLogger().info("Components initialized");
    }
    
    /**
     * Register plugin commands.
     */
    private void registerCommands() {
        // TODO: Register commands
        // - /wp command with subcommands
        
        getLogger().info("Commands registered");
    }
    
    /**
     * Register event listeners.
     */
    private void registerEvents() {
        // TODO: Register event listeners
        // - Block events
        // - Entity events
        // - Player events
        // - World events
        
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
     * Save plugin data.
     */
    private void saveData() {
        // TODO: Save region data
        // - Save to database/file
        // - Backup if configured
        
        getLogger().info("Data saved");
    }
    
    /**
     * Cleanup resources.
     */
    private void cleanup() {
        // TODO: Cleanup resources
        // - Close database connections
        // - Clear caches
        // - Stop async tasks
        
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