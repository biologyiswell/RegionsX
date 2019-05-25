package io.github.biologyiswell.regionsx;

import io.github.biologyiswell.regionsx.command.RXCommand;
import io.github.biologyiswell.regionsx.configuration.RXConfiguration;
import io.github.biologyiswell.regionsx.listener.RXListener;
import io.github.biologyiswell.regionsx.region.RXRegions;

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
        RXRegions.enable(this);
        RXListener.enable(this);
        RXCommand.enable(this);
    }

    @Override
    public void onDisable() {
        RXConfiguration.disable(this);
        RXRegions.disable(this);
    }
}
