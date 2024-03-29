package com.marcusslover.plus.lib.world;

import com.marcusslover.plus.lib.events.BundledListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class LocationUtils {
    private static final Map<String, List<Consumer<World>>> waiting = new HashMap<>();
    private static boolean initialized = false;

    /**
     * Gets the chunk X and Z of a location
     *
     * @param loc The location to get the chunk coordinates of
     * @return An array containing the chunk coordinates [x, z]
     */
    public static int[] getChunkCoordinates(Location loc) {
        return new int[]{loc.getBlockX() >> 4, loc.getBlockZ() >> 4};
    }

    /**
     * Loads a Location from a String. If the world this Location is in is not yet loaded, waits for it to load, then passes
     * the Location to the callback.
     *
     * @param string    The String to be parsed into a Location
     * @param separator The separator used when converting this Location to a String
     * @param callback  The callback to use the Location once it has been loaded
     */
    public static void fromStringLater(String string, String separator, Consumer<Location> callback) {
        String[] split = string.split(Pattern.quote(separator));
        World world = Bukkit.getWorld(split[0]);
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        if (world != null) {
            callback.accept(new Location(world, x, y, z));
            return;
        }
        new BundledListener<>(WorldLoadEvent.class, (l, e) -> {
            if (e.getWorld().getName().equals(split[0])) {
                World w = Bukkit.getWorld(split[0]);
                callback.accept(new Location(w, x, y, z));
                l.unregister();
            }
        });
    }

    /**
     * Converts a String back to a Location
     *
     * @param string    The stringified Location
     * @param separator The separator that was used in toString
     * @return The Location
     */
    public static Location fromString(String string, String separator) {
        String[] split = string.split(Pattern.quote(separator));
        World world = Bukkit.getWorld(split[0]);
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        Location location = new Location(world, x, y, z);
        if (world == null) {
            waitForWorld(split[0], location::setWorld);
        }
        return location;
    }

    /**
     * Converts a String back to a Location. The same as calling fromString(String, " ")
     *
     * @param string The stringified Location
     * @return The Location
     */
    public static Location fromString(String string) {
        return fromString(string, " ");
    }

    /**
     * Converts a String back to a Location
     *
     * @param string    The stringified Location
     * @param separator The separator that was used in toString
     * @return The Location
     */
    public static Location fromAccurateString(String string, String separator) {
        String[] split = string.split(Pattern.quote(separator));
        World world = Bukkit.getWorld(split[0]);
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        float yaw = (float) Double.parseDouble(split[4]);
        float pitch = (float) Double.parseDouble(split[5]);
        Location location = new Location(world, x, y, z, yaw, pitch);
        if (world == null) {
            waitForWorld(split[0], location::setWorld);
        }
        return location;
    }

    /**
     * Converts a String back to a Location. The same as calling fromString(String, " ")
     *
     * @param string The stringified Location
     * @return The Location
     */
    public static Location fromAccurateString(String string) {
        return fromAccurateString(string, " ");
    }

    /**
     * Converts a Location to a precise String
     *
     * @param loc       The Location to be stringified
     * @param separator The separator to use between pieces of information
     * @return The stringified Location
     */
    public static String toAccurateString(Location loc, String separator) {
        return loc.getWorld().getName() + separator +
                loc.getX() + separator +
                loc.getY() + separator +
                loc.getZ() + separator +
                loc.getYaw() + separator +
                loc.getPitch();
    }

    /**
     * Converts a Location to a precise String
     *
     * @param separator The separator to use between pieces of information
     * @return The stringified Location
     */
    public static String toAccurateString(String world, double x, double y, double z, float yaw, float pitch, String separator) {
        return world + separator +
                x + separator +
                y + separator +
                z + separator +
                yaw + separator +
                pitch;
    }

    /**
     * Converts a Location to a String
     *
     * @param loc       The Location to be stringified
     * @param separator The separator to use between pieces of information
     * @return The stringified Location
     */
    public static String toString(Location loc, String separator) {
        return loc.getWorld().getName() + separator +
                loc.getX() + separator +
                loc.getY() + separator +
                loc.getZ();
    }

    /**
     * Converts a Location to a String representing its location
     *
     * @param block     The Block location to be stringified
     * @param separator The separator to use between pieces of information
     * @return The stringified location
     */
    public static String toString(Block block, String separator) {
        return block.getWorld().getName() + separator +
                block.getX() + separator +
                block.getY() + separator +
                block.getZ();
    }

    /**
     * Converts a Location to a String representing its location
     *
     * @param block The Block location to be stringified
     * @return The stringified location
     */
    public static String toString(Block block) {
        return toString(block, " ");
    }

    /**
     * Loads a Location from a String. If the world this Location is in is not yet loaded, waits for it to load, then passes
     * the Location to the callback.
     *
     * @param string   The String to be parsed into a Location
     * @param callback The callback to use the Location once it has been loaded
     */
    public static void fromStringLater(String string, Consumer<Location> callback) {
        fromStringLater(string, " ", callback);
    }

    /**
     * Converts a Location to a String. The same as calling toString(Location, " ")
     *
     * @param loc The Location to be stringified
     * @return The stringified Location
     */
    public static String toString(Location loc) {
        return toString(loc, " ");
    }

    private static void initializeListener() {
        if (initialized) {
            return;
        }
        initialized = true;
        new BundledListener<>(WorldLoadEvent.class, e -> {
            List<Consumer<World>> list = waiting.remove(e.getWorld().getName());
            if (list == null) {
                return;
            }
            list.forEach(c -> c.accept(e.getWorld()));
        });
    }

    /**
     * Waits for a world with the given name to load before calling the callback
     *
     * @param worldname The name of the world
     * @param callback  A callback to be passed the world when it loads
     */
    public static void waitForWorld(String worldname, Consumer<World> callback) {
        World world = Bukkit.getWorld(worldname);
        if (world != null) {
            callback.accept(world);
            return;
        }
        waiting.putIfAbsent(worldname, new ArrayList<>());
        List<Consumer<World>> list = waiting.get(worldname);
        list.add(callback);
        initializeListener();
    }

    /**
     * This will +/- 0.5 of an exact cord to get the center of the block
     */
    public static double getCenterCord(int i) {
        double d = i;
        d = d < 0 ? d - .5 : d + .5;
        return d;
    }

    /**
     * Gets the Vector direction of a BlockFace. For use in versions below 1.13.
     *
     * @param face The block face
     * @return The vector representing the direction
     */
    public static Vector getDirection(BlockFace face) {
        return new Vector(face.getModX(), face.getModY(), face.getModZ());
    }

    /**
     * This will modify the passed in location to be at the center of the block.
     */
    public Location centerLocation(Location location) {
        location.setX(getCenterCord(location.getBlockX()));
        location.setY(getCenterCord(location.getBlockY()));
        location.setZ(getCenterCord(location.getBlockZ()));
        return location;
    }

    /**
     * Returns a list of locations that are in a circle around the given center.
     */
    protected @NotNull List<Location> getCircle(@NotNull Location center, double radius, int amount) {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z, center.getYaw(), center.getPitch()));
        }
        return locations;
    }
}