package com.worldprotect.listener;

import com.worldprotect.WorldProtectPlugin;
import com.worldprotect.area.Area;
import com.worldprotect.flags.Flag;
import com.worldprotect.flags.FlagResolver;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

/**
 * Main protection listener that handles all protection events.
 */
public class ProtectionListener implements Listener {
    
    private final WorldProtectPlugin plugin;
    
    public ProtectionListener(WorldProtectPlugin plugin) {
        this.plugin = plugin;
    }
    
    // ========== BLOCK EVENTS ==========
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        // Check bypass permission
        if (player.hasPermission("worldprotect.bypass")) {
            return;
        }
        
        // Check flag
        boolean allowed = checkFlag(player, block.getLocation(), Flag.BLOCK_BREAK);
        if (!allowed) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot break blocks here.");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        // Check bypass permission
        if (player.hasPermission("worldprotect.bypass")) {
            return;
        }
        
        // Check flag
        boolean allowed = checkFlag(player, block.getLocation(), Flag.BLOCK_PLACE);
        if (!allowed) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot place blocks here.");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        // Check fire spread flag
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            boolean allowed = checkFlag(null, event.getBlock().getLocation(), Flag.FIRE_SPREAD);
            if (!allowed) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        Block block = event.getBlock();
        Block toBlock = event.getToBlock();
        Material type = block.getType();
        
        // Check if fluid is trying to flow INTO a protected area
        // First check the source block location
        boolean sourceAllowed = true;
        boolean targetAllowed = true;
        
