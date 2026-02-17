package com.worldprotect.flags;

import com.worldprotect.area.Area;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Resolves flag values for areas, handling priority, overrides, and group flags.
 */
public class FlagResolver {
    
    /**
     * Subject group for flag resolution (owner, member, nonmember).
     */
    public enum SubjectGroup {
        OWNER,
        MEMBER,
        NONMEMBER
    }
    
    /**
     * Get the effective value of a flag at a specific location.
     * @param areas list of areas containing the location (sorted by priority)
     * @param flagName the flag name to check
     * @param player the player to check membership for (nullable)
     * @return the effective flag value
     */
    public static boolean getEffectiveFlagValue(@NotNull List<Area> areas, @NotNull String flagName,
                                                @Nullable Player player) {
        if (areas.isEmpty()) {
            // No areas at this location, return default
            Flag flag = Flag.byName(flagName);
            return flag != null ? flag.getDefaultValue() : true;
        }
        
        // Determine subject group
        SubjectGroup subjectGroup = getSubjectGroup(areas, player);
        
        // Check for atomic flag overrides first
        Flag atomicFlag = Flag.byName(flagName);
        if (atomicFlag != null) {
            Boolean atomicValue = getAtomicFlagValue(areas, atomicFlag, subjectGroup);
            if (atomicValue != null) {
                return atomicValue;
            }
        }
        
        // Check group flags
        GroupFlag groupFlag = GroupFlag.byName(flagName);
        if (groupFlag != null) {
            Boolean groupValue = getGroupFlagValue(areas, groupFlag, subjectGroup);
            if (groupValue != null) {
                return groupValue;
            }
        }
        
        // Return default if no explicit value found
        return atomicFlag != null ? atomicFlag.getDefaultValue() : true;
    }
    
