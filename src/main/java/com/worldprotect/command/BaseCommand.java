package com.worldprotect.command;

import com.worldprotect.WorldProtectPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Base command class for World Protect commands.
 */
public abstract class BaseCommand implements CommandExecutor, TabCompleter {
    
    protected final WorldProtectPlugin plugin;
    protected final String permission;
    protected final boolean playerOnly;
    
    public BaseCommand(@NotNull WorldProtectPlugin plugin, @Nullable String permission, boolean playerOnly) {
        this.plugin = plugin;
        this.permission = permission;
        this.playerOnly = playerOnly;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        // Check permission
        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }
        
        // Check if player-only command
        if (playerOnly && !(sender instanceof org.bukkit.entity.Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }
        
        // Execute command
        return execute(sender, args);
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String label,
                                                @NotNull String[] args) {
        // Check permission
        if (permission != null && !sender.hasPermission(permission)) {
            return new ArrayList<>();
        }
        
        // Check if player-only command
        if (playerOnly && !(sender instanceof org.bukkit.entity.Player)) {
            return new ArrayList<>();
        }
        
        // Get tab completions
        return tabComplete(sender, args);
    }
    
    /**
     * Execute the command.
     * @param sender the command sender
     * @param args the command arguments
     * @return true if command was executed successfully
     */
    protected abstract boolean execute(@NotNull CommandSender sender, @NotNull String[] args);
    
    /**
     * Get tab completions for the command.
     * @param sender the command sender
     * @param args the command arguments
     * @return list of tab completions
     */
    protected abstract @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args);
    
    /**
     * Send a message to the command sender.
     * @param sender the command sender
     * @param message the message
     */
    protected void sendMessage(@NotNull CommandSender sender, @NotNull String message) {
        sender.sendMessage("§8[§3WorldProtect§8]§r " + message);
    }
    
    /**
     * Send an error message to the command sender.
     * @param sender the command sender
     * @param message the error message
     */
    protected void sendError(@NotNull CommandSender sender, @NotNull String message) {
        sender.sendMessage("§c✗§r " + message);
    }
    
    /**
     * Send a success message to the command sender.
     * @param sender the command sender
     * @param message the success message
     */
    protected void sendSuccess(@NotNull CommandSender sender, @NotNull String message) {
        sender.sendMessage("§a✓§r " + message);
    }
    
    /**
     * Send an info message to the command sender.
     * @param sender the command sender
     * @param message the info message
     */
    protected void sendInfo(@NotNull CommandSender sender, @NotNull String message) {
        sender.sendMessage("§7ℹ§r " + message);
    }
}