package com.worldprotect.area;

import com.worldprotect.flags.Flag;
import com.worldprotect.flags.FlagResolver;
import com.worldprotect.flags.GroupFlag;
import com.worldprotect.selection.Selection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Area implements ConfigurationSerializable {
    
    private final String name;
    private final String worldName;
    private final int priority;
    private final Shape shape;
    private final Style style;
    private final int borderThickness;
    private final Set<UUID> owners;
    private final Set<UUID> members;
    private final Map<Flag, Map<FlagResolver.SubjectGroup, Boolean>> flagValues;
    private final Map<GroupFlag, Map<FlagResolver.SubjectGroup, Boolean>> groupFlagValues;
    private final Location minBounds;
    private final Location maxBounds;
    private final List<Location> polygonPoints; // For POLYGON shape only
    private final long createdAt;
    
    public enum Shape { SQUARE, CIRCLE, TRIANGLE, HEXAGON, POLYGON }
    public enum Style { FULL, BORDER }
    
    public Area(@NotNull String name, @NotNull Selection selection, int priority,
                @NotNull Shape shape, @NotNull Style style, int borderThickness) {
        this.name = name;
        this.worldName = selection.getWorldName();
        this.priority = Math.max(1, Math.min(50, priority));
        this.shape = shape;
        this.style = style;
        this.borderThickness = Math.max(1, borderThickness);
        this.owners = new HashSet<>();
        this.owners.add(selection.getOwnerId());
        this.members = new HashSet<>();
        this.flagValues = new HashMap<>();
        this.groupFlagValues = new HashMap<>();
        
        // Copy polygon points from selection if this is a polygon shape
        if (shape == Shape.POLYGON) {
            this.polygonPoints = new ArrayList<>();
            for (Location point : selection.getPoints()) {
                this.polygonPoints.add(point.clone());
            }
        } else {
            this.polygonPoints = new ArrayList<>();
        }
        
        Location min = selection.getMinBounds();
        Location max = selection.getMaxBounds();
        if (min == null || max == null) {
            throw new IllegalArgumentException("Selection has insufficient points");
        }
        this.minBounds = min.clone();
        this.maxBounds = max.clone();
        this.createdAt = System.currentTimeMillis();
    }
    
    @SuppressWarnings("unchecked")
    public Area(@NotNull Map<String, Object> data) {
        this.name = (String) data.get("name");
        this.worldName = (String) data.get("worldName");
        this.priority = ((Number) data.get("priority")).intValue();
        this.shape = Shape.valueOf((String) data.get("shape"));
        this.style = Style.valueOf((String) data.get("style"));
        this.borderThickness = ((Number) data.get("borderThickness")).intValue();
        this.createdAt = ((Number) data.get("createdAt")).longValue();
        
        this.owners = new HashSet<>();
        List<String> ownerStrings = (List<String>) data.get("owners");
        if (ownerStrings != null) {
            for (String ownerStr : ownerStrings) {
                owners.add(UUID.fromString(ownerStr));
            }
        }
        
        this.members = new HashSet<>();
        List<String> memberStrings = (List<String>) data.get("members");
        if (memberStrings != null) {
            for (String memberStr : memberStrings) {
                members.add(UUID.fromString(memberStr));
            }
        }
        
        this.flagValues = new HashMap<>();
        Map<String, Map<String, Boolean>> flagData = (Map<String, Map<String, Boolean>>) data.get("flagValues");
        if (flagData != null) {
            for (Map.Entry<String, Map<String, Boolean>> entry : flagData.entrySet()) {
                Flag flag = Flag.byName(entry.getKey());
                if (flag != null) {
                    Map<FlagResolver.SubjectGroup, Boolean> subjectMap = new HashMap<>();
                    Map<String, Boolean> subjectData = entry.getValue();
                    for (Map.Entry<String, Boolean> subjectEntry : subjectData.entrySet()) {
                        try {
                            FlagResolver.SubjectGroup subject = FlagResolver.SubjectGroup.valueOf(
                                subjectEntry.getKey().toUpperCase());
                            subjectMap.put(subject, subjectEntry.getValue());
                        } catch (IllegalArgumentException ignored) {}
                    }
                    flagValues.put(flag, subjectMap);
                }
            }
        }
        
        this.groupFlagValues = new HashMap<>();
        Map<String, Map<String, Boolean>> groupFlagData = (Map<String, Map<String, Boolean>>) data.get("groupFlagValues");
        if (groupFlagData != null) {
            for (Map.Entry<String, Map<String, Boolean>> entry : groupFlagData.entrySet()) {
                GroupFlag groupFlag = GroupFlag.byName(entry.getKey());
                if (groupFlag != null) {
                    Map<FlagResolver.SubjectGroup, Boolean> subjectMap = new HashMap<>();
                    Map<String, Boolean> subjectData = entry.getValue();
                    for (Map.Entry<String, Boolean> subjectEntry : subjectData.entrySet()) {
                        try {
                            FlagResolver.SubjectGroup subject = FlagResolver.SubjectGroup.valueOf(
                                subjectEntry.getKey().toUpperCase());
                            subjectMap.put(subject, subjectEntry.getValue());
                        } catch (IllegalArgumentException ignored) {}
                    }
                    groupFlagValues.put(groupFlag, subjectMap);
                }
            }
        }
        
        // Initialize polygon points (load from data if available)
        this.polygonPoints = new ArrayList<>();
        List<Map<String, Object>> polygonData = (List<Map<String, Object>>) data.get("polygonPoints");
        if (polygonData != null) {
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                for (Map<String, Object> pointData : polygonData) {
                    Location point = new Location(world,
                        ((Number) pointData.get("x")).doubleValue(),
                        ((Number) pointData.get("y")).doubleValue(),
                        ((Number) pointData.get("z")).doubleValue());
                    polygonPoints.add(point);
                }
            }
        }
        
        Map<String, Object> minData = (Map<String, Object>) data.get("minBounds");
        Map<String, Object> maxData = (Map<String, Object>) data.get("maxBounds");
        World world = Bukkit.getWorld(worldName);
        if (world != null && minData != null && maxData != null) {
            this.minBounds = new Location(world,
                ((Number) minData.get("x")).doubleValue(),
                ((Number) minData.get("y")).doubleValue(),
                ((Number) minData.get("z")).doubleValue());
            this.maxBounds = new Location(world,
                ((Number) maxData.get("x")).doubleValue(),
                ((Number) maxData.get("y")).doubleValue(),
                ((Number) maxData.get("z")).doubleValue());
        } else {
            this.minBounds = new Location(world != null ? world : Bukkit.getWorlds().get(0), 0, 0, 0);
            this.maxBounds = new Location(world != null ? world : Bukkit.getWorlds().get(0), 0, 0, 0);
        }
    }
    
    @NotNull public String getName() { return name; }
    @NotNull public String getWorldName() { return worldName; }
    @Nullable public World getWorld() { return Bukkit.getWorld(worldName); }
    public int getPriority() { return priority; }
    @NotNull public Shape getShape() { return shape; }
    @NotNull public Style getStyle() { return style; }
    public int getBorderThickness() { return borderThickness; }
    @NotNull public Set<UUID> getOwners() { return Collections.unmodifiableSet(owners); }
    @NotNull public Set<UUID> getMembers() { return Collections.unmodifiableSet(members); }
    public long getCreatedAt() { return createdAt; }
    @NotNull public Location getMinBounds() { return minBounds.clone(); }
    @NotNull public Location getMaxBounds() { return maxBounds.clone(); }
    @NotNull public List<Location> getPolygonPoints() { return Collections.unmodifiableList(polygonPoints); }
    
    @NotNull
    public Location getCenter() {
        return new Location(minBounds.getWorld(),
            (minBounds.getX() + maxBounds.getX()) / 2,
            (minBounds.getY() + maxBounds.getY()) / 2,
            (minBounds.getZ() + maxBounds.getZ()) / 2);
    }
    
    public double getVolume() {
        double width = Math.abs(maxBounds.getX() - minBounds.getX()) + 1;
        double height = Math.abs(maxBounds.getY() - minBounds.getY()) + 1;
        double depth = Math.abs(maxBounds.getZ() - minBounds.getZ()) + 1;
        return width * height * depth;
    }
    
    public boolean contains(@NotNull Location location) {
        return com.worldprotect.util.GeometryUtils.contains(location, this);
    }
    
    public boolean isOwner(@NotNull UUID playerId) { return owners.contains(playerId); }
    public boolean isMember(@NotNull UUID playerId) { return members.contains(playerId); }
    public boolean addOwner(@NotNull UUID playerId) { return owners.add(playerId); }
    public boolean removeOwner(@NotNull UUID playerId) { return owners.remove(playerId); }
    public boolean addMember(@NotNull UUID playerId) { return members.add(playerId); }
    public boolean removeMember(@NotNull UUID playerId) { return members.remove(playerId); }
    
    public void setFlagValue(@NotNull Flag flag, @NotNull FlagResolver.SubjectGroup subjectGroup, boolean value) {
        Map<FlagResolver.SubjectGroup, Boolean> subjectMap = flagValues.computeIfAbsent(flag, k -> new HashMap<>());
        subjectMap.put(subjectGroup, value);
    }
    
    /**
     * Set a flag value for all subject groups (owner, member, nonmember).
     * This is a convenience method for simple flag setting.
     */
    public void setFlag(@NotNull Flag flag, boolean value) {
        for (FlagResolver.SubjectGroup subjectGroup : FlagResolver.SubjectGroup.values()) {
            setFlagValue(flag, subjectGroup, value);
        }
    }
    
    @Nullable
    public Boolean getFlagValue(@NotNull Flag flag, @NotNull FlagResolver.SubjectGroup subjectGroup) {
        Map<FlagResolver.SubjectGroup, Boolean> subjectMap = flagValues.get(flag);
        return subjectMap != null ? subjectMap.get(subjectGroup) : null;
    }
    
    public boolean removeFlagValue(@NotNull Flag flag, @NotNull FlagResolver.SubjectGroup subjectGroup) {
        Map<FlagResolver.SubjectGroup, Boolean> subjectMap = flagValues.get(flag);
        if (subjectMap != null) {
            Boolean removed = subjectMap.remove(subjectGroup);
            if (subjectMap.isEmpty()) flagValues.remove(flag);
            return removed != null;
        }
        return false;
    }
    
    public void setGroupFlagValue(@NotNull GroupFlag groupFlag, @NotNull FlagResolver.SubjectGroup subjectGroup, boolean value) {
        Map<FlagResolver.SubjectGroup, Boolean> subjectMap = groupFlagValues.computeIfAbsent(groupFlag, k -> new HashMap<>());
        subjectMap.put(subjectGroup, value);
    }
    
    @Nullable
    public Boolean getGroupFlagValue(@NotNull GroupFlag groupFlag, @NotNull FlagResolver.SubjectGroup subjectGroup) {
        Map<FlagResolver.SubjectGroup, Boolean> subjectMap = groupFlagValues.get(groupFlag);
        return subjectMap != null ? subjectMap.get(subjectGroup) : null;
    }
    
    public boolean removeGroupFlagValue(@NotNull GroupFlag groupFlag, @NotNull FlagResolver.SubjectGroup subjectGroup) {
        Map<FlagResolver.SubjectGroup, Boolean> subjectMap = groupFlagValues.get(groupFlag);
        if (subjectMap != null) {
            Boolean removed = subjectMap.remove(subjectGroup);
            if (subjectMap.isEmpty()) groupFlagValues.remove(groupFlag);
            return removed != null;
        }
        return false;
    }
    
    @NotNull
    public Map<Flag, Map<FlagResolver.SubjectGroup, Boolean>> getFlagValues() {
        Map<Flag, Map<FlagResolver.SubjectGroup, Boolean>> result = new HashMap<>();
        for (Map.Entry<Flag, Map<FlagResolver.SubjectGroup, Boolean>> entry : flagValues.entrySet()) {
            result.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return result;
    }
    
    @NotNull
    public Map<GroupFlag, Map<FlagResolver.SubjectGroup, Boolean>> getGroupFlagValues() {
        Map<GroupFlag, Map<FlagResolver.SubjectGroup, Boolean>> result = new HashMap<>();
        for (Map.Entry<GroupFlag, Map<FlagResolver.SubjectGroup, Boolean>> entry : groupFlagValues.entrySet()) {
            result.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return result;
    }
    
    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("worldName", worldName);
        data.put("priority", priority);
        data.put("shape", shape.name());
        data.put("style", style.name());
        data.put("borderThickness", borderThickness);
        data.put("createdAt", createdAt);
        
        List<String> ownerStrings = new ArrayList<>();
        for (UUID owner : owners) ownerStrings.add(owner.toString());
        data.put("owners", ownerStrings);
        
        List<String> memberStrings = new ArrayList<>();
        for (UUID member : members) memberStrings.add(member.toString());
        data.put("members", memberStrings);
        
        Map<String, Map<String, Boolean>> flagData = new HashMap<>();
        for (Map.Entry<Flag, Map<FlagResolver.SubjectGroup, Boolean>> entry : flagValues.entrySet()) {
            Map<String, Boolean> subjectData = new HashMap<>();
            for (Map.Entry<FlagResolver.SubjectGroup, Boolean> subjectEntry : entry.getValue().entrySet()) {
                subjectData.put(subjectEntry.getKey().name(), subjectEntry.getValue());
            }
            flagData.put(entry.getKey().getName(), subjectData);
        }
        data.put("flagValues", flagData);
        
        Map<String, Map<String, Boolean>> groupFlagData = new HashMap<>();
        for (Map.Entry<GroupFlag, Map<FlagResolver.SubjectGroup, Boolean>> entry : groupFlagValues.entrySet()) {
            Map<String, Boolean> subjectData = new HashMap<>();
            for (Map.Entry<FlagResolver.SubjectGroup, Boolean> subjectEntry : entry.getValue().entrySet()) {
                subjectData.put(subjectEntry.getKey().name(), subjectEntry.getValue());
            }
            groupFlagData.put(entry.getKey().getName(), subjectData);
        }
        data.put("groupFlagValues", groupFlagData);
        
        Map<String, Object> minData = new HashMap<>();
        minData.put("x", minBounds.getX());
        minData.put("y", minBounds.getY());
        minData.put("z", minBounds.getZ());
        data.put("minBounds", minData);
        
        Map<String, Object> maxData = new HashMap<>();
        maxData.put("x", maxBounds.getX());
        maxData.put("y", maxBounds.getY());
        maxData.put("z", maxBounds.getZ());
        data.put("maxBounds", maxData);
        
        // Save polygon points if this is a polygon shape
        if (shape == Shape.POLYGON && !polygonPoints.isEmpty()) {
            List<Map<String, Object>> polygonData = new ArrayList<>();
            for (Location point : polygonPoints) {
                Map<String, Object> pointData = new HashMap<>();
                pointData.put("x", point.getX());
                pointData.put("y", point.getY());
                pointData.put("z", point.getZ());
                polygonData.add(pointData);
            }
            data.put("polygonPoints", polygonData);
        }
        
        return data;
    }
    
    @NotNull
    public static Area deserialize(@NotNull Map<String, Object> data) {
        return new Area(data);
    }
}