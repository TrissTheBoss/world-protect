package com.worldprotect.listener;

import com.worldprotect.WorldProtectPlugin;
import com.worldprotect.selection.Selection;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener for selection wand interactions.
 */
public class SelectionListener implements Listener {
    
    private final WorldProtectPlugin plugin;
    
    public SelectionListener(WorldProtectPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // Check if player has permission
        if (!player.hasPermission("worldprotect.wand")) {
            return;
        }
        
        // Get wand material from config
        String wandMaterialName = plugin.getConfig().getString("regions.selection-wand", "golden_axe");
        Material wandMaterial = Material.getMaterial(wandMaterialName.toUpperCase());
        if (wandMaterial == null) {
            wandMaterial = Material.GOLDEN_AXE; // Default fallback
        }
        
        // Check if holding selection wand
        ItemStack item = event.getItem();
        if (item == null || item.getType() != wandMaterial) {
            return;
        }
        
        // Cancel event to prevent block breaking
        event.setCancelled(true);
        
        // Handle shift + right-click to remove last point
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.isSneaking()) {
            if (plugin.getSelectionManager().removeLastPoint(player)) {
                player.sendMessage("§eLast point removed.");
                int pointCount = plugin.getSelectionManager().getPointCount(player);
                if (pointCount > 0) {
                    player.sendMessage("§7Points remaining: " + pointCount);
                } else {
                    player.sendMessage("§7Selection cleared.");
                }
            } else {
                player.sendMessage("§cNo points to remove.");
            }
            return;
        }
        
        // Get or create selection
        Selection selection = plugin.getSelectionManager().getSelection(player);
        if (selection == null) {
            // Start new selection
            selection = plugin.getSelectionManager().startSelection(
                player, "temp", player.getWorld(), Selection.SelectionType.POINT_BASED);
            
            if (selection == null) {
                player.sendMessage("§cYou already have an active selection. Use /wp cancel to cancel it.");
                return;
            }
            
            player.sendMessage("§aSelection started! Left-click to set point 1, right-click to set point 2.");
            player.sendMessage("§7Shift + Right-click: Remove last point");
        } else {
            // If selection already exists and is complete, ask player to cancel or continue
            if (selection.isComplete()) {
                player.sendMessage("§eYou already have a complete selection.");
                player.sendMessage("§7Use /wp create <name> to create an area, or /wp cancel to start over.");
                return;
            }
        }
        
        // Add point based on action
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getClickedBlock() != null) {
            if (plugin.getSelectionManager().addPoint(player, event.getClickedBlock().getLocation())) {
                int pointCount = plugin.getSelectionManager().getPointCount(player);
                player.sendMessage("§aPoint " + pointCount + " set at " + formatLocation(event.getClickedBlock().getLocation()));
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            if (plugin.getSelectionManager().addPoint(player, event.getClickedBlock().getLocation())) {
                int pointCount = plugin.getSelectionManager().getPointCount(player);
                player.sendMessage("§aPoint " + pointCount + " set at " + formatLocation(event.getClickedBlock().getLocation()));
            }
        }
        
        // Check if selection is complete
        if (plugin.getSelectionManager().isSelectionComplete(player)) {
            player.sendMessage("§aSelection complete! Use /wp create <name> to create an area.");
            player.sendMessage("§7Volume: " + plugin.getSelectionManager().getVolume(player) + " blocks");
        }
    }
    
    private String formatLocation(org.bukkit.Location location) {
        return String.format("(%d, %d, %d)", 
            location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}