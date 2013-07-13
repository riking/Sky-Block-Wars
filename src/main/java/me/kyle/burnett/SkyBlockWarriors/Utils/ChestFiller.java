package me.kyle.burnett.SkyBlockWarriors.Utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import me.kyle.burnett.SkyBlockWarriors.Main;
import me.kyle.burnett.SkyBlockWarriors.Configs.ConfigFormats;

public class ChestFiller {
    public static void loadChests(int arena) {
        Main plugin = Main.getInstance();
        World w = Bukkit.getWorld(Main.getInstance().Arena.getString("Arena." + arena + ".World"));

        fillChests(w, plugin.Chest.getStringList("Chest." + arena + ".Spawn"), plugin.Config.getStringList("Chests.Spawn-Chests.ItemID/Amount"));
        fillChests(w, plugin.Chest.getStringList("Chest." + arena + ".Side"), plugin.Config.getStringList("Chests.Side-Chests.ItemID/Amount"));
        fillChests(w, plugin.Chest.getStringList("Chest." + arena + ".Center"), plugin.Config.getStringList("Chests.Middle-Chests.ItemID/Amount"));
    }

    private static void fillChests(World world, List<String> chestLocations, List<String> chestContents) {
        ItemStack[] items = new ItemStack[27];

        int i = 0;
        for (String item : chestContents) {
            items[i++] = ConfigFormats.itemFromString(item);
        }

        for (String locString : chestLocations) {
            Block b = world.getBlockAt(ConfigFormats.vecFromString(locString).toLocation(world));

            if (b.getType().equals(Material.CHEST)) {
                Chest c = (Chest) b.getState();
                c.getInventory().setContents(items);
            } else {
                Main.getInstance().getLogger().warning("Failed to find chest at " + locString + ", skipping...");
            }
        }
    }
}
