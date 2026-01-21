package net.flamesmp.plugin.listeners;

import org.bukkit.block.BrewingStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class BrewListener implements Listener {
    @EventHandler
    public void onBrew(BrewEvent e) {
        BrewingStand b = (BrewingStand) e.getBlock().getState();
        for (int x = 0; x < 3; x++) {
            ItemStack i = b.getInventory().getItem(x);
            if (i == null) continue;
            if (!(i.getItemMeta() instanceof PotionMeta meta)) continue;

            PotionType p = meta.getBasePotionType();
            if (    p == PotionType.STRONG_STRENGTH  ||
                    p == PotionType.STRONG_SWIFTNESS ||
                    p == PotionType.FIRE_RESISTANCE  ||
                    p == PotionType.LONG_FIRE_RESISTANCE
            ) {
                b.getInventory().setItem(x, null);
            }
        }
        b.update();
    }
}
