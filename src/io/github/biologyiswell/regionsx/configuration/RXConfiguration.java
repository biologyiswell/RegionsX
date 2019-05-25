package io.github.biologyiswell.regionsx.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import lombok.Getter;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class handles the configuration from plugin,
 * loading and saving information about regions.
 *
 * @author biologyiswell (23/05/2019 21:30)
 * @since 1.0
 */
public class RXConfiguration {

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    // ... Default configuration ...

    @Getter private static double pricePerBlock = 25.0d;
    @Getter private static int minRegionSize = 10;
    @Getter private static int maxCommonHouses = 5;

    // ...

    /**
     * Enable configuration, this method will check the
     * basic process of configurations like check if
     * database exists, create default configurations
     * and load information from database.
     */
    public static void enable(JavaPlugin plugin) {
        File database = plugin.getDataFolder();
        File configuration = new File(database, "configuration.json");
        File regions = new File(database, "regions.json");

        validateDatabase(database, configuration, regions);
        validateConfiguration(configuration);
    }

    /**
     * Disable configuration, this method save the all
     * object stored information to database.
     */
    public static void disable(JavaPlugin plugin) {

    }

    // ...

    /**
     * This method will check if database folder and regions file
     * that represents the two main files from database exists.
     * If not, then create them and set the default configuration.
     */
    private static void validateDatabase(File database, File configuration, File regions) {
        if (!database.exists()) database.mkdirs();
        if (!configuration.exists()) {
            try {
                configuration.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!regions.exists()) {
            try {
                regions.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if defualt configuration is set to configuration file.
     */
    private static void validateConfiguration(File configuration) {
        try (FileReader reader = new FileReader(configuration)) {
            JsonObject objectConfiguration = GSON.fromJson(reader, JsonObject.class);

            // If the JSON content loaded from file is null,
            // then represents that has nothing configuration
            // in file, then set default configuration to file.
            if (objectConfiguration == null) {
                objectConfiguration = new JsonObject();

                objectConfiguration.addProperty("price_per_block", pricePerBlock);
                objectConfiguration.addProperty("max_common_houses", maxCommonHouses);
                objectConfiguration.addProperty("min_region_size", minRegionSize);

                try (FileWriter writer = new FileWriter(configuration)) {
                    writer.write(GSON.toJson(objectConfiguration));
                }
                return;
            }

            // Instead of, continue the process loading
            // information from file.
            pricePerBlock = objectConfiguration.get("price_per_block").getAsDouble();
            maxCommonHouses = objectConfiguration.get("max_common_houses").getAsInt();
            minRegionSize = objectConfiguration.get("min_region_size").getAsInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
