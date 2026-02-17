package com.worldprotect.flags;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a protection flag that can be applied to areas.
 * Flags control various behaviors like PvP, block breaking, etc.
 */
public enum Flag implements ConfigurationSerializable {
    
    // Player combat flags
    PVP("pvp", "Controls player vs player combat", false, Arrays.asList("allow", "deny", "members-only")),
    
    // Mob/entity flags
    MOB_DAMAGE_PLAYERS("mob-damage-players", "Controls whether mobs can damage players", true),
    MOB_SPAWNING("mob-spawning", "Controls mob spawning", true),
    
    // Explosion flags
    TNT("tnt", "Controls TNT explosions", true),
    CREEPER_EXPLOSION("creeper-explosion", "Controls creeper explosions", true),
    GHAST_FIREBALL("ghast-fireball", "Controls ghast fireball explosions", true),
    
    // Environment flags
    FIRE_SPREAD("fire-spread", "Controls fire spread", true),
    LAVA_FLOW("lava-flow", "Controls lava flow", true),
    WATER_FLOW("water-flow", "Controls water flow", true),
    FLUID_LEAK("fluid-leak", "Prevents fluid from leaking into area from outside", true),
    LIGHTNING("lightning", "Controls lightning strikes", true),
    SNOW_FALL("snow-fall", "Controls snow accumulation", true),
    SNOW_MELT("snow-melt", "Controls snow melting", true),
    ICE_FORM("ice-form", "Controls ice formation", true),
    ICE_MELT("ice-melt", "Controls ice melting", true),
    CROP_GROWTH("crop-growth", "Controls crop growth", true),
    GRASS_SPREAD("grass-spread", "Controls grass spread", true),
    LEAF_DECAY("leaf-decay", "Controls leaf decay", true),
    
    // Block & container flags
    BLOCK_BREAK("block-break", "Controls block breaking", true),
    BLOCK_PLACE("block-place", "Controls block placing", true),
    CONTAINER_ACCESS("container-access", "Controls access to containers", true),
    USE("use", "Controls use of doors, buttons, levers, etc.", true),
    
    // Item & vehicle flags
    ITEM_DROP("item-drop", "Controls item dropping", true),
    ITEM_PICKUP("item-pickup", "Controls item pickup", true),
    VEHICLE_PLACE("vehicle-place", "Controls vehicle placement", true),
    VEHICLE_DESTROY("vehicle-destroy", "Controls vehicle destruction", true),
    
    // Player state flags
    HUNGER("hunger", "Controls hunger depletion", true),
    NATURAL_REGENERATION("natural-regeneration", "Controls natural health regeneration", true),
    
    // Teleportation flags
    TELEPORT_IN("teleport-in", "Controls teleportation into area", true),
    TELEPORT_OUT("teleport-out", "Controls teleportation out of area", true),
    
    // Entry/exit flags
    ENTRY("entry", "Controls entry into area", true, Arrays.asList("allow", "deny", "notify")),
    LEAVE("leave", "Controls exit from area", true, Arrays.asList("allow", "deny", "notify")),
    
    // Command flags
    COMMANDS("commands", "Controls command usage in area", true);
    
    private final String name;
    private final String description;
    private final boolean defaultValue;
    private final List<String> allowedValues;
    
    Flag(String name, String description, boolean defaultValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.allowedValues = Arrays.asList("allow", "deny");
    }
    
    Flag(String name, String description, boolean defaultValue, List<String> allowedValues) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.allowedValues = allowedValues;
    }
    
    /**
     * Get the flag name (lowercase with hyphens).
     * @return the flag name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the flag description.
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get the allowed values for this flag.
     * @return list of allowed values
     */
    public List<String> getAllowedValues() {
        return allowedValues;
    }
    
    /**
     * Check if a value is valid for this flag.
     * @param value the value to check
     * @return true if valid
     */
    public boolean isValidValue(String value) {
        return allowedValues.contains(value.toLowerCase());
    }
    
    /**
     * Get the default value for this flag.
     * @return the default value
     */
    public boolean getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Find a flag by name (case-insensitive).
     * @param name the flag name
     * @return the flag, or null if not found
     */
    public static Flag byName(String name) {
        for (Flag flag : values()) {
            if (flag.getName().equalsIgnoreCase(name)) {
                return flag;
            }
        }
        return null;
    }
    
    /**
     * Get all flag names.
     * @return list of flag names
     */
    public static List<String> getFlagNames() {
        List<String> names = new ArrayList<>();
        for (Flag flag : values()) {
            names.add(flag.getName());
        }
        return names;
    }
    
    /**
     * Get flags by category.
     * @return map of category to list of flags
     */
    public static Map<String, List<Flag>> getFlagsByCategory() {
        Map<String, List<Flag>> categories = new LinkedHashMap<>();
        
        categories.put("Player Combat", Arrays.asList(PVP));
        categories.put("Mobs & Entities", Arrays.asList(MOB_DAMAGE_PLAYERS, MOB_SPAWNING));
        categories.put("Explosions", Arrays.asList(TNT, CREEPER_EXPLOSION, GHAST_FIREBALL));
        categories.put("Environment", Arrays.asList(FIRE_SPREAD, LAVA_FLOW, WATER_FLOW, FLUID_LEAK, LIGHTNING,
                SNOW_FALL, SNOW_MELT, ICE_FORM, ICE_MELT, CROP_GROWTH, GRASS_SPREAD, LEAF_DECAY));
        categories.put("Blocks & Containers", Arrays.asList(BLOCK_BREAK, BLOCK_PLACE, CONTAINER_ACCESS, USE));
        categories.put("Items & Vehicles", Arrays.asList(ITEM_DROP, ITEM_PICKUP, VEHICLE_PLACE, VEHICLE_DESTROY));
        categories.put("Player State", Arrays.asList(HUNGER, NATURAL_REGENERATION));
        categories.put("Teleportation", Arrays.asList(TELEPORT_IN, TELEPORT_OUT));
        categories.put("Entry/Exit", Arrays.asList(ENTRY, LEAVE));
        categories.put("Commands", Arrays.asList(COMMANDS));
        
        return categories;
    }
    
    // ConfigurationSerializable implementation
    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("description", description);
        data.put("default", defaultValue);
        return data;
    }
    
    /**
     * Deserialize a Flag from configuration data.
     * @param data the configuration data
     * @return the Flag
     */
    @NotNull
    public static Flag deserialize(@NotNull Map<String, Object> data) {
        String name = (String) data.get("name");
        Flag flag = byName(name);
        if (flag == null) {
            throw new IllegalArgumentException("Unknown flag: " + name);
        }
        return flag;
    }
}