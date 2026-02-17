package com.worldprotect.listener;

import com.worldprotect.WorldProtectPlugin;
import com.worldprotect.selection.Selection;
import org.bukkit.Location;
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
            // Start new selection - default to POINT_BASED for backward compatibility
            selection = plugin.getSelectionManager().startSelection(
                player, "temp", player.getWorld(), Selection.SelectionType.POINT_BASED);
            
            if (selection == null) {
                player.sendMessage("§cYou already have an active selection. Use /wp cancel to cancel it.");
                return;
            }
            
            if (selection.getType() == Selection.SelectionType.POINT_BASED) {
                player.sendMessage("§aSelection started! Left-click to set point 1, right-click to set point 2.");
                player.sendMessage("§7Shift + Right-click: Remove last point");
            } else {
                player.sendMessage("§aPolygon selection started! Click to add points.");
                player.sendMessage("§7Minimum 3 points required. Use /wp selection finish to complete.");
                player.sendMessage("§7Shift + Right-click: Remove last point");
            }
        } else {
            // For POINT_BASED selections, check if complete and prevent adding more points
            if (selection.getType() == Selection.SelectionType.POINT_BASED && selection.isComplete()) {
                player.sendMessage("§eYou already have a complete selection.");
                player.sendMessage("§7Use /wp create <name> to create an area, or /wp cancel to start over.");
                return;
            }
            // For FREE_DRAW selections, always allow adding more points
        }
        
        // Add point based on action
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getClickedBlock() != null) {
            // Store block center coordinates for consistent polygon boundaries
            Location blockLocation = event.getClickedBlock().getLocation();
            Location centerLocation = new Location(
                blockLocation.getWorld(),
                blockLocation.getBlockX() + 0.5,
                blockLocation.getBlockY(),
                blockLocation.getBlockZ() + 0.5
            );
            
            if (plugin.getSelectionManager().addPoint(player, centerLocation)) {
                int pointCount = plugin.getSelectionManager().getPointCount(player);
                player.sendMessage("§aPoint " + pointCount + " set at " + formatLocation(event.getClickedBlock().getLocation()));
                
                // Check if maximum points reached for FREE_DRAW selection
                Selection sel = plugin.getSelectionManager().getSelection(player);
                if (sel != null && sel.getType() == Selection.SelectionType.FREE_DRAW && pointCount >= 360) {
                    player.sendMessage("§eMaximum polygon points (360) reached. Use /wp selection finish to complete.");
                }
            } else {
                // Check if failed due to maximum points
                Selection sel = plugin.getSelectionManager().getSelection(player);
                if (sel != null && sel.getType() == Selection.SelectionType.FREE_DRAW && sel.getPointCount() >= 360) {
                    player.sendMessage("§cMaximum polygon points (360) reached. Use /wp selection finish to complete.");
                }
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            // Store block center coordinates for consistent polygon boundaries
            Location blockLocation = event.getClickedBlock().getLocation();
            Location centerLocation = new Location(
                blockLocation.getWorld(),
                blockLocation.getBlockX() + 0.5,
                blockLocation.getBlockY(),
                blockLocation.getBlockZ() + 0.5
            );
            
            if (plugin.getSelectionManager().addPoint(player, centerLocation)) {
                int pointCount = plugin.getSelectionManager().getPointCount(player);
                player.sendMessage("§aPoint " + pointCount + " set at " + formatLocation(event.getClickedBlock().getLocation()));
                
                // Check if maximum points reached for FREE_DRAW selection
                Selection sel = plugin.getSelectionManager().getSelection(player);
                if (sel != null && sel.getType() == Selection.SelectionType.FREE_DRAW && pointCount >= 360) {
                    player.sendMessage("§eMaximum polygon points (360) reached. Use /wp selection finish to complete.");
                }
            } else {
                // Check if failed due to maximum points
                Selection sel = plugin.getSelectionManager().getSelection(player);
                if (sel != null && sel.getType() == Selection.SelectionType.FREE_DRAW && sel.getPointCount() >= 360) {
                    player.sendMessage("§cMaximum polygon points (360) reached. Use /wp selection finish to complete.");
                }
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