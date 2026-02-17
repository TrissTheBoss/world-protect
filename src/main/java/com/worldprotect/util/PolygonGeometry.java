package com.worldprotect.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for polygon geometry calculations.
 * Provides point-in-polygon and distance-to-boundary algorithms for polygon selections.
 */
public class PolygonGeometry {
    
    private PolygonGeometry() {
        // Utility class - no instantiation
    }
    
    /**
     * Represents a 2D point for polygon calculations.
     */
    public static class Point2D {
        public final double x;
        public final double z;
        
        public Point2D(double x, double z) {
            this.x = x;
            this.z = z;
        }
        
        public Point2D(Location location) {
            // Convert location coordinates to block centers for consistent polygon boundaries
            // If coordinates are integers (old-style corner points), add 0.5 to convert to center
            double locX = location.getX();
            double locZ = location.getZ();
            
            // Check if coordinates appear to be block corners (integers within tolerance)
            // Use a small epsilon to account for floating point precision
            final double EPSILON = 0.0001;
            if (Math.abs(locX - Math.round(locX)) < EPSILON) {
                // Integer coordinate, likely a block corner from old data
                this.x = Math.round(locX) + 0.5;
            } else {
                this.x = locX;
            }
            
            if (Math.abs(locZ - Math.round(locZ)) < EPSILON) {
                // Integer coordinate, likely a block corner from old data
                this.z = Math.round(locZ) + 0.5;
            } else {
                this.z = locZ;
            }
        }
        
        @Override
        public String toString() {
            return String.format("(%.1f, %.1f)", x, z);
        }
    }
    
    /**
     * Check if a point is inside a polygon using the ray-casting algorithm.
     * 
     * @param testPoint The point to test
     * @param polygon Ordered list of polygon vertices (must be closed: last point connects to first)
     * @return true if the point is inside the polygon, false otherwise
     */
    public static boolean isPointInPolygon(Point2D testPoint, List<Point2D> polygon) {
        if (polygon.size() < 3) {
            return false; // Not a valid polygon
        }
        
        int intersections = 0;
        int n = polygon.size();
        
        for (int i = 0; i < n; i++) {
            Point2D p1 = polygon.get(i);
            Point2D p2 = polygon.get((i + 1) % n); // Wrap around to close polygon
            
            // Check if the edge straddles the test point's z-coordinate
            if ((p1.z > testPoint.z) != (p2.z > testPoint.z)) {
                // Calculate x-coordinate of intersection
                double xIntersection = (p2.x - p1.x) * (testPoint.z - p1.z) / (p2.z - p1.z) + p1.x;
                
                // If intersection is to the right of test point, count it
                if (testPoint.x < xIntersection) {
                    intersections++;
                }
            }
        }
        
        // Odd number of intersections = inside, even = outside
        return (intersections % 2) == 1;
    }
    
    /**
     * Check if a location is inside a polygon (3D version with Y bounds check).
     * 
     * @param location The location to test
     * @param polygonPoints Ordered list of polygon vertices as Locations
     * @param minY Minimum Y coordinate of the polygon area
     * @param maxY Maximum Y coordinate of the polygon area
     * @return true if the location is inside the polygon bounds, false otherwise
     */
    public static boolean isLocationInPolygon(Location location, List<Location> polygonPoints, 
                                              double minY, double maxY) {
        // Check Y bounds first
        double y = location.getY();
        if (y < minY || y > maxY) {
            return false;
        }
        
        // Convert to 2D points for polygon check
        List<Point2D> polygon2D = new ArrayList<>();
        for (Location point : polygonPoints) {
            polygon2D.add(new Point2D(point));
        }
        
        Point2D testPoint = new Point2D(location);
        return isPointInPolygon(testPoint, polygon2D);
    }
    
    /**
     * Calculate the distance from a point to the nearest edge of a polygon.
     * 
     * @param testPoint The point to test
     * @param polygon Ordered list of polygon vertices (must be closed: last point connects to first)
     * @return The minimum distance to any edge of the polygon
     */
    public static double distanceToPolygonBoundary(Point2D testPoint, List<Point2D> polygon) {
        if (polygon.size() < 2) {
            return Double.POSITIVE_INFINITY; // Not a valid polygon
        }
        
        double minDist = Double.POSITIVE_INFINITY;
        int n = polygon.size();
        
        for (int i = 0; i < n; i++) {
            Point2D p1 = polygon.get(i);
            Point2D p2 = polygon.get((i + 1) % n); // Wrap around to close polygon
            
            double dist = distanceToSegment(testPoint, p1, p2);
            if (dist < minDist) {
                minDist = dist;
            }
        }
        
        return minDist;
    }
    
    /**
     * Calculate the distance from a point to a line segment.
     * 
     * @param p The point
     * @param a Start of line segment
     * @param b End of line segment
     * @return Distance from point to line segment
     */
    public static double distanceToSegment(Point2D p, Point2D a, Point2D b) {
        double dx = b.x - a.x;
        double dz = b.z - a.z;
        double lengthSquared = dx * dx + dz * dz;
        
        // If segment has zero length, return distance to point a
        if (lengthSquared == 0) {
            return distance(p, a);
        }
        
        // Calculate projection parameter t
        double t = ((p.x - a.x) * dx + (p.z - a.z) * dz) / lengthSquared;
        
        // Clamp t to [0, 1] to stay within segment
        t = Math.max(0, Math.min(1, t));
        
        // Calculate projection point
        Point2D projection = new Point2D(a.x + t * dx, a.z + t * dz);
        
        return distance(p, projection);
    }
    
