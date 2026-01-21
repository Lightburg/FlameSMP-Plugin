package net.flamesmp.plugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class BrewListener implements Listener {
    @EventHandler
    public void onBrew(BrewEvent e){
        BrewingStand b = (BrewingStand) e.getBlock().getState();
        for (ItemStack i : e.getResults()){
            PotionType p = ((PotionMeta) i.getItemMeta()).getBasePotionType();
            if(p == PotionType.STRONG_STRENGTH || p == PotionType.STRONG_SWIFTNESS || p.toString().toLowerCase().contains("resistance")){
                b.update();
            }
        }
    }
}
