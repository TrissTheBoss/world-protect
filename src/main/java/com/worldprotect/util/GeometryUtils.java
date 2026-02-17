package com.worldprotect.util;

import com.worldprotect.area.Area;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for geometric calculations used by World Protect.
 */
public class GeometryUtils {
    
    private GeometryUtils() {
        // Utility class - no instantiation
    }
    
    /**
     * Check if a location is within an area, considering shape and style.
     */
    public static boolean contains(Location location, Area area) {
        if (!location.getWorld().getName().equals(area.getWorldName())) {
            return false;
        }
        
        // Get bounds for quick rejection
        Location min = area.getMinBounds();
        Location max = area.getMaxBounds();
        double x = location.getX(), y = location.getY(), z = location.getZ();
        
        // Quick AABB check
        if (x < min.getX() || x > max.getX() ||
            y < min.getY() || y > max.getY() ||
            z < min.getZ() || z > max.getZ()) {
            return false;
        }
        
        // Check shape
        boolean inShape = false;
        switch (area.getShape()) {
            case SQUARE:
                inShape = isInSquare(location, min, max);
                break;
            case CIRCLE:
                inShape = isInCircle(location, min, max);
                break;
            case TRIANGLE:
                inShape = isInTriangle(location, min, max);
                break;
            case HEXAGON:
                inShape = isInHexagon(location, min, max);
                break;
            case POLYGON:
                inShape = isInPolygon(location, area);
                break;
        }
        
        if (!inShape) {
            return false;
        }
        
        // Check style
        switch (area.getStyle()) {
            case FULL:
                return true;
            case BORDER:
                return isInBorder(location, min, max, area.getBorderThickness(), area.getShape(), area);
            default:
                return true;
        }
    }
    
    /**
     * Check if location is within a square/rectangle (axis-aligned).
     */
    private static boolean isInSquare(Location location, Location min, Location max) {
        // For square, any point within AABB is valid
        return true; // Already checked by AABB
    }
    
    /**
     * Check if location is within a circle (2D circle in XZ plane, full height).
     */
    private static boolean isInCircle(Location location, Location min, Location max) {
        double centerX = (min.getX() + max.getX()) / 2;
        double centerZ = (min.getZ() + max.getZ()) / 2;
        double radiusX = Math.abs(max.getX() - min.getX()) / 2;
        double radiusZ = Math.abs(max.getZ() - min.getZ()) / 2;
        
        // Use the smaller radius for a proper circle (ellipse if bounds aren't square)
        double radius = Math.min(radiusX, radiusZ);
        
        double dx = location.getX() - centerX;
        double dz = location.getZ() - centerZ;
        
        return (dx * dx + dz * dz) <= (radius * radius);
    }
    
    /**
     * Check if location is within a triangle.
     * Uses the three corners of the bounding box to form a triangle.
     */
    private static boolean isInTriangle(Location location, Location min, Location max) {
        // Create triangle points from bounding box corners
        Vector p1 = new Vector(min.getX(), min.getY(), min.getZ());
        Vector p2 = new Vector(max.getX(), min.getY(), min.getZ());
        Vector p3 = new Vector(min.getX(), min.getY(), max.getZ());
        
        Vector point = location.toVector();
        
        // Barycentric coordinate check
        Vector v0 = p2.subtract(p1);
        Vector v1 = p3.subtract(p1);
        Vector v2 = point.subtract(p1);
        
        double dot00 = v0.dot(v0);
        double dot01 = v0.dot(v1);
        double dot02 = v0.dot(v2);
        double dot11 = v1.dot(v1);
        double dot12 = v1.dot(v2);
        
        double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
        double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        double v = (dot00 * dot12 - dot01 * dot02) * invDenom;
        
        return (u >= 0) && (v >= 0) && (u + v <= 1);
    }
    
    /**
     * Check if location is within a hexagon.
     */
    private static boolean isInHexagon(Location location, Location min, Location max) {
        double centerX = (min.getX() + max.getX()) / 2;
        double centerZ = (min.getZ() + max.getZ()) / 2;
        double radius = Math.min(
            Math.abs(max.getX() - min.getX()) / 2,
            Math.abs(max.getZ() - min.getZ()) / 2
        );
        
        double dx = Math.abs(location.getX() - centerX);
        double dz = Math.abs(location.getZ() - centerZ);
        
        // Hexagon check: point is inside if dx <= radius and dz <= (radius - dx/2)
        return dx <= radius && dz <= (radius - dx / 2);
    }
    
