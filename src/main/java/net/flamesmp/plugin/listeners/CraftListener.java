package net.flamesmp.plugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class CraftListener implements Listener {
    @EventHandler
    public void onCraft(BrewEvent e){
        for (ItemStack i : e.getResults()){
            PotionMeta p = (PotionMeta) i.getItemMeta();
            assert p != null;
            if(p.getBasePotionType() == PotionType.STRONG_STRENGTH || p.getBasePotionType() == PotionType.STRONG_SWIFTNESS || p.getBasePotionType() == PotionType.FIRE_RESISTANCE || p.getBasePotionType() == PotionType.LONG_FIRE_RESISTANCE){
                e.setCancelled(true);
            }
        }
    }
}
