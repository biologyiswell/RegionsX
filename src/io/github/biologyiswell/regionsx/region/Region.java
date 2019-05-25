package io.github.biologyiswell.regionsx.region;

import com.google.common.base.Preconditions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class mark a region in the world,
 * and set the owner and the name from region.
 *
 * @author biologyiswell (24/05/2019 20:02)
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public class Region {

    private String owner;
    private String name;

    private int minX;
    private int minZ;

    private int maxX;
    private int maxZ;

    private String world;
    private Map<String, List<RegionFlag>> members;

    /**
     * Constructor, requires the player that will be owner from region,
     * the name from region, and the minimum and maximum position that
     * will mark the region.
     */
    public Region(Player player, String name, Location min, Location max) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(min);
        Preconditions.checkNotNull(max);
        Preconditions.checkState(min.getWorld() == max.getWorld(),
                "world at minimum position must be equal from world at maximum position.");

        this.owner = player.getName();
        this.name = name;

        this.minX = Math.min(min.getBlockX(), max.getBlockX());
        this.minZ = Math.min(min.getBlockZ(), max.getBlockZ());

        this.maxX = Math.max(min.getBlockX(), max.getBlockX());
        this.maxZ = Math.max(min.getBlockZ(), max.getBlockZ());

        this.world = min.getWorld().getName();
        this.members = new HashMap<>();
    }

    // ... Util ...

    /**
     * Check if the member from the region has the flag.
     */
    public boolean hasFlag(String name, RegionFlag flag) {
        return this.owner.equals(name) || this.members.containsKey(name) && this.members.get(name).contains(flag);
    }

    /**
     * Check if the member from the region has the flag by player.
     * @see #hasFlag(String, RegionFlag)
     */
    public boolean hasFlag(Player player, RegionFlag flag) {
        return this.hasFlag(player.getName(), flag);
    }

    /**
     * Add a flag to a member.
     */
    public void addFlag(String member, RegionFlag flag) {
        List<RegionFlag> flagList = this.members.computeIfAbsent(member, k -> new ArrayList<>());

        if (!flagList.contains(flag)) {
            flagList.add(flag);
        }
    }

    /**
     * Remove a flag from a member.
     */
    public void removeFlag(String member, RegionFlag flag) {
        List<RegionFlag> flagList = this.members.get(member);

        if (flagList == null) return;

        flagList.remove(flag);
    }

    /**
     * Get center X position.
     */
    public int getCenterX() {
        return (this.minX + this.maxX) / 2;
    }

    /**
     * Get center Z position.
     */
    public int getCenterZ() {
        return (this.minZ + this.maxZ) / 2;
    }
}
