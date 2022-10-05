package com.marcusslover.plus.lib.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * WorldPoints allow the storing of sets of coordinates without dealing with
 * chunk loading or world name storage. Unlike {@link Vector}, Positions allow
 * us to store a {@link #yaw} and {@link #pitch} as well as being easily serializable.
 */
public class WorldPoint {

    private static final DecimalFormat df = new DecimalFormat("#.00");
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public WorldPoint() {
        this.yaw = 0;
        this.pitch = 0;
    }

    public WorldPoint(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public WorldPoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0;
        this.pitch = 0;
    }

    public WorldPoint(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public WorldPoint(String string) {
        String[] parts = string.split(",");

        this.x = Double.parseDouble(parts[0].replaceAll("[^\\d-.]", ""));
        this.y = Double.parseDouble(parts[1]);
        this.z = Double.parseDouble(parts[2]);
        this.yaw = Float.parseFloat(parts[3]);
        this.pitch = Float.parseFloat(parts[4]);
    }

    /**
     * Creates a new Location object from this WorldPoint.
     *
     * @param world some valid bukkit world
     * @return Location
     */
    public Location asLocation(World world) {
        return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    /**
     * Computes the distance between this WorldPoint and WorldPoint {@code (x1, y1, z1)}.
     *
     * @param x1 the x coordinate of other WorldPoint
     * @param y1 the y coordinate of other WorldPoint
     * @param z1 the z coordinate of other WorldPoint
     * @return the distance between this WorldPoint and WorldPoint {@code (x1, y1, z1)}.
     */
    public double distance(double x1, double y1, double z1) {
        double a = this.getX() - x1;
        double b = this.getY() - y1;
        double c = this.getZ() - z1;
        return Math.sqrt(a * a + b * b + c * c);
    }

    /**
     * Computes the distance between this WorldPoint and the specified {@code WorldPoint}.
     *
     * @param worldPoint the other WorldPoint
     * @return the distance between this WorldPoint and the specified {@code WorldPoint}.
     * @throws NullPointerException if the specified {@code WorldPoint} is null
     */
    public double distance(WorldPoint worldPoint) {
        return this.distance(worldPoint.getX(), worldPoint.getY(), worldPoint.getZ());
    }

    /**
     * Computes the distance between this WorldPoint and WorldPoint {@code (x1, y1, z1)}.
     *
     * @param x1 the x coordinate of other WorldPoint
     * @param y1 the y coordinate of other WorldPoint
     * @param z1 the z coordinate of other WorldPoint
     * @return the distance between this WorldPoint and WorldPoint {@code (x1, y1, z1)}.
     */
    public double distanceSquared(double x1, double y1, double z1) {
        double a = this.getX() - x1;
        double b = this.getY() - y1;
        double c = this.getZ() - z1;
        return a * a + b * b + c * c;
    }

    /**
     * Computes the distance between this WorldPoint and the specified {@code WorldPoint}.
     *
     * @param worldPoint the other WorldPoint
     * @return the distance between this WorldPoint and the specified {@code WorldPoint}.
     * @throws NullPointerException if the specified {@code WorldPoint} is null
     */
    public double distanceSquared(WorldPoint worldPoint) {
        return this.distanceSquared(worldPoint.getX(), worldPoint.getY(), worldPoint.getZ());
    }

    /**
     * Add a WorldPoint to WorldPoint
     *
     * @param worldPoint WorldPoint to add
     * @return A WorldPoint
     */
    public WorldPoint add(WorldPoint worldPoint) {
        return new WorldPoint(this.x + worldPoint.getX(), this.y + worldPoint.getY(), this.z + worldPoint.getZ());
    }

    /**
     * Add x, y, z to WorldPoint
     *
     * @param x X amount to add
     * @param y Y amount to add
     * @param z Z amount to add
     * @return A WorldPoint
     */
    public WorldPoint add(double x, double y, double z) {
        return new WorldPoint(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Subtract a WorldPoint to WorldPoint
     *
     * @param worldPoint WorldPoint to subtract
     * @return A WorldPoint
     */
    public WorldPoint subtract(WorldPoint worldPoint) {
        return new WorldPoint(this.x - worldPoint.getX(), this.y - worldPoint.getY(), this.z - worldPoint.getZ());
    }

    /**
     * Add x, y, z to WorldPoint
     *
     * @param x X amount to subtract
     * @param y Y amount to subtract
     * @param z Z amount to subtract
     * @return A WorldPoint
     */
    public WorldPoint subtract(double x, double y, double z) {
        return new WorldPoint(this.x - x, this.y - y, this.z - z);
    }

    /**
     * @param min Minimum WorldPoint
     * @param max Maximum WorldPoint
     * @return A boolean
     */
    public boolean isBetween(WorldPoint min, WorldPoint max) {
        return this.x > min.getX() && this.x < max.getX()
               && this.y > min.getY() && this.y < max.getY()
               && this.z > min.getZ() && this.z < max.getZ();
    }

    /**
     * Returns a position which lies in the middle between this position and the
     * specified coordinates.
     *
     * @param x the X coordinate of the second endpoint
     * @param y the Y coordinate of the second endpoint
     * @param z the Z coordinate of the second endpoint
     * @return the position in the middle
     */
    public WorldPoint midpoint(double x, double y, double z) {
        return new WorldPoint(
                x + (this.getX() - x) / 2.0,
                y + (this.getY() - y) / 2.0,
                z + (this.getZ() - z) / 2.0);
    }

    /**
     * Returns a WorldPoint which lies in the middle between this WorldPoint and the
     * specified WorldPoint.
     *
     * @param worldPoint the other endpoint
     * @return the WorldPoint in the middle
     * @throws NullPointerException if the specified {@code WorldPoint} is null
     */
    public WorldPoint midpoint(WorldPoint worldPoint) {
        return this.midpoint(worldPoint.getX(), worldPoint.getY(), worldPoint.getZ());
    }

    /**
     * @return A {@link Vector}
     */
    public Vector toVector() {
        return new Vector(this.x, this.y, this.z);
    }

    /**
     * Check if the chunk is loaded in the world for this {@link WorldPoint}
     *
     * @param world World to use
     * @return A boolean
     */
    public boolean isChunkLoaded(World world) {
        return world != null && world.isChunkLoaded((int) this.x >> 4, (int) this.z >> 4);
    }

    public int getBlockX() {
        return (int) Math.floor(this.x);
    }

    public int getBlockY() {
        return (int) Math.floor(this.y);
    }

    public int getBlockZ() {
        return (int) Math.floor(this.z);
    }

    @Override
    public String toString() {
        return WorldPoint.df.format(this.x) + "," + WorldPoint.df.format(this.y) + "," + WorldPoint.df.format(this.z) + "," + WorldPoint.df.format(this.yaw) + "," + WorldPoint.df.format(this.pitch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        WorldPoint worldPoint = (WorldPoint) o;
        return Double.compare(worldPoint.x, this.x) == 0
               && Double.compare(worldPoint.y, this.y) == 0
               && Double.compare(worldPoint.z, this.z) == 0
               && Float.compare(worldPoint.yaw, this.yaw) == 0
               && Float.compare(worldPoint.pitch, this.pitch) == 0;
    }

    /**
     * WorldPoints allow the storing of sets of coordinates without dealing with
     * chunk loading or world name storage. Unlike {@link Vector}, Positions allow
     * us to store a {@link #yaw} and {@link #pitch} as well as being easily serializable.
     */
    public WorldPoint of(double x, double y, double z) {
        return new WorldPoint(x, y, z);
    }

    /**
     * WorldPoints allow the storing of sets of coordinates without dealing with
     * chunk loading or world name storage. Unlike {@link Vector}, Positions allow
     * us to store a {@link #yaw} and {@link #pitch} as well as being easily serializable.
     */
    public WorldPoint of(double x, double y, double z, float yaw, float pitch) {
        return new WorldPoint(x, y, z, yaw, pitch);
    }

    /**
     * WorldPoints allow the storing of sets of coordinates without dealing with
     * chunk loading or world name storage. Unlike {@link Vector}, Positions allow
     * us to store a {@link #yaw} and {@link #pitch} as well as being easily serializable.
     */
    public WorldPoint of(Location location) {
        return new WorldPoint(location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}