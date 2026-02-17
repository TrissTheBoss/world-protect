package com.worldprotect.command;

import com.worldprotect.WorldProtectPlugin;
import com.worldprotect.area.Area;
import com.worldprotect.flags.Flag;
import com.worldprotect.flags.GroupFlag;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Main /wp command for World Protect.
 */
public class WorldProtectCommand extends BaseCommand {
    
    public WorldProtectCommand(@NotNull WorldProtectPlugin plugin) {
        super(plugin, "worldprotect.use", false);
    }
    
    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            return showHelp(sender);
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "help":
                return showHelp(sender);
            case "create":
                return createArea(sender, args);
            case "delete":
                return deleteArea(sender, args);
            case "list":
                return listAreas(sender);
            case "info":
                return areaInfo(sender, args);
            case "flags":
                return handleFlagsCommand(sender, args);
            case "flag":
                return setFlag(sender, args);
            case "wand":
                return giveWand(sender);
            case "reload":
                return reloadPlugin(sender);
            case "version":
                return showVersion(sender);
            case "cancel":
                return cancelSelection(sender);
            case "here":
                return handleHereCommand(sender, args);
            case "selection":
                return handleSelectionCommand(sender, args);
            case "circle":
                return createCircleArea(sender, args);
            default:
                sendError(sender, "Unknown command. Use /wp help for available commands.");
                return true;
        }
    }
    
    @Override
    protected @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.addAll(Arrays.asList("help", "wand", "create", "delete", "list", "info", "flags", "flag", "reload", "version", "cancel", "here", "selection", "circle"));
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "delete":
                case "info":
                case "flag":
                case "flags":
                    completions.addAll(plugin.getAreaManager().getAllAreas().stream()
                            .map(Area::getName)
                            .toList());
                    break;
                case "here":
                    completions.addAll(Arrays.asList("flags", "visualize"));
                    break;
                case "selection":
                    completions.addAll(Arrays.asList("new", "clear", "cancel", "info", "mode", "finish"));
                    break;
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("flag") || subCommand.equals("flags")) {
                // Add flag names for tab completion
                completions.addAll(Flag.getFlagNames());
            } else if (subCommand.equals("selection") && args[1].equalsIgnoreCase("new")) {
                // Suggest selection names
                completions.add("myarea");
                completions.add("spawn");
                completions.add("arena");
            }
        } else if (args.length == 4) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("flag") || subCommand.equals("flags")) {
                // Get the flag being set and suggest its allowed values
                String flagName = args[2];
                Flag flag = Flag.byName(flagName);
                if (flag != null) {
                    completions.addAll(flag.getAllowedValues());
                } else {
                    // Default suggestions if flag not found
                    completions.addAll(Arrays.asList("allow", "deny"));
                }
            }
        }
        
        return completions;
    }
    
    private boolean showHelp(@NotNull CommandSender sender) {
        sendMessage(sender, "§3=== World Protect Help ===");
        sendMessage(sender, "§6/wp help §7- Show this help");
        sendMessage(sender, "§6/wp wand §7- Get selection wand");
        sendMessage(sender, "§6/wp create <name> §7- Create a new area from selection");
        sendMessage(sender, "§6/wp circle <name> <radius> [height] §7- Create circular area");
        sendMessage(sender, "§6/wp delete <name> §7- Delete an area");
        sendMessage(sender, "§6/wp list §7- List all areas");
        sendMessage(sender, "§6/wp info <name> §7- Show area information");
        sendMessage(sender, "§6/wp flags §7- List available flags (or use /wp flags <area> <flag> <value> to set)");
        sendMessage(sender, "§6/wp flag <area> <flag> <value> §7- Set area flag");
        sendMessage(sender, "§6/wp reload §7- Reload the plugin");
        sendMessage(sender, "§6/wp version §7- Show plugin version");
        sendMessage(sender, "§6/wp cancel §7- Cancel current selection");
        sendMessage(sender, "§6/wp selection new <name> §7- Start a new selection");
        sendMessage(sender, "§6/wp selection info §7- Show selection information");
        sendMessage(sender, "§6/wp selection mode <wand|draw|points> §7- Switch selection mode");
        sendMessage(sender, "§6/wp selection finish §7- Complete polygon selection (draw/points mode)");
        sendMessage(sender, "§6/wp here §7- Show areas at your location");
        sendMessage(sender, "§6/wp here flags §7- Show effective flags at your location");
        sendMessage(sender, "§6/wp here visualize §7- Visualize area boundaries");
        return true;
    }
    
    private boolean createArea(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sendError(sender, "This command can only be used by players.");
            return true;
        }
        
        if (args.length < 2) {
            sendError(sender, "Usage: /wp create <name> [shape] [style] [border] [priority]");
            sendError(sender, "Shapes: square, circle, triangle, hexagon, polygon");
            sendError(sender, "Styles: full, border");
            sendError(sender, "Example: /wp create spawn polygon full 1 10");
            return true;
        }
        
        String name = args[1];
        
        // Check if area already exists
        if (plugin.getAreaManager().hasArea(name)) {
            sendError(sender, "An area with that name already exists.");
            return true;
        }
        
        // Check if player has a selection
        var selection = plugin.getSelectionManager().getSelection(player);
        if (selection == null || !selection.isComplete()) {
            sendError(sender, "You need to make a selection first. Use the selection wand.");
            return true;
        }
        
        // Default values - auto-detect shape based on selection
        Area.Shape shape;
        if (selection.getType() == com.worldprotect.selection.Selection.SelectionType.FREE_DRAW && selection.getPointCount() >= 3) {
            // FREE_DRAW selection with 3+ points should be POLYGON
            shape = Area.Shape.POLYGON;
        } else if (selection.getPointCount() == 2) {
            // 2 points = SQUARE (rectangular area)
            shape = Area.Shape.SQUARE;
        } else {
            // Default to SQUARE
            shape = Area.Shape.SQUARE;
        }
        
        Area.Style style = Area.Style.FULL;
        int borderThickness = 1;
        int priority = 10;
        
        // Parse optional arguments (allow overriding auto-detected shape)
        if (args.length >= 3) {
            try {
                shape = Area.Shape.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException e) {
                sendError(sender, "Invalid shape. Use: square, circle, triangle, hexagon, polygon");
                return true;
            }
        }
        
        if (args.length >= 4) {
            try {
                style = Area.Style.valueOf(args[3].toUpperCase());
            } catch (IllegalArgumentException e) {
                sendError(sender, "Invalid style. Use: full, border");
                return true;
            }
        }
        
        if (args.length >= 5) {
            try {
                borderThickness = Integer.parseInt(args[4]);
                if (borderThickness < 1) {
                    sendError(sender, "Border thickness must be at least 1.");
                    return true;
                }
                if (borderThickness > 50) {
                    sendError(sender, "Border thickness cannot exceed 50.");
                    return true;
                }
            } catch (NumberFormatException e) {
                sendError(sender, "Invalid border thickness. Must be a number.");
                return true;
            }
        }
        
        if (args.length >= 6) {
            try {
                priority = Integer.parseInt(args[5]);
                if (priority < 1 || priority > 50) {
                    sendError(sender, "Priority must be between 1 and 50.");
                    return true;
                }
            } catch (NumberFormatException e) {
                sendError(sender, "Invalid priority. Must be a number between 1 and 50.");
                return true;
            }
        }
        
        // Validate polygon selection
        if (shape == Area.Shape.POLYGON && selection.getType() != com.worldprotect.selection.Selection.SelectionType.FREE_DRAW) {
            sendError(sender, "Polygon shape requires a free-draw selection. Use /wp selection mode draw first.");
            return true;
        }
        
        // Validate minimum points for polygon
        if (shape == Area.Shape.POLYGON && selection.getPointCount() < 3) {
            sendError(sender, "Polygon shape requires at least 3 points.");
            return true;
        }
        
        // Create area
        Area area = plugin.getAreaManager().createArea(name, selection, priority, 
                shape, style, borderThickness);
        
        if (area == null) {
            sendError(sender, "Failed to create area.");
            return true;
        }
        
        // Save area
        plugin.getStorageManager().saveArea(area).join();
        
        sendSuccess(sender, "Area '" + name + "' created successfully!");
        sendInfo(sender, "Shape: " + shape + ", Style: " + style + ", Priority: " + priority);
        sendInfo(sender, "Volume: " + area.getVolume() + " blocks");
        
        if (shape == Area.Shape.POLYGON) {
            sendInfo(sender, "Polygon points: " + selection.getPointCount());
        }
        return true;
    }
    
    private boolean deleteArea(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sendError(sender, "Usage: /wp delete <name>");
            return true;
        }
        
        String name = args[1];
        
        // Check if area exists
        Area area = plugin.getAreaManager().getArea(name);
        if (area == null) {
            sendError(sender, "Area '" + name + "' not found.");
            return true;
        }
        
        // Check permission (owners can delete their areas)
        if (sender instanceof Player player) {
            if (!area.isOwner(player.getUniqueId()) && !sender.hasPermission("worldprotect.admin")) {
                sendError(sender, "You don't own this area.");
                return true;
            }
        }
        
        // Delete area
        plugin.getAreaManager().removeArea(name);
        plugin.getStorageManager().deleteArea(name).join();
        
        sendSuccess(sender, "Area '" + name + "' deleted successfully!");
        return true;
    }
    
    private boolean listAreas(@NotNull CommandSender sender) {
        var areas = plugin.getAreaManager().getAllAreas();
        
        if (areas.isEmpty()) {
            sendInfo(sender, "No areas defined.");
            return true;
        }
        
        sendMessage(sender, "§3=== Areas (" + areas.size() + ") ===");
        
        for (Area area : areas) {
            String owners = String.join(", ", area.getOwners().stream()
                    .limit(3)
                    .map(uuid -> {
                        var player = plugin.getServer().getOfflinePlayer(uuid);
                        return player.getName() != null ? player.getName() : uuid.toString().substring(0, 8);
                    })
                    .toList());
            
            if (area.getOwners().size() > 3) {
                owners += ", ...";
            }
            
            sendMessage(sender, "§6" + area.getName() + " §7- Owners: " + owners + 
                    " §8| §7World: " + area.getWorldName() + 
                    " §8| §7Priority: " + area.getPriority());
        }
        
        return true;
    }
    
    private boolean areaInfo(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sendError(sender, "Usage: /wp info <name>");
            return true;
        }
        
        String name = args[1];
        Area area = plugin.getAreaManager().getArea(name);
        
        if (area == null) {
            sendError(sender, "Area '" + name + "' not found.");
            return true;
        }
        
        sendMessage(sender, "§3=== Area Info: " + area.getName() + " ===");
        sendMessage(sender, "§6World: §7" + area.getWorldName());
        sendMessage(sender, "§6Priority: §7" + area.getPriority());
        sendMessage(sender, "§6Shape: §7" + area.getShape());
        sendMessage(sender, "§6Style: §7" + area.getStyle());
        sendMessage(sender, "§6Volume: §7" + area.getVolume() + " blocks");
        sendMessage(sender, "§6Created: §7" + new java.util.Date(area.getCreatedAt()));
        
        // Owners
        String owners = String.join(", ", area.getOwners().stream()
                .map(uuid -> {
                    var player = plugin.getServer().getOfflinePlayer(uuid);
                    return player.getName() != null ? player.getName() : uuid.toString().substring(0, 8);
                })
                .toList());
        sendMessage(sender, "§6Owners: §7" + owners);
        
        // Members
        if (!area.getMembers().isEmpty()) {
            String members = String.join(", ", area.getMembers().stream()
                    .limit(5)
                    .map(uuid -> {
                        var player = plugin.getServer().getOfflinePlayer(uuid);
                        return player.getName() != null ? player.getName() : uuid.toString().substring(0, 8);
                    })
                    .toList());
            
            if (area.getMembers().size() > 5) {
                members += ", ... (+" + (area.getMembers().size() - 5) + " more)";
            }
            sendMessage(sender, "§6Members: §7" + members);
        }
        
        return true;
    }
    
    private boolean handleFlagsCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // If we have 3 arguments (area, flag, value), treat it as a flag set command
        if (args.length == 4) { // args[0] is "flags", so args[1], args[2], args[3] are area, flag, value
            return setFlag(sender, args);
        }
        
        // Otherwise, list flags
        return listFlags(sender);
    }
    
    private boolean listFlags(@NotNull CommandSender sender) {
        sendMessage(sender, "§3=== Available Flags ===");
        
        sendMessage(sender, "§6Atomic Flags:");
        for (Flag flag : Flag.values()) {
            sendMessage(sender, "  §7- " + flag.getName() + " §8- " + flag.getDescription());
        }
        
        sendMessage(sender, "§6Group Flags:");
        for (GroupFlag groupFlag : GroupFlag.values()) {
            sendMessage(sender, "  §7- " + groupFlag.getName() + " §8- " + groupFlag.getDescription());
        }
        
        return true;
    }
    
    private boolean reloadPlugin(@NotNull CommandSender sender) {
        if (!sender.hasPermission("worldprotect.admin")) {
            sendError(sender, "You don't have permission to reload the plugin.");
            return true;
        }
        
        plugin.reloadConfig();
        sendSuccess(sender, "Plugin configuration reloaded!");
        return true;
    }
    
    private boolean setFlag(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 4) {
            sendError(sender, "Usage: /wp flag <area> <flag> <value>");
            sendError(sender, "Example: /wp flag spawn pvp deny");
            sendError(sender, "Example: /wp flag arena pvp members-only");
            sendError(sender, "Example: /wp flag spawn entry notify");
            return true;
        }
        
        String areaName = args[1];
        String flagName = args[2];
        String valueStr = args[3].toLowerCase();
        
        // Check if area exists
        Area area = plugin.getAreaManager().getArea(areaName);
        if (area == null) {
            sendError(sender, "Area '" + areaName + "' not found.");
            // Suggest similar area names
            List<String> suggestions = plugin.getAreaManager().getAllAreas().stream()
                    .map(Area::getName)
                    .filter(name -> name.toLowerCase().contains(areaName.toLowerCase()) || 
                                   areaName.toLowerCase().contains(name.toLowerCase()))
                    .limit(3)
                    .toList();
            if (!suggestions.isEmpty()) {
                sendInfo(sender, "Did you mean: " + String.join(", ", suggestions) + "?");
            }
            return true;
        }
        
        // Check permission (owners can set flags on their areas)
        if (sender instanceof Player player) {
            if (!area.isOwner(player.getUniqueId()) && !sender.hasPermission("worldprotect.admin")) {
                sendError(sender, "You don't own this area.");
                return true;
            }
        }
        
        // Check if flag exists
        Flag flag = Flag.byName(flagName);
        if (flag == null) {
            sendError(sender, "Flag '" + flagName + "' not found. Use /wp flags to see available flags.");
            // Suggest similar flag names
            List<String> suggestions = Flag.getFlagNames().stream()
                    .filter(name -> name.toLowerCase().contains(flagName.toLowerCase()) || 
                                   flagName.toLowerCase().contains(name.toLowerCase()))
                    .limit(3)
                    .toList();
            if (!suggestions.isEmpty()) {
                sendInfo(sender, "Did you mean: " + String.join(", ", suggestions) + "?");
            }
            return true;
        }
        
        // Validate the value against allowed values for this flag
        if (!flag.isValidValue(valueStr)) {
            sendError(sender, "Invalid value '" + valueStr + "' for flag '" + flag.getName() + "'.");
            sendInfo(sender, "Allowed values: " + String.join(", ", flag.getAllowedValues()));
            return true;
        }
        
        // Parse value - handle special cases
        boolean booleanValue;
        String displayValue = valueStr;
        
        // Convert string values to boolean for storage
        if (valueStr.equals("allow") || valueStr.equals("true") || valueStr.equals("on") || valueStr.equals("1")) {
            booleanValue = true;
            displayValue = "allow";
        } else if (valueStr.equals("deny") || valueStr.equals("false") || valueStr.equals("off") || valueStr.equals("0")) {
            booleanValue = false;
            displayValue = "deny";
        } else if (valueStr.equals("members-only")) {
            // For PVP flag, "members-only" means true but with special handling
            booleanValue = true;
            displayValue = "members-only";
        } else if (valueStr.equals("notify")) {
            // For ENTRY/LEAVE flags, "notify" means true but with notification
            booleanValue = true;
            displayValue = "notify";
        } else {
            // Default to true for other custom values
            booleanValue = true;
        }
        
        // Set flag
        area.setFlag(flag, booleanValue);
        
        // Save area
        plugin.getStorageManager().saveArea(area).join();
        
        sendSuccess(sender, "Flag '" + flag.getName() + "' set to " + displayValue + " for area '" + areaName + "'.");
        return true;
    }
    
    private boolean giveWand(@NotNull CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendError(sender, "This command can only be used by players.");
            return true;
        }
        
        // Get wand material from config
        String wandMaterialName = plugin.getConfig().getString("regions.selection-wand", "golden_axe");
        org.bukkit.Material wandMaterial = org.bukkit.Material.getMaterial(wandMaterialName.toUpperCase());
        if (wandMaterial == null) {
            wandMaterial = org.bukkit.Material.GOLDEN_AXE; // Default fallback
        }
        
        // Create wand item
        org.bukkit.inventory.ItemStack wand = new org.bukkit.inventory.ItemStack(wandMaterial);
        org.bukkit.inventory.meta.ItemMeta meta = wand.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6World Protect Selection Wand");
            meta.setLore(java.util.Arrays.asList(
                "§7Left-click: Set point 1",
                "§7Right-click: Set point 2",
                "§7Shift + Right-click: Remove last point"
            ));
            wand.setItemMeta(meta);
        }
        
        // Give wand to player
        player.getInventory().addItem(wand);
        
        sendSuccess(sender, "Selection wand given!");
        sendInfo(sender, "Use left-click and right-click to select an area.");
        return true;
    }
    
    private boolean showVersion(@NotNull CommandSender sender) {
        var description = plugin.getDescription();
        sendMessage(sender, "§3=== World Protect ===");
        sendMessage(sender, "§6Version: §7" + description.getVersion());
        sendMessage(sender, "§6Authors: §7" + String.join(", ", description.getAuthors()));
        sendMessage(sender, "§6Website: §7" + description.getWebsite());
        return true;
    }
    
    private boolean cancelSelection(@NotNull CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendError(sender, "This command can only be used by players.");
            return true;
        }
        
        var selection = plugin.getSelectionManager().cancelSelection(player);
        if (selection == null) {
            sendError(sender, "You don't have an active selection.");
            return true;
        }
        
        sendSuccess(sender, "Selection cancelled.");
        return true;
    }
    
    private boolean handleHereCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sendError(sender, "This command can only be used by players.");
            return true;
        }
        
        Location location = player.getLocation();
        List<Area> areas = plugin.getAreaManager().getAreasAtLocation(location);
        
        if (args.length > 1) {
            String subArg = args[1].toLowerCase();
            if (subArg.equals("flags")) {
                // Show effective flag values
                return showHereFlags(sender, player, location, areas);
            } else if (subArg.equals("visualize")) {
                // Visualize areas
                return visualizeAreas(sender, player, location, areas);
            }
        }
        
        // Show areas at location
        return showHereAreas(sender, player, location, areas);
    }
    
    private boolean showHereAreas(@NotNull CommandSender sender, @NotNull Player player, 
                                  @NotNull Location location, @NotNull List<Area> areas) {
        sendMessage(sender, "§3=== Areas at your location ===");
        sendMessage(sender, "§6Location: §7" + formatLocation(location));
        
        if (areas.isEmpty()) {
            sendInfo(sender, "No areas at this location.");
            return true;
        }
        
        // Determine effective area (highest priority, with tie-break)
        Area effectiveArea = areas.isEmpty() ? null : areas.get(0);
        
        for (int i = 0; i < areas.size(); i++) {
            Area area = areas.get(i);
            String prefix = (i == 0) ? "§a✓ " : "§7  ";
            String effectiveMarker = (i == 0) ? " §a(Effective)" : "";
            
            sendMessage(sender, prefix + "§6" + area.getName() + 
                " §7- Priority: " + area.getPriority() + 
                ", Shape: " + area.getShape() + 
                ", Style: " + area.getStyle() +
                effectiveMarker);
        }
        
        if (areas.size() > 1) {
            sendInfo(sender, "Multiple areas overlap. Highest priority area (" + 
                effectiveArea.getName() + ") determines effective flags.");
        }
        
        return true;
    }
    
    private boolean showHereFlags(@NotNull CommandSender sender, @NotNull Player player,
                                  @NotNull Location location, @NotNull List<Area> areas) {
        sendMessage(sender, "§3=== Effective flags at your location ===");
        sendMessage(sender, "§6Location: §7" + formatLocation(location));
        
        if (areas.isEmpty()) {
            sendInfo(sender, "No areas at this location. Using default flag values.");
            // Show default flags
            for (Flag flag : Flag.values()) {
                String valueStr = flag.getDefaultValue() ? "§aallow" : "§cdeny";
                sendMessage(sender, "  §7- " + flag.getName() + ": " + valueStr + " §8(default)");
            }
            return true;
        }
        
        // Get effective flag values
        Map<String, Boolean> flagValues = plugin.getAreaManager().getAllFlagValuesAtLocation(location, player);
        
        // Show which area is effective
        Area effectiveArea = areas.get(0);
        sendMessage(sender, "§6Effective area: §7" + effectiveArea.getName() + 
            " (Priority: " + effectiveArea.getPriority() + ")");
        
        if (areas.size() > 1) {
            sendInfo(sender, areas.size() + " areas overlap. Highest priority area determines flags.");
        }
        
        // Show flag values
        for (Map.Entry<String, Boolean> entry : flagValues.entrySet()) {
            String flagName = entry.getKey();
            boolean value = entry.getValue();
            String valueStr = value ? "§aallow" : "§cdeny";
            sendMessage(sender, "  §7- " + flagName + ": " + valueStr);
        }
        
        return true;
    }
    
    private boolean visualizeAreas(@NotNull CommandSender sender, @NotNull Player player,
                                   @NotNull Location location, @NotNull List<Area> areas) {
        sendMessage(sender, "§3=== Area Visualization ===");
        sendMessage(sender, "§6Location: §7" + formatLocation(location));
        
        if (areas.isEmpty()) {
            sendInfo(sender, "No areas at this location to visualize.");
            return true;
        }
        
        // Show area boundaries
        for (Area area : areas) {
            sendMessage(sender, "§6Area: §7" + area.getName());
            
            Location min = area.getMinBounds();
            Location max = area.getMaxBounds();
            
            if (min != null && max != null) {
                sendMessage(sender, "  §7Bounds: " + 
                    formatLocation(min) + " to " + formatLocation(max));
                sendMessage(sender, "  §7Shape: " + area.getShape() + 
                    ", Style: " + area.getStyle());
                
                // Show distance to area center
                Location center = area.getCenter();
                if (center != null) {
                    double distance = location.distance(center);
                    sendMessage(sender, "  §7Distance to center: " + 
                        String.format("%.1f", distance) + " blocks");
                }
            }
        }
        
        sendInfo(sender, "Visualization shows area boundaries and your position relative to them.");
        return true;
    }
    
    private boolean handleSelectionCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sendError(sender, "This command can only be used by players.");
            return true;
        }
        
        if (args.length < 2) {
            sendError(sender, "Usage: /wp selection <new|clear|info|mode|finish> [name]");
            sendError(sender, "  new <name> - Start a new selection with given name");
            sendError(sender, "  clear/cancel - Clear current selection");
            sendError(sender, "  info - Show selection information");
            sendError(sender, "  mode <wand|draw|points> - Switch selection mode");
            sendError(sender, "  finish - Complete polygon selection (draw/points mode only)");
            return true;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "new":
                return newSelection(player, args);
            case "clear":
            case "cancel":
                return cancelSelection(sender);
            case "info":
                return selectionInfo(player);
            case "mode":
                return selectionMode(player, args);
            case "finish":
                return finishSelection(player);
            default:
                sendError(sender, "Unknown selection action. Use: new, clear, info, mode, or finish");
                return true;
        }
    }
    
    private boolean newSelection(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 3) {
            sendError(player, "Usage: /wp selection new <name>");
            return true;
        }
        
        String name = args[2];
        
        // Cancel any existing selection first
        plugin.getSelectionManager().cancelSelection(player);
        
        // Start new selection
        var selection = plugin.getSelectionManager().startSelection(
            player, name, player.getWorld(), com.worldprotect.selection.Selection.SelectionType.POINT_BASED);
        
        if (selection == null) {
            sendError(player, "Failed to start new selection.");
            return true;
        }
        
        sendSuccess(player, "New selection '" + name + "' started!");
        sendInfo(player, "Use the selection wand to set points.");
        sendInfo(player, "Left-click: Set point 1, Right-click: Set point 2");
        sendInfo(player, "Shift + Right-click: Remove last point");
        return true;
    }
    
    private boolean selectionInfo(@NotNull Player player) {
        var selection = plugin.getSelectionManager().getSelection(player);
        if (selection == null) {
            sendError(player, "You don't have an active selection.");
            return true;
        }
        
        sendMessage(player, "§3=== Selection Info ===");
        sendMessage(player, "§6Name: §7" + selection.getName());
        sendMessage(player, "§6World: §7" + selection.getWorldName());
        sendMessage(player, "§6Type: §7" + selection.getType());
        sendMessage(player, "§6Points: §7" + selection.getPointCount());
        sendMessage(player, "§6Complete: §7" + (selection.isComplete() ? "Yes" : "No"));
        
        if (selection.getPointCount() > 0) {
            Location min = selection.getMinBounds();
            Location max = selection.getMaxBounds();
            if (min != null && max != null) {
                sendMessage(player, "§6Bounds: §7" + formatLocation(min) + " to " + formatLocation(max));
                sendMessage(player, "§6Volume: §7" + selection.getVolume() + " blocks");
            }
        }
        
        return true;
    }
    
    private boolean selectionMode(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 3) {
            sendError(player, "Usage: /wp selection mode <wand|draw|points>");
            sendError(player, "  wand - Classic two-point rectangular selection");
            sendError(player, "  draw/points - Multi-point polygon selection (unlimited points)");
            return true;
        }
        
        String mode = args[2].toLowerCase();
        com.worldprotect.selection.Selection.SelectionType type;
        
        switch (mode) {
            case "draw":
            case "points":
                type = com.worldprotect.selection.Selection.SelectionType.FREE_DRAW;
                break;
            case "wand":
                type = com.worldprotect.selection.Selection.SelectionType.POINT_BASED;
                break;
            default:
                sendError(player, "Invalid mode. Use: wand, draw, or points");
                return true;
        }
        
        // Get current selection or create new one
        var selection = plugin.getSelectionManager().getSelection(player);
        if (selection == null) {
            // Create a new selection with default name
            selection = plugin.getSelectionManager().startSelection(
                player, "selection", player.getWorld(), type);
        } else {
            // Change type of existing selection
            // Note: This would require modifying the Selection class or creating a new one
            // For simplicity, we'll cancel the current selection and start a new one
            plugin.getSelectionManager().cancelSelection(player);
            selection = plugin.getSelectionManager().startSelection(
                player, selection.getName(), player.getWorld(), type);
        }
        
        if (selection == null) {
            sendError(player, "Failed to switch selection mode.");
            return true;
        }
        
        sendSuccess(player, "Selection mode switched to " + mode + ".");
        if (mode.equals("draw")) {
            sendInfo(player, "Use left-click to add points. Minimum 3 points required for polygon.");
            sendInfo(player, "Use /wp selection finish to complete the polygon.");
        } else {
            sendInfo(player, "Use left-click for point 1, right-click for point 2.");
        }
        return true;
    }
    
    private boolean finishSelection(@NotNull Player player) {
        var selection = plugin.getSelectionManager().getSelection(player);
        if (selection == null) {
            sendError(player, "You don't have an active selection.");
            return true;
        }
        
        if (selection.getType() != com.worldprotect.selection.Selection.SelectionType.FREE_DRAW) {
            sendError(player, "The finish command is only for polygon (draw/points) selections.");
            sendInfo(player, "Use /wp selection mode draw to switch to polygon selection mode.");
            return true;
        }
        
        if (selection.getPointCount() < 3) {
            sendError(player, "Polygon selection requires at least 3 points.");
            sendInfo(player, "You have " + selection.getPointCount() + " points. Add more points with the selection wand.");
            return true;
        }
        
        if (plugin.getSelectionManager().finishSelection(player)) {
            sendSuccess(player, "Polygon selection completed!");
            sendInfo(player, "Points: " + selection.getPointCount());
            sendInfo(player, "Use /wp create <name> to create a polygon area.");
            return true;
        } else {
            sendError(player, "Failed to complete selection.");
            return true;
        }
    }
    
    private boolean createCircleArea(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sendError(sender, "This command can only be used by players.");
            return true;
        }
        
        if (args.length < 3) {
            sendError(sender, "Usage: /wp circle <name> <radius> [height]");
            sendError(sender, "Example: /wp circle spawn 10");
            sendError(sender, "Example: /wp circle arena 20 10");
            return true;
        }
        
        String name = args[1];
        String radiusStr = args[2];
        
        // Check if area already exists
        if (plugin.getAreaManager().hasArea(name)) {
            sendError(sender, "An area with that name already exists.");
            return true;
        }
        
        // Parse radius
        int radius;
        try {
            radius = Integer.parseInt(radiusStr);
            if (radius <= 0) {
                sendError(sender, "Radius must be positive.");
                return true;
            }
            if (radius > 100) {
                sendError(sender, "Radius cannot exceed 100 blocks.");
                return true;
            }
        } catch (NumberFormatException e) {
            sendError(sender, "Invalid radius. Must be a number.");
            return true;
        }
        
        // Parse optional height
        int height = radius * 2; // Default height is diameter
        if (args.length >= 4) {
            try {
                height = Integer.parseInt(args[3]);
                if (height <= 0) {
                    sendError(sender, "Height must be positive.");
                    return true;
                }
                if (height > 256) {
                    sendError(sender, "Height cannot exceed 256 blocks.");
                    return true;
                }
            } catch (NumberFormatException e) {
                sendError(sender, "Invalid height. Must be a number.");
                return true;
            }
        }
        
        // Get player location as center
        Location center = player.getLocation();
        
        // Calculate bounds for circle
        double centerX = center.getX();
        double centerZ = center.getZ();
        double minY = center.getY() - (height / 2.0);
        double maxY = center.getY() + (height / 2.0);
        
        // Create min and max bounds for the circle
        Location min = new Location(center.getWorld(), 
            centerX - radius, Math.max(minY, 0), centerZ - radius);
        Location max = new Location(center.getWorld(), 
            centerX + radius, Math.min(maxY, 255), centerZ + radius);
        
        // Create a temporary selection for the circle
        var selection = plugin.getSelectionManager().startSelection(
            player, "temp_circle", center.getWorld(), 
            com.worldprotect.selection.Selection.SelectionType.POINT_BASED);
        
        if (selection == null) {
            sendError(sender, "Failed to create selection.");
            return true;
        }
        
        // Add the calculated bounds as points
        selection.addPoint(min);
        selection.addPoint(max);
        
        // Create area with circle shape
        Area area = plugin.getAreaManager().createArea(name, selection, 10, 
                Area.Shape.CIRCLE, Area.Style.FULL, 1);
        
        if (area == null) {
            sendError(sender, "Failed to create circle area.");
            plugin.getSelectionManager().cancelSelection(player);
            return true;
        }
        
        // Save area
        plugin.getStorageManager().saveArea(area).join();
        
        // Clean up temporary selection
        plugin.getSelectionManager().cancelSelection(player);
        
        sendSuccess(sender, "Circle area '" + name + "' created successfully!");
        sendInfo(sender, "Center: " + formatLocation(center));
        sendInfo(sender, "Radius: " + radius + " blocks");
        sendInfo(sender, "Height: " + height + " blocks");
        sendInfo(sender, "Volume: " + area.getVolume() + " blocks");
        return true;
    }
    
    private String formatLocation(@NotNull Location location) {
        return String.format("(%d, %d, %d) in %s", 
            location.getBlockX(), location.getBlockY(), location.getBlockZ(),
            location.getWorld().getName());
    }
}