    /**
     * Check if location is within a polygon.
     * Uses the ray-casting algorithm for point-in-polygon testing.
     */
    private static boolean isInPolygon(Location location, Area area) {
        List<Location> polygonPoints = area.getPolygonPoints();
        if (polygonPoints.size() < 3) {
            // Not enough points for a valid polygon, fall back to AABB
            return true;
        }
        
        // Convert to 2D points for polygon check
        List<com.worldprotect.util.PolygonGeometry.Point2D> polygon2D = new ArrayList<>();
        for (Location point : polygonPoints) {
            polygon2D.add(new com.worldprotect.util.PolygonGeometry.Point2D(point));
        }
        
        // Use block center coordinates for consistent boundary checks
        // This ensures that when a player clicks a block at (10, 64, 20),
        // the polygon boundary passes through the center of that block (10.5, 64, 20.5)
        com.worldprotect.util.PolygonGeometry.Point2D testPoint = 
            new com.worldprotect.util.PolygonGeometry.Point2D(
                location.getBlockX() + 0.5,
                location.getBlockZ() + 0.5
            );
        
        return com.worldprotect.util.PolygonGeometry.isPointInPolygon(testPoint, polygon2D);
    }
    
    /**
     * Check if location is within the border of a shape.
     */
    private static boolean isInBorder(Location location, Location min, Location max, 
                                     int borderThickness, Area.Shape shape, Area area) {
        // For border style, we need to check if point is near the edge of the actual shape
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        
        switch (shape) {
            case SQUARE:
                // For square, check distance to nearest edge
                double distToMinX = Math.abs(x - min.getX());
                double distToMaxX = Math.abs(x - max.getX());
                double distToMinZ = Math.abs(z - min.getZ());
                double distToMaxZ = Math.abs(z - max.getZ());
                
                // Find minimum distance to any edge in XZ plane
                double minDistToEdge = Math.min(
                    Math.min(distToMinX, distToMaxX),
                    Math.min(distToMinZ, distToMaxZ)
                );
                
                return minDistToEdge <= borderThickness;
                
            case CIRCLE:
                // For circle, check distance to circumference
                double centerX = (min.getX() + max.getX()) / 2;
                double centerZ = (min.getZ() + max.getZ()) / 2;
                double radiusX = Math.abs(max.getX() - min.getX()) / 2;
                double radiusZ = Math.abs(max.getZ() - min.getZ()) / 2;
                double radius = Math.min(radiusX, radiusZ);
                
                double dx = x - centerX;
                double dz = z - centerZ;
                double distanceFromCenter = Math.sqrt(dx * dx + dz * dz);
                
                // Check if point is within border thickness of the circumference
                return Math.abs(distanceFromCenter - radius) <= borderThickness;
                
            case TRIANGLE:
                // For triangle, we need to check distance to triangle edges
                // Create triangle points
                Vector p1 = new Vector(min.getX(), min.getY(), min.getZ());
                Vector p2 = new Vector(max.getX(), min.getY(), min.getZ());
                Vector p3 = new Vector(min.getX(), min.getY(), max.getZ());
                Vector point = location.toVector();
                
                // Calculate distance to each edge
                double distToEdge1 = distanceToLineSegment(point, p1, p2);
                double distToEdge2 = distanceToLineSegment(point, p2, p3);
                double distToEdge3 = distanceToLineSegment(point, p3, p1);
                
                double minDistToTriangleEdge = Math.min(
                    Math.min(distToEdge1, distToEdge2),
                    distToEdge3
                );
                
                return minDistToTriangleEdge <= borderThickness;
                
            case HEXAGON:
                // For hexagon, approximate with circle distance
                double hexCenterX = (min.getX() + max.getX()) / 2;
                double hexCenterZ = (min.getZ() + max.getZ()) / 2;
                double hexRadius = Math.min(
                    Math.abs(max.getX() - min.getX()) / 2,
                    Math.abs(max.getZ() - min.getZ()) / 2
                );
                
                double hexDx = x - hexCenterX;
                double hexDz = z - hexCenterZ;
                double hexDistance = Math.sqrt(hexDx * hexDx + hexDz * hexDz);
                
                // Hexagon border is approximately circular
                return Math.abs(hexDistance - hexRadius) <= borderThickness;
                
            case POLYGON:
                // For polygon, calculate distance to polygon boundary
                List<Location> polygonPoints = area.getPolygonPoints();
                if (polygonPoints.size() < 3) {
                    // Not enough points for a valid polygon, fall back to AABB check
                    double distToMinXPoly = Math.abs(x - min.getX());
                    double distToMaxXPoly = Math.abs(x - max.getX());
                    double distToMinZPoly = Math.abs(z - min.getZ());
                    double distToMaxZPoly = Math.abs(z - max.getZ());
                    
                    double minDistToEdgePoly = Math.min(
                        Math.min(distToMinXPoly, distToMaxXPoly),
                        Math.min(distToMinZPoly, distToMaxZPoly)
                    );
                    
                    return minDistToEdgePoly <= borderThickness;
                }
                
                // Convert to 2D points for polygon distance calculation
                List<com.worldprotect.util.PolygonGeometry.Point2D> polygon2D = new ArrayList<>();
                for (Location polyPoint : polygonPoints) {
                    polygon2D.add(new com.worldprotect.util.PolygonGeometry.Point2D(polyPoint));
                }
                
                // Use block center coordinates for consistent boundary checks
                com.worldprotect.util.PolygonGeometry.Point2D testPoint = 
                    new com.worldprotect.util.PolygonGeometry.Point2D(
                        location.getBlockX() + 0.5,
                        location.getBlockZ() + 0.5
                    );
                
                double distanceToBoundary = com.worldprotect.util.PolygonGeometry.distanceToPolygonBoundary(testPoint, polygon2D);
                return distanceToBoundary <= borderThickness;
                
            default:
                // Fallback to AABB check
                double distToMinXAABB = Math.abs(x - min.getX());
                double distToMaxXAABB = Math.abs(x - max.getX());
                double distToMinYAABB = Math.abs(y - min.getY());
                double distToMaxYAABB = Math.abs(y - max.getY());
                double distToMinZAABB = Math.abs(z - min.getZ());
                double distToMaxZAABB = Math.abs(z - max.getZ());
                
                double minDistToFace = Math.min(
                    Math.min(distToMinXAABB, distToMaxXAABB),
                    Math.min(
                        Math.min(distToMinYAABB, distToMaxYAABB),
                        Math.min(distToMinZAABB, distToMaxZAABB)
                    )
                );
                
                return minDistToFace <= borderThickness;
        }
    }
    
