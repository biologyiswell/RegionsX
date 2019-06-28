package io.github.biologyiswell.regionsx.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author biologyiswell (28/06/2019 12:57)
 * @since 1.0
 */
public class RXListenerManager {

    /**
     * Enable all listeners.
     */
    public static void enable(JavaPlugin plugin) {
        registerListener(plugin, new RXListener());
    }


    /**
     * Register listener.
     *
     * @param plugin the plugin.
     * @param listener the listener.
     */
    public static void registerListener(JavaPlugin plugin, Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }
}
