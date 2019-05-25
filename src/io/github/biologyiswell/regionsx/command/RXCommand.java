package io.github.biologyiswell.regionsx.command;

import io.github.biologyiswell.regionsx.configuration.RXConfiguration;
import io.github.biologyiswell.regionsx.region.RXRegions;
import io.github.biologyiswell.regionsx.region.Region;
import io.github.biologyiswell.regionsx.region.RegionFlag;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * This class handle the all commands from the plugin.
 *
 * @author biologyiswell (24/05/2019 21:39)
 * @since 1.0
 */
public class RXCommand implements CommandExecutor {

    /**
     * Enable the command.
     */
    public static void enable(JavaPlugin plugin) {
        plugin.getCommand("regionsx").setExecutor(new RXCommand());
    }

    // ... Command ...

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command.");
            return false;
        }

        Player player = (Player) sender;

        // No arguments, then send the sub-commands list.
        if (args.length == 0) {
            player.sendMessage("");
            player.sendMessage(formatCommand("criar <nome> <tamanho>", "Criar uma região com um tamanho específico"));
            player.sendMessage(formatCommand("deletar <nome>", "Deletar uma região sua"));
            player.sendMessage(formatCommand("flags", "Mostrar todas as flags de permissões"));
            player.sendMessage(formatCommand("regioes", "Listar todas as suas regiões"));
            player.sendMessage(formatCommand("confiar <terreno> <nome> [flag]", "Confiar uma permissão á um jogador"));
            player.sendMessage(formatCommand("desconfiar <terreno> <nome> [flag]", "Desconfiar uma permissão de um jogador"));
            player.sendMessage("");
            return false;
        }

        String command = args[0];

        // Sub-command: create.
        if (command.equalsIgnoreCase("criar")) {
            // No args enough.
            if (args.length < 3) return invalidUsage(player, "criar <nome> <tamanho>");

            String name = args[1];
            int size = parseIntQuietly(args[2]);

            // Check if the parsed integer value fails.
            if (size == -584) return warnAndReturn(player, "O argumento tamanho precisa ser um número.");

            // Check if the player put the minimum size that a region can be has.
            if (size < RXConfiguration.getMinRegionSize())
                return warnAndReturn(player, "O tamanho mínimo de um terreno deve ser de " + RXConfiguration.getMinRegionSize() + ".");

            // Check if the player reached the maximum regions that can has.
            if (!player.isOp() && !player.hasPermission("rx.regions") && RXRegions.getPlayerRegions(player).size() == RXConfiguration.getMaxCommonHouses())
                return warnAndReturn(player, "Você já atingiu o número máximo de regiões que pode proteger (" + RXConfiguration.getMaxCommonHouses() + ").");

            // Radius from region.
            int r = size / 2;

            Location min = player.getLocation().clone().add(-r, 0, -r);
            Location max = player.getLocation().clone().add(r, 0, r);

            // ... Validate region ...

            Region localRegion = RXRegions.getRegionByLocation(min);

            // Check first if a region intercept another by the minimum location,
            // if not, then check the maximum location.
            if (localRegion != null) return warnAndReturn(player, "Uma região já existe nesta localidade.");
            else {
                localRegion = RXRegions.getRegionByLocation(max);
                if (localRegion != null) return warnAndReturn(player, "Uma região já existe nesta localidade.");
            }

            if (RXRegions.hasRegion(player, name)) return warnAndReturn(player, "Você já tem uma região com o nome de \"" + name + "\", por favor escolha outro.");

            // ... Create region ...

            // Add region to region handler, and mark the region.
            RXRegions.addRegion(new Region(player, name, min, max));

            player.sendMessage(ChatColor.GREEN + "Você protegeu uma região de " + size + "x" + size + ", com nome de \"" + name + "\".");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
            return false;
        }

        // Sub-command: delete.
        if (command.equalsIgnoreCase("deletar")) {
            // No args enough.
            if (args.length < 2) return invalidUsage(player, "deletar <nome>");

            String name = args[1];
            Region region = RXRegions.getRegion(player, name);

            // Check if the player has the region.
            if (region == null) return warnAndReturn(player, "Você não tem nenhuma região com o nome de \"" + name + "\".");

            // Remove the region from region handler.
            RXRegions.removeRegion(region);

            player.sendMessage(ChatColor.GREEN + "Você deletou a região \"" + name + "\".");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
            return false;
        }

        // Sub-command: flags.
        if (command.equalsIgnoreCase("flags")) {
            player.sendMessage("");
            player.sendMessage(" Lista de permissões para conceder a um membro do seu terreno.");
            player.sendMessage(" 1. Quebrar " + ChatColor.GRAY + "(Break)" + ChatColor.WHITE + ".");
            player.sendMessage(" 2. Colocar " + ChatColor.GRAY + "(Place)" + ChatColor.WHITE + ".");
            player.sendMessage(" 3. Usar " + ChatColor.GRAY + "(Use)" + ChatColor.WHITE + ".");
            player.sendMessage("");
            player.sendMessage(" Conceder permissão: " + ChatColor.GRAY + "/rx confiar <nome> [flag]" + ChatColor.WHITE + ".");
            player.sendMessage(" Remover permissão: " + ChatColor.GRAY + "/rx desconfiar <nome> [flag]" + ChatColor.WHITE + ".");
            player.sendMessage("");
            return false;
        }

        // Sub-command: regions.
        if (command.equalsIgnoreCase("regioes")) {
            List<Region> regionList = RXRegions.getPlayerRegions(player);

            // Check if the player has no region.
            if (regionList.isEmpty()) return warnAndReturn(player, "Você não tem nenhuma região para listar.");

            player.sendMessage("");
            int index = 1;
            for (Region region : regionList)
                player.sendMessage(" " + (index++) + ". " + region.getName() + ", X: " + region.getCenterX() + ", Z: " + region.getCenterZ() + ".");
            player.sendMessage("");
            return false;
        }

        // Sub-command: trust.
        if (command.equalsIgnoreCase("confiar")) {
            // No args enough.
            if (args.length < 3) return invalidUsage(player, "confiar <terreno> <nome> [flag]");

            String regionName = args[1];
            String member = args[2];

            Region region = RXRegions.getRegion(player, regionName);

            // Check if the player has the region.
            if (region == null) return warnAndReturn(player, "Você não tem uma região com o nome de \"" + regionName + "\".");

            // If the arguments length is equals 3, then this represents
            // that the owner from region give all permissions to modify
            // the region.
            if (args.length == 3) {
                region.addFlag(member, RegionFlag.BREAK);
                region.addFlag(member, RegionFlag.PLACE);
                region.addFlag(member, RegionFlag.USE);

                player.sendMessage(ChatColor.GREEN + "Você deu todas as permissões para o membro \"" + member + "\" para modificar a região \"" + regionName + "\".");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                return false;
            }

            RegionFlag regionFlag = RegionFlag.getRegionFlagByName(args[3]);

            // Check if the flag has been found.
            if (regionFlag == null) return warnAndReturn(player, "Esta flag não foi encontrada. Use: /rx flags. Para saber todas as flags disponíveis.");

            // Add the flag to player.
            region.addFlag(member, regionFlag);

            player.sendMessage(ChatColor.GREEN + "Você deu a permissão de " + RegionFlag.getTranslatedName(regionFlag) + " para modificar a região \"" + regionName + "\" para o jogador \"" + member + "\".");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
            return false;
        }

        // Sub-command: untrust.
        if (command.equalsIgnoreCase("desconfiar")) {
            // No args enough.
            if (args.length < 3) return invalidUsage(player, "desconfiar <terreno> <nome> [flag]");

            String regionName = args[1];
            String member = args[2];

            Region region = RXRegions.getRegion(player, regionName);

            // Check if the player has the region.
            if (region == null) return warnAndReturn(player, "Você não tem uma região com o nome de \"" + regionName + "\".");

            // If the arguments length is equals 3, then this represents
            // that the owner from region give all permissions to modify
            // the region.
            if (args.length == 3) {
                region.removeFlag(member, RegionFlag.BREAK);
                region.removeFlag(member, RegionFlag.PLACE);
                region.removeFlag(member, RegionFlag.USE);

                player.sendMessage(ChatColor.GREEN + "Você removeu todas as permissões do membro \"" + member + "\" para modificar a região \"" + regionName + "\".");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                return false;
            }

            RegionFlag regionFlag = RegionFlag.getRegionFlagByName(args[3]);

            // Check if the flag has been found.
            if (regionFlag == null) return warnAndReturn(player, "Esta flag não foi encontrada. Use: /rx flags. Para saber todas as flags disponíveis.");

            // Remove the flag from player.
            region.removeFlag(member, regionFlag);

            player.sendMessage(ChatColor.GREEN + "Você removeu a permissão de " + RegionFlag.getTranslatedName(regionFlag) + " para modificar a região \"" + regionName + "\" do jogador \"" + member + "\".");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
            return false;
        }


        return false;
    }

    /**
     * This method format the command to avoid repeat
     * the code colors in the command handler.
     */
    private String formatCommand(String command, String description) {
        return "  /rx " + command + ChatColor.DARK_GRAY +  " - " + ChatColor.GRAY.toString() + description + ".";
    }

    /**
     * Send a message to player with red color,
     * showing a invalid usage from the last
     * tried command sent.
     */
    private boolean invalidUsage(Player player, String usage) {
        player.sendMessage(ChatColor.RED.toString() + "Uso inválido. Tente: /rx " + usage + ".");
        return false;
    }

    /**
     * Warn the usage.
     */
    private boolean warnAndReturn(Player player, String warning) {
        player.sendMessage(ChatColor.RED.toString() + warning);
        return false;
    }

    /**
     * Parse a integer value quietly. If the value can be
     * parsed then the integer value parsed will returned,
     * otherwise if parse fails then return the integer value
     * "-584" to represents to command handler a parse integer
     * value failure.
     */
    private int parseIntQuietly(String string) {
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            return -584;
        }
    }
}
