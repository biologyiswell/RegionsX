package io.github.biologyiswell.regionsx.region;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import io.github.biologyiswell.regionsx.RXPlugin;
import io.github.biologyiswell.regionsx.configuration.RXConfiguration;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class handle the all regions
 * stored in plugin.
 *
 * @author biologyiswell (24/05/2018 20:00)
 * @since 1.0
 */
public class RXRegions {

    /**
     * This list store the all regions registered
     * in server.
     */
    private static final List<Region> REGION_LIST = new ArrayList<>();

    /**
     * Enable region handler. This method will
     * initialize the main configuration of region
     * handler, and load all stored regions from
     * database to list.
     */
    public static void enable(JavaPlugin plugin) {
        loadRegions(plugin);
    }

    /**
     * Disable region handler. This method will save
     * all information from list to database.
     */
    public static void disable(JavaPlugin plugin) {
        saveRegions(plugin);
    }

    // ... Database ...

    /**
     * This method will load all regions stored at
     * database to the list.
     */
    private static void loadRegions(JavaPlugin plugin) {
        File regions = new File(plugin.getDataFolder(), "regions.json");

        try (FileReader reader = new FileReader(regions)) {
            JsonArray arrayData = RXConfiguration.GSON.fromJson(reader, JsonArray.class);

            // If array data is null, then this represents
            // that has nothing region to load.
            if (arrayData == null) return;

            for (JsonElement regionData : arrayData) {
                Region region = RXConfiguration.GSON.fromJson(regionData, Region.class);

                // Add the region to list.
                REGION_LIST.add(region);
            }

            RXPlugin.getInstance().getLogger().info("Um total de " + REGION_LIST.size() + " regiões foram carregadas.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will save all regions from list
     * to database.
     */
    private static void saveRegions(JavaPlugin plugin) {
        File regions = new File(plugin.getDataFolder(), "regions.json");

        try (FileWriter writer = new FileWriter(regions)) {
            JsonArray arrayData = new JsonArray();

            for (Region region : REGION_LIST) {
                arrayData.add(RXConfiguration.GSON.toJsonTree(region));
            }

            writer.write(RXConfiguration.GSON.toJson(arrayData));
            RXPlugin.getInstance().getLogger().info("Um total de " + REGION_LIST.size() + " regiões foram salvas.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ... Util ...

    /**
     * Add a region to region list.
     */
    public static void addRegion(Region region) {
        REGION_LIST.add(region);
        markRegion(region);
    }

    /**
     * Remove the region from region list.
     */
    public static void removeRegion(Region region) {
        REGION_LIST.remove(region);
    }

    /**
     * This method mark the region with fences.
     */
    private static void markRegion(Region region) {
        World world = Bukkit.getWorld(region.getWorld());
        Preconditions.checkNotNull(world, "world \"" + region.getWorld() + "\" not found.");

        for (int x = region.getMinX(); x <= region.getMaxX(); x++) {
            for (int z = region.getMinZ(); z <= region.getMaxZ(); z++) {
                // Avoid fill the area.
                if ((x > region.getMinX() && x < region.getMaxX()) && (z > region.getMinZ() && z < region.getMaxZ())) continue;

                world.getHighestBlockAt(x, z).setType(Material.OAK_FENCE);
            }
        }
    }

    /**
     * Get region from player by the name.
     */
    public static Region getRegion(Player player, String name) {
        for (Region region : REGION_LIST) {
            if (region.getOwner().equals(player.getName()) && region.getName().equals(name)) {
                return region;
            }
        }

        return null;
    }

    /**
     * Return a list with all player own regions.
     */
    public static List<Region> getPlayerRegions(Player player) {
        List<Region> regionList = null;

        for (Region region : REGION_LIST) {
            if (region.getOwner().equals(player.getName())) {
                // Lazy initialization for region list.
                if (regionList == null) regionList = new ArrayList<>();
                regionList.add(region);
            }
        }

        return regionList == null ? Collections.emptyList() : regionList;
    }

    /**
     * Check if the player has a region with this name.
     */
    public static boolean hasRegion(Player player, String name) {
        List<Region> regionList = getPlayerRegions(player);

        for (Region region : regionList) {
            if (region.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get region by location. This method will check if some region
     * registered in region list is in inside in the location.
     */
    public static Region getRegionByLocation(Location location) {
        for (Region region : REGION_LIST) {
            int minX = region.getMinX();
            int minZ = region.getMinZ();

            int maxX = region.getMaxX();
            int maxZ = region.getMaxZ();

            int lx = location.getBlockX();
            int lz = location.getBlockZ();

            if (lx >= minX && lx <= maxX && lz >= minZ && lz <= maxZ) {
                return region;
            }
        }

        return null;
    }
}
