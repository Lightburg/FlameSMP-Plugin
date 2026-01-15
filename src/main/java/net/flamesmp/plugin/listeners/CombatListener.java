package net.flamesmp.plugin.listeners;

import net.flamesmp.plugin.FlameSMP;
import net.flamesmp.plugin.data.FlamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatListener implements Listener {
   private final FlameSMP plugin;

   public CombatListener(FlameSMP plugin) {
      this.plugin = plugin;
   }

   @EventHandler(priority = EventPriority.LOW)
   public void onPlayerDamagePlayer(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player) {
         if (event.getDamager() instanceof Player) {
            Player victim = (Player)event.getEntity();
            Player attacker = (Player)event.getDamager();
            FlamePlayer attackerFp = this.plugin.getDataManager().getFlamePlayer(attacker.getUniqueId());
            if (attackerFp.isTrusted(victim.getUniqueId())) {
               event.setCancelled(true);
               attacker.sendMessage(ChatColor.GRAY + "You can't attack trusted players!");
            }
         }
      }
   }
}
