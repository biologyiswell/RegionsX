package io.github.biologyiswell.regionsx.configuration;

import com.google.gson.JsonObject;

import io.github.biologyiswell.regionsx.RXPlugin;

import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author biologyiswell (28/06/2019 13:00)
 */
public class RXLanguageSupport {

    // ... Command ...
    @Getter private static String onlyPlayersExecuteCommand;
    @Getter private static String createRegionCommandDescription;
    @Getter private static String deleteRegionCommandDescription;
    @Getter private static String regionsCommandDescription;

    // ... Listener ...
    @Getter private static String cantBreakBlock;
    @Getter private static String cantPlaceBlock;
    @Getter private static String cantUseBlock;

    // ...

    /**
     * Enable all configurations from language support.
     */
    protected static void enable() {
        final File lang = new File("lang");
        if (!lang.exists()) lang.mkdirs();

        // Load language messages.
        try {
            loadLanguage();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method load the all messages from
     * the specified langauge.
     */
    private static void loadLanguage() throws FileNotFoundException {
        final File langFile = new File("lang", RXConfiguration.getLang() + ".lang");

        if (!langFile.exists()) {
            throw new FileNotFoundException("Language file \"" + langFile.getName() + "\" not found.");
        }

        final JsonObject languageObject;

        try (final FileReader reader = new FileReader(langFile)) {
            languageObject = RXConfiguration.GSON.fromJson(reader, JsonObject.class);
        } catch (final IOException e) {
            RXPlugin.getInstance().getLogger().severe("Can\'t load language file \"" + langFile.getAbsolutePath() + "\".");
            e.printStackTrace();
            return;
        }

        onlyPlayersExecuteCommand = languageObject.get("only_players_execute_command").getAsString();
        createRegionCommandDescription = languageObject.get("create_region_command_description").getAsString();
        deleteRegionCommandDescription = languageObject.get("delete_region_command_description").getAsString();
        regionsCommandDescription = languageObject.get("regions_command_description").getAsString();

        cantBreakBlock = languageObject.get("cant_break_block").getAsString();
        cantPlaceBlock = languageObject.get("cant_place_block").getAsString();
        cantUseBlock = languageObject.get("cant_use_block").getAsString();
    }
}
