package io.github.biologyiswell.regionsx.command;

import com.google.common.base.Preconditions;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author biologyiswell (28/06/2019 12:54)
 * @since 1.0
 */
public class RXCommandManager {

    /**
     * Enable all commands.
     */
    public static void enable(JavaPlugin plugin) {
        registerCommand(plugin, "regionsx", new RXCommand());
    }

    /**
     * Register command.
     *
     * @param name the name.
     * @param commandExecutor the command executor.
     */
    public static void registerCommand(JavaPlugin plugin, String name, CommandExecutor commandExecutor) {
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(commandExecutor, "commandExecutor");

        plugin.getCommand(name).setExecutor(commandExecutor);
    }
}