    /**
     * Calculate distance from a point to a line segment.
     */
    private static double distanceToLineSegment(Vector point, Vector lineStart, Vector lineEnd) {
        Vector line = lineEnd.subtract(lineStart);
        double lineLength = line.length();
        Vector lineDir = line.normalize();
        
        Vector pointToStart = point.subtract(lineStart);
        double projection = pointToStart.dot(lineDir);
        
        if (projection <= 0) {
            // Point is before start of segment
            return pointToStart.length();
        } else if (projection >= lineLength) {
            // Point is after end of segment
            return point.subtract(lineEnd).length();
        } else {
            // Point is within segment, calculate perpendicular distance
            Vector closestPoint = lineStart.add(lineDir.multiply(projection));
            return point.subtract(closestPoint).length();
        }
    }
    
    /**
     * Get points for visualizing a shape.
     */
    public static List<Location> getShapePoints(Area area, int pointsPerSide) {
        List<Location> result = new ArrayList<>();
        Location min = area.getMinBounds();
        Location max = area.getMaxBounds();
        double centerX = (min.getX() + max.getX()) / 2;
        double centerZ = (min.getZ() + max.getZ()) / 2;
        double radiusX = Math.abs(max.getX() - min.getX()) / 2;
        double radiusZ = Math.abs(max.getZ() - min.getZ()) / 2;
        double radius = Math.min(radiusX, radiusZ);
        
        switch (area.getShape()) {
            case SQUARE:
                // Square: 4 corners
                result.add(new Location(min.getWorld(), min.getX(), min.getY(), min.getZ()));
                result.add(new Location(min.getWorld(), max.getX(), min.getY(), min.getZ()));
                result.add(new Location(min.getWorld(), max.getX(), min.getY(), max.getZ()));
                result.add(new Location(min.getWorld(), min.getX(), min.getY(), max.getZ()));
                break;
                
            case CIRCLE:
                // Circle: points around circumference
                for (int i = 0; i < pointsPerSide; i++) {
                    double angle = 2 * Math.PI * i / pointsPerSide;
                    double x = centerX + radius * Math.cos(angle);
                    double z = centerZ + radius * Math.sin(angle);
                    result.add(new Location(min.getWorld(), x, min.getY(), z));
                }
                break;
                
            case TRIANGLE:
                // Triangle: 3 corners
                result.add(new Location(min.getWorld(), min.getX(), min.getY(), min.getZ()));
                result.add(new Location(min.getWorld(), max.getX(), min.getY(), min.getZ()));
                result.add(new Location(min.getWorld(), min.getX(), min.getY(), max.getZ()));
                break;
                
            case HEXAGON:
                // Hexagon: 6 corners
                for (int i = 0; i < 6; i++) {
                    double angle = 2 * Math.PI * i / 6;
                    double x = centerX + radius * Math.cos(angle);
                    double z = centerZ + radius * Math.sin(angle);
                    result.add(new Location(min.getWorld(), x, min.getY(), z));
                }
                break;
        }
        
        return result;
    }
}