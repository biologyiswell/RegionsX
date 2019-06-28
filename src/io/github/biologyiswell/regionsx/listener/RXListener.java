package io.github.biologyiswell.regionsx.listener;

import io.github.biologyiswell.regionsx.region.RXRegionManager;
import io.github.biologyiswell.regionsx.region.RXRegion;
import io.github.biologyiswell.regionsx.region.RXRegionFlag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

/**
 * This class listen some events to handle regions.
 *
 * @author biologyiswell (24/05/2019 21:21)
 * @since 1.0
 */
public class RXListener implements Listener {

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        RXRegion region = RXRegionManager.getRegionByLocation(event.getBlock().getLocation());

        // If the region is null, then this represents that
        // no one is owner from this region.
        if (region == null) return;

        // Check if the member from the region has the flag
        // to can break the blocks from region.
        if (region.hasFlag(event.getPlayer(), RXRegionFlag.BREAK)) return;

        event.getPlayer().sendMessage(ChatColor.RED + "Você não pode quebrar blocos da região de " + region.getOwner() + ".");
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        RXRegion region = RXRegionManager.getRegionByLocation(event.getBlock().getLocation());

        if (region == null) return;

        // Check if the member from the region has the flag
        // to can break the blocks from region.
        if (region.hasFlag(event.getPlayer(), RXRegionFlag.PLACE)) return;

        event.getPlayer().sendMessage(ChatColor.RED + "Você não pode colocar blocos na região de " + region.getOwner() + ".");
        event.setCancelled(true);
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        // Check if the clicked block is null, then return it.
        if (event.getClickedBlock() == null) return;

        RXRegion region = RXRegionManager.getRegionByLocation(event.getClickedBlock().getLocation());

        if (region == null) return;

        // Check if the member from region has the flag
        // to can use the blocks from region.
        if (region.hasFlag(event.getPlayer(), RXRegionFlag.USE)) return;

        event.getPlayer().sendMessage(ChatColor.RED + "Você não pode usar os blocos da região de " + region.getOwner() + ".");
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Iterator iterator = event.blockList().iterator(); iterator.hasNext(); ) {
            Block block = (Block) iterator.next();
            RXRegion region = RXRegionManager.getRegionByLocation(block.getLocation());

            // If the block is inner from a region, then remove the block
            // from block list.
            if (region != null) iterator.remove();
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        RXRegion region = RXRegionManager.getRegionByLocation(event.getBlock().getLocation());

        // If the ignited block is inner a region, then cancel the spread. But
        // if the region is null, no region owner there.
        if (region == null) return;

        if (event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
            event.getPlayer().sendMessage(ChatColor.RED + "Você não pode colocar fogo nesta região.");
        }

        event.setCancelled(true);
    }
}
