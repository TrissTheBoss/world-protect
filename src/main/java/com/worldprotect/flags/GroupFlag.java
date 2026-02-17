package com.worldprotect.flags;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a group flag that toggles multiple atomic flags at once.
 */
public enum GroupFlag implements ConfigurationSerializable {
    
    ENVIRONMENT_ALL("environment-all", "All environment flags",
            Arrays.asList(Flag.FIRE_SPREAD, Flag.LAVA_FLOW, Flag.WATER_FLOW, Flag.LIGHTNING,
                    Flag.SNOW_FALL, Flag.SNOW_MELT, Flag.ICE_FORM, Flag.ICE_MELT)),
    
    EXPLOSIONS_ALL("explosions-all", "All explosion flags",
            Arrays.asList(Flag.TNT, Flag.CREEPER_EXPLOSION, Flag.GHAST_FIREBALL)),
    
    INTERACTIONS_ALL("interactions-all", "All interaction flags",
            Arrays.asList(Flag.CONTAINER_ACCESS, Flag.USE, Flag.ITEM_DROP, Flag.ITEM_PICKUP,
                    Flag.VEHICLE_PLACE, Flag.VEHICLE_DESTROY)),
    
    BUILD_ALL("build-all", "All building flags",
            Arrays.asList(Flag.BLOCK_BREAK, Flag.BLOCK_PLACE)),
    
    MOB_ALL("mob-all", "All mob-related flags",
            Arrays.asList(Flag.MOB_DAMAGE_PLAYERS, Flag.MOB_SPAWNING));
    
    private final String name;
    private final String description;
    private final List<Flag> atomicFlags;
    
    GroupFlag(String name, String description, List<Flag> atomicFlags) {
        this.name = name;
        this.description = description;
        this.atomicFlags = atomicFlags;
    }
    
    /**
     * Get the group flag name.
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the group flag description.
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get the atomic flags included in this group.
     * @return list of atomic flags
     */
    public List<Flag> getAtomicFlags() {
        return atomicFlags;
    }
    
    /**
     * Find a group flag by name (case-insensitive).
     * @param name the group flag name
     * @return the group flag, or null if not found
     */
    public static GroupFlag byName(String name) {
        for (GroupFlag groupFlag : values()) {
            if (groupFlag.getName().equalsIgnoreCase(name)) {
                return groupFlag;
            }
        }
        return null;
    }
    
    /**
     * Get all group flag names.
     * @return list of group flag names
     */
    public static List<String> getGroupFlagNames() {
        List<String> names = new ArrayList<>();
        for (GroupFlag groupFlag : values()) {
            names.add(groupFlag.getName());
        }
        return names;
    }
    
    /**
     * Get all flag names (atomic + group).
     * @return list of all flag names
     */
    public static List<String> getAllFlagNames() {
        List<String> names = new ArrayList<>();
        names.addAll(Flag.getFlagNames());
        names.addAll(getGroupFlagNames());
        return names;
    }
    
    /**
     * Check if a flag name is a group flag.
     * @param name the flag name
     * @return true if it's a group flag
     */
    public static boolean isGroupFlag(String name) {
        return byName(name) != null;
    }
    
    /**
     * Check if a flag name is an atomic flag.
     * @param name the flag name
     * @return true if it's an atomic flag
     */
    public static boolean isAtomicFlag(String name) {
        return Flag.byName(name) != null;
    }
    
    // ConfigurationSerializable implementation
    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("description", description);
        
        List<String> flagNames = new ArrayList<>();
        for (Flag flag : atomicFlags) {
            flagNames.add(flag.getName());
        }
        data.put("atomicFlags", flagNames);
        
        return data;
    }
    
    /**
     * Deserialize a GroupFlag from configuration data.
     * @param data the configuration data
     * @return the GroupFlag
     */
    @NotNull
    public static GroupFlag deserialize(@NotNull Map<String, Object> data) {
        String name = (String) data.get("name");
        GroupFlag groupFlag = byName(name);
        if (groupFlag == null) {
            throw new IllegalArgumentException("Unknown group flag: " + name);
        }
        return groupFlag;
    }
}