    /**
     * Get the atomic flag value from areas, respecting priority.
     * @param areas areas sorted by priority
     * @param flag the atomic flag
     * @param subjectGroup the subject group
     * @return the flag value, or null if not explicitly set
     */
    @Nullable
    private static Boolean getAtomicFlagValue(@NotNull List<Area> areas, @NotNull Flag flag,
                                              @NotNull SubjectGroup subjectGroup) {
        for (Area area : areas) {
            Boolean value = area.getFlagValue(flag, subjectGroup);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
    
    /**
     * Get the group flag value from areas, respecting priority.
     * @param areas areas sorted by priority
     * @param groupFlag the group flag
     * @param subjectGroup the subject group
     * @return the group flag value, or null if not explicitly set
     */
    @Nullable
    private static Boolean getGroupFlagValue(@NotNull List<Area> areas, @NotNull GroupFlag groupFlag,
                                             @NotNull SubjectGroup subjectGroup) {
        for (Area area : areas) {
            Boolean value = area.getGroupFlagValue(groupFlag, subjectGroup);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
    
    /**
     * Determine the subject group for a player relative to areas.
     * @param areas areas to check membership in
     * @param player the player (nullable)
     * @return the subject group
     */
    @NotNull
    private static SubjectGroup getSubjectGroup(@NotNull List<Area> areas, @Nullable Player player) {
        if (player == null) {
            return SubjectGroup.NONMEMBER;
        }
        
        // Check if player is owner of any area
        for (Area area : areas) {
            if (area.isOwner(player.getUniqueId())) {
                return SubjectGroup.OWNER;
            }
        }
        
        // Check if player is member of any area
        for (Area area : areas) {
            if (area.isMember(player.getUniqueId())) {
                return SubjectGroup.MEMBER;
            }
        }
        
        return SubjectGroup.NONMEMBER;
    }
    
    /**
     * Get all effective flag values at a location.
     * @param areas areas containing the location
     * @param player the player (nullable)
     * @return map of flag name to effective value
     */
    @NotNull
    public static Map<String, Boolean> getAllEffectiveFlagValues(@NotNull List<Area> areas,
                                                                 @Nullable Player player) {
        Map<String, Boolean> result = new LinkedHashMap<>();
        
        // Add all atomic flags
        for (Flag flag : Flag.values()) {
            boolean value = getEffectiveFlagValue(areas, flag.getName(), player);
            result.put(flag.getName(), value);
        }
        
        // Add group flags
        for (GroupFlag groupFlag : GroupFlag.values()) {
            boolean value = getEffectiveFlagValue(areas, groupFlag.getName(), player);
            result.put(groupFlag.getName(), value);
        }
        
        return result;
    }
    
    /**
     * Get flag values with source information.
     * @param areas areas containing the location
     * @param player the player (nullable)
     * @return list of flag info with source area and value
     */
    @NotNull
    public static List<FlagInfo> getFlagInfo(@NotNull List<Area> areas, @Nullable Player player) {
        List<FlagInfo> infoList = new ArrayList<>();
        
        // Process atomic flags
        for (Flag flag : Flag.values()) {
            FlagInfo info = getFlagInfoForFlag(areas, flag, player);
            infoList.add(info);
        }
        
        // Process group flags
        for (GroupFlag groupFlag : GroupFlag.values()) {
            FlagInfo info = getFlagInfoForGroupFlag(areas, groupFlag, player);
            infoList.add(info);
        }
        
        return infoList;
    }
    
    @NotNull
    private static FlagInfo getFlagInfoForFlag(@NotNull List<Area> areas, @NotNull Flag flag,
                                               @Nullable Player player) {
        SubjectGroup subjectGroup = getSubjectGroup(areas, player);
        
        for (Area area : areas) {
            Boolean value = area.getFlagValue(flag, subjectGroup);
            if (value != null) {
                return new FlagInfo(flag.getName(), value, area.getName(), true, false);
            }
        }
        
        // Check group flags that include this atomic flag
        for (Area area : areas) {
            for (GroupFlag groupFlag : GroupFlag.values()) {
                if (groupFlag.getAtomicFlags().contains(flag)) {
                    Boolean groupValue = area.getGroupFlagValue(groupFlag, subjectGroup);
                    if (groupValue != null) {
                        // Check if atomic flag is overridden in higher priority areas
                        boolean overridden = false;
                        for (Area higherArea : areas) {
                            if (higherArea == area) break; // Only check areas before current
                            if (higherArea.getFlagValue(flag, subjectGroup) != null) {
                                overridden = true;
                                break;
                            }
                        }
                        
                        if (!overridden) {
                            return new FlagInfo(flag.getName(), groupValue, area.getName(), false, true);
                        }
                    }
                }
            }
        }
        
        return new FlagInfo(flag.getName(), flag.getDefaultValue(), "default", false, false);
    }
    
    @NotNull
    private static FlagInfo getFlagInfoForGroupFlag(@NotNull List<Area> areas, @NotNull GroupFlag groupFlag,
                                                    @Nullable Player player) {
        SubjectGroup subjectGroup = getSubjectGroup(areas, player);
        
        for (Area area : areas) {
            Boolean value = area.getGroupFlagValue(groupFlag, subjectGroup);
            if (value != null) {
                return new FlagInfo(groupFlag.getName(), value, area.getName(), false, true);
            }
        }
        
        return new FlagInfo(groupFlag.getName(), true, "default", false, false);
    }
    
    /**
     * Information about a flag value and its source.
     */
    public static class FlagInfo {
        private final String flagName;
        private final boolean value;
        private final String source;
        private final boolean isAtomic;
        private final boolean isFromGroup;
        
        public FlagInfo(String flagName, boolean value, String source, boolean isAtomic, boolean isFromGroup) {
            this.flagName = flagName;
            this.value = value;
            this.source = source;
            this.isAtomic = isAtomic;
            this.isFromGroup = isFromGroup;
        }
        
        public String getFlagName() { return flagName; }
        public boolean getValue() { return value; }
        public String getSource() { return source; }
        public boolean isAtomic() { return isAtomic; }
        public boolean isFromGroup() { return isFromGroup; }
        
        @Override
        public String toString() {
            return flagName + "=" + value + " (from " + source + 
                   (isAtomic ? ", atomic" : "") + 
                   (isFromGroup ? ", group" : "") + ")";
        }
    }
}