        if (type == Material.LAVA || type == Material.LAVA_BUCKET) {
            sourceAllowed = checkFlag(null, block.getLocation(), Flag.LAVA_FLOW);
            targetAllowed = checkFlag(null, toBlock.getLocation(), Flag.LAVA_FLOW);
            
            // Check fluid leak flag for target area
            boolean fluidLeakAllowed = checkFlag(null, toBlock.getLocation(), Flag.FLUID_LEAK);
            if (!fluidLeakAllowed) {
                // If fluid leak is disabled, check if source is outside protected area
                // and target is inside protected area
                List<Area> sourceAreas = plugin.getAreaManager().getAreasAtLocation(block.getLocation());
                List<Area> targetAreas = plugin.getAreaManager().getAreasAtLocation(toBlock.getLocation());
                
                // If target has areas but source doesn't (or different areas), prevent flow
                if (!targetAreas.isEmpty() && (sourceAreas.isEmpty() || !sourceAreas.equals(targetAreas))) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        // Check water flow
        else if (type == Material.WATER || type == Material.WATER_BUCKET) {
            sourceAllowed = checkFlag(null, block.getLocation(), Flag.WATER_FLOW);
            targetAllowed = checkFlag(null, toBlock.getLocation(), Flag.WATER_FLOW);
            
            // Check fluid leak flag for target area
            boolean fluidLeakAllowed = checkFlag(null, toBlock.getLocation(), Flag.FLUID_LEAK);
            if (!fluidLeakAllowed) {
                // If fluid leak is disabled, check if source is outside protected area
                // and target is inside protected area
                List<Area> sourceAreas = plugin.getAreaManager().getAreasAtLocation(block.getLocation());
                List<Area> targetAreas = plugin.getAreaManager().getAreasAtLocation(toBlock.getLocation());
                
                // If target has areas but source doesn't (or different areas), prevent flow
                if (!targetAreas.isEmpty() && (sourceAreas.isEmpty() || !sourceAreas.equals(targetAreas))) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        
        // Cancel if either source or target doesn't allow flow
        if (!sourceAllowed || !targetAllowed) {
            event.setCancelled(true);
        }
    }
    
    // ========== PLAYER EVENTS ==========
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        
        if (block == null) {
            return;
        }
        
        // Check bypass permission
        if (player.hasPermission("worldprotect.bypass")) {
            return;
        }
        
        // Check container access
        if (block.getState() instanceof InventoryHolder) {
            boolean allowed = checkFlag(player, block.getLocation(), Flag.CONTAINER_ACCESS);
            if (!allowed) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot access containers here.");
                return;
            }
        }
        
        // Check use flag for doors, buttons, levers, etc.
        Material type = block.getType();
        if (type == Material.OAK_DOOR || type == Material.IRON_DOOR || 
            type == Material.OAK_BUTTON || type == Material.STONE_BUTTON ||
            type == Material.LEVER || type == Material.OAK_TRAPDOOR ||
            type == Material.IRON_TRAPDOOR) {
            boolean allowed = checkFlag(player, block.getLocation(), Flag.USE);
            if (!allowed) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot use that here.");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        // Check bypass permission
        if (player.hasPermission("worldprotect.bypass")) {
            return;
        }
        
        // Check flag
        boolean allowed = checkFlag(player, player.getLocation(), Flag.ITEM_DROP);
        if (!allowed) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot drop items here.");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        
        // Check bypass permission
        if (player.hasPermission("worldprotect.bypass")) {
            return;
        }
        
        // Check flag
        boolean allowed = checkFlag(player, player.getLocation(), Flag.ITEM_PICKUP);
        if (!allowed) {
            event.setCancelled(true);
            // Note: PlayerPickupItemEvent is deprecated in newer versions
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        
        // Check PvP
        if (damager instanceof Player && entity instanceof Player) {
            Player attacker = (Player) damager;
            Player victim = (Player) entity;
            
            // Check bypass permission
            if (attacker.hasPermission("worldprotect.bypass")) {
                return;
            }
            
            // Check flag
            boolean allowed = checkFlag(attacker, victim.getLocation(), Flag.PVP);
            if (!allowed) {
                event.setCancelled(true);
                attacker.sendMessage("§cPvP is disabled here.");
            }
        }
        // Check mob damage to players
        else if (damager instanceof Monster && entity instanceof Player) {
            Player player = (Player) entity;
            boolean allowed = checkFlag(player, player.getLocation(), Flag.MOB_DAMAGE_PLAYERS);
            if (!allowed) {
                event.setCancelled(true);
            }
        }
    }
    
    // ========== ENTITY EVENTS ==========
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        Location location = event.getLocation();
        
        // Check explosion flags
        if (entity instanceof Creeper) {
            boolean allowed = checkFlag(null, location, Flag.CREEPER_EXPLOSION);
            if (!allowed) {
                event.setCancelled(true);
                event.blockList().clear();
            }
        } else if (entity instanceof TNTPrimed) {
            boolean allowed = checkFlag(null, location, Flag.TNT);
            if (!allowed) {
                event.setCancelled(true);
                event.blockList().clear();
            }
        } else if (entity instanceof Fireball) {
            // Check for ghast fireballs
            boolean allowed = checkFlag(null, location, Flag.GHAST_FIREBALL);
            if (!allowed) {
                event.setCancelled(true);
                event.blockList().clear();
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        
        // Check mob spawning
        if (entity instanceof Monster || entity instanceof Animals) {
            boolean allowed = checkFlag(null, entity.getLocation(), Flag.MOB_SPAWNING);
            if (!allowed) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLightningStrike(LightningStrikeEvent event) {
        boolean allowed = checkFlag(null, event.getLightning().getLocation(), Flag.LIGHTNING);
        if (!allowed) {
            event.setCancelled(true);
        }
    }
    
    // ========== VEHICLE EVENTS ==========
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onVehicleCreate(VehicleCreateEvent event) {
        Vehicle vehicle = event.getVehicle();
        boolean allowed = checkFlag(null, vehicle.getLocation(), Flag.VEHICLE_PLACE);
        if (!allowed) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        Vehicle vehicle = event.getVehicle();
        Entity attacker = event.getAttacker();
        
        if (attacker instanceof Player) {
            Player player = (Player) attacker;
            
            // Check bypass permission
            if (player.hasPermission("worldprotect.bypass")) {
                return;
            }
            
            boolean allowed = checkFlag(player, vehicle.getLocation(), Flag.VEHICLE_DESTROY);
            if (!allowed) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot destroy vehicles here.");
            }
        }
    }
    
    // ========== SNOW AND ICE EVENTS ==========
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockForm(BlockFormEvent event) {
        Block block = event.getBlock();
        Material newType = event.getNewState().getType();
        
        // Check snow formation
        if (newType == Material.SNOW) {
            boolean allowed = checkFlag(null, block.getLocation(), Flag.SNOW_FALL);
            if (!allowed) {
                event.setCancelled(true);
            }
        }
        // Check ice formation
        else if (newType == Material.ICE || newType == Material.PACKED_ICE || newType == Material.BLUE_ICE) {
            boolean allowed = checkFlag(null, block.getLocation(), Flag.ICE_FORM);
            if (!allowed) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();
        
        // Check snow melting
        if (type == Material.SNOW) {
            boolean allowed = checkFlag(null, block.getLocation(), Flag.SNOW_MELT);
            if (!allowed) {
                event.setCancelled(true);
            }
        }
        // Check ice melting
        else if (type == Material.ICE || type == Material.PACKED_ICE || type == Material.BLUE_ICE) {
            boolean allowed = checkFlag(null, block.getLocation(), Flag.ICE_MELT);
            if (!allowed) {
                event.setCancelled(true);
            }
        }
    }
    
    /**
     * Helper method to check flag value at a location.
     */
    private boolean checkFlag(Player player, Location location, Flag flag) {
        // Get areas at location
        List<Area> areas = plugin.getAreaManager().getAreasAtLocation(location);
        
        // Use FlagResolver to get effective flag value
        return FlagResolver.getEffectiveFlagValue(areas, flag.getName(), player);
    }
    
    /**
     * Helper method to check if a player is an owner or member of an area.
     */
    private boolean isOwnerOrMember(Player player, Area area) {
        if (area == null) {
            return false;
        }
        return area.getOwners().contains(player.getUniqueId()) || 
               area.getMembers().contains(player.getUniqueId());
    }
}