    /**
     * Calculate Euclidean distance between two points.
     */
    public static double distance(Point2D a, Point2D b) {
        double dx = a.x - b.x;
        double dz = a.z - b.z;
        return Math.sqrt(dx * dx + dz * dz);
    }
    
    /**
     * Calculate the area of a polygon using the shoelace formula.
     * 
     * @param polygon Ordered list of polygon vertices
     * @return The area of the polygon (positive for counter-clockwise order)
     */
    public static double calculatePolygonArea(List<Point2D> polygon) {
        if (polygon.size() < 3) {
            return 0;
        }
        
        double area = 0;
        int n = polygon.size();
        
        for (int i = 0; i < n; i++) {
            Point2D p1 = polygon.get(i);
            Point2D p2 = polygon.get((i + 1) % n);
            area += (p1.x * p2.z - p2.x * p1.z);
        }
        
        return Math.abs(area) / 2.0;
    }
    
    /**
     * Check if a polygon is convex.
     * 
     * @param polygon Ordered list of polygon vertices
     * @return true if the polygon is convex, false if concave
     */
    public static boolean isConvexPolygon(List<Point2D> polygon) {
        if (polygon.size() < 3) {
            return false;
        }
        
        int n = polygon.size();
        boolean signSet = false;
        boolean sign = false;
        
        for (int i = 0; i < n; i++) {
            Point2D p0 = polygon.get(i);
            Point2D p1 = polygon.get((i + 1) % n);
            Point2D p2 = polygon.get((i + 2) % n);
            
            // Calculate cross product
            double cross = (p1.x - p0.x) * (p2.z - p1.z) - (p1.z - p0.z) * (p2.x - p1.x);
            
            if (cross != 0) {
                boolean currentSign = cross > 0;
                if (!signSet) {
                    sign = currentSign;
                    signSet = true;
                } else if (currentSign != sign) {
                    return false; // Sign changed, polygon is concave
                }
            }
        }
        
        return true;
    }
    
    /**
     * Get the bounding box of a polygon.
     * 
     * @param polygon Ordered list of polygon vertices
     * @return Array of [minX, minZ, maxX, maxZ]
     */
    public static double[] getPolygonBounds(List<Point2D> polygon) {
        if (polygon.isEmpty()) {
            return new double[]{0, 0, 0, 0};
        }
        
        double minX = polygon.get(0).x;
        double minZ = polygon.get(0).z;
        double maxX = polygon.get(0).x;
        double maxZ = polygon.get(0).z;
        
        for (Point2D point : polygon) {
            minX = Math.min(minX, point.x);
            minZ = Math.min(minZ, point.z);
            maxX = Math.max(maxX, point.x);
            maxZ = Math.max(maxZ, point.z);
        }
        
        return new double[]{minX, minZ, maxX, maxZ};
    }
    
    /**
     * Calculate the centroid (center of mass) of a polygon.
     * 
     * @param polygon Ordered list of polygon vertices
     * @return The centroid point
     */
    public static Point2D calculateCentroid(List<Point2D> polygon) {
        if (polygon.isEmpty()) {
            return new Point2D(0, 0);
        }
        
        double area = 0;
        double centroidX = 0;
        double centroidZ = 0;
        int n = polygon.size();
        
        for (int i = 0; i < n; i++) {
            Point2D p1 = polygon.get(i);
            Point2D p2 = polygon.get((i + 1) % n);
            
            double cross = p1.x * p2.z - p2.x * p1.z;
            area += cross;
            centroidX += (p1.x + p2.x) * cross;
            centroidZ += (p1.z + p2.z) * cross;
        }
        
        area /= 2.0;
        double factor = 1.0 / (6.0 * area);
        
        centroidX *= factor;
        centroidZ *= factor;
        
        return new Point2D(centroidX, centroidZ);
    }
    
    /**
     * Simplify a polygon by removing collinear points.
     * 
     * @param polygon Original polygon vertices
     * @param tolerance Angle tolerance in radians
     * @return Simplified polygon
     */
    public static List<Point2D> simplifyPolygon(List<Point2D> polygon, double tolerance) {
        if (polygon.size() < 3) {
            return new ArrayList<>(polygon);
        }
        
        List<Point2D> simplified = new ArrayList<>();
        int n = polygon.size();
        
        for (int i = 0; i < n; i++) {
            Point2D prev = polygon.get((i - 1 + n) % n);
            Point2D curr = polygon.get(i);
            Point2D next = polygon.get((i + 1) % n);
            
            // Calculate vectors
            double v1x = curr.x - prev.x;
            double v1z = curr.z - prev.z;
            double v2x = next.x - curr.x;
            double v2z = next.z - curr.z;
            
            // Calculate angle between vectors
            double dot = v1x * v2x + v1z * v2z;
            double mag1 = Math.sqrt(v1x * v1x + v1z * v1z);
            double mag2 = Math.sqrt(v2x * v2x + v2z * v2z);
            
            if (mag1 > 0 && mag2 > 0) {
                double cosAngle = dot / (mag1 * mag2);
                double angle = Math.acos(Math.max(-1, Math.min(1, cosAngle)));
                
                // Keep point if angle is significant
                if (angle > tolerance) {
                    simplified.add(curr);
                }
            } else {
                simplified.add(curr);
            }
        }
        
        // Ensure we have at least 3 points
        if (simplified.size() < 3) {
            return new ArrayList<>(polygon.subList(0, Math.min(3, polygon.size())));
        }
        
        return simplified;
    }
}