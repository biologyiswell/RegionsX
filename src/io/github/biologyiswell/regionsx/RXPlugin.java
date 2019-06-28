package io.github.biologyiswell.regionsx;

import io.github.biologyiswell.regionsx.command.RXCommandManager;
import io.github.biologyiswell.regionsx.configuration.RXConfiguration;
import io.github.biologyiswell.regionsx.listener.RXListenerManager;
import io.github.biologyiswell.regionsx.region.RXRegionManager;

import lombok.Getter;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class represents the main instance of project.
 * That handle the plugin and your functions.
 *
 * @author biologyiswell (23/05/2019 21:28)
 * @since 1.0
 */
public class RXPlugin extends JavaPlugin {

    /**
     * Instance, used to give access to classes
     * that are out from this package and need
     * access functions from plugin.
     */
    @Getter private static RXPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        RXConfiguration.enable(this);
        RXRegionManager.enable(this);
        RXListenerManager.enable(this);
        RXCommandManager.enable(this);
    }

    @Override
    public void onDisable() {
        RXConfiguration.disable(this);
        RXRegionManager.disable(this);
    }
}
