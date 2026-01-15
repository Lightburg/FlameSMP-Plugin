package net.flamesmp.plugin.listeners;

import java.io.File;
import net.flamesmp.plugin.FlameSMP;
import net.flamesmp.plugin.data.FlamePlayer;
import org.bukkit.*;
import org.bukkit.BanList.Type;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

class ReviveMenuListener implements Listener {
   private final FlameSMP plugin;
   private final Player opener;
   private final ItemStack reviveItem;
   private boolean used = false;

   public ReviveMenuListener(FlameSMP plugin, Player opener, ItemStack reviveItem) {
      this.plugin = plugin;
      this.opener = opener;
      this.reviveItem = reviveItem;
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      if (event.getWhoClicked() instanceof Player player) {
          if (player.equals(this.opener)) {
            if (event.getView().getTitle().contains("Revive a Fallen Player")) {
               event.setCancelled(true);
               if (!this.used) {
                  ItemStack clicked = event.getCurrentItem();
                  String targetName = "";
                  if (clicked != null && clicked.getType() == Material.PLAYER_HEAD) {
                        String playerName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                        targetName = playerName;
                        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
                        Bukkit.getBanList(Type.NAME).pardon(playerName);
                        FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(target.getUniqueId());
                        fp.setLives(this.plugin.getConfig().getInt("lives.starting-lives", 5));
                        this.plugin.getDataManager().savePlayer(target.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "Your fallen comrade has been revived, they will start with 5 lives.");
                     }

                     if (this.reviveItem.getAmount() > 1) {
                        this.reviveItem.setAmount(this.reviveItem.getAmount() - 1);
                     } else {
                        this.opener.getInventory().remove(this.reviveItem);
                     }

                     this.used = true;
                     player.closeInventory();
                     player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation().add(0.0, 1.0, 0.0), 100, 2.0, 2.0, 2.0, 0.3);
                     player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
                     Bukkit.broadcastMessage("");
                     Bukkit.broadcastMessage(ChatColor.GOLD + "═══════════════════════════════════");
                     Bukkit.broadcastMessage(ChatColor.GREEN + "  ✦ " + ChatColor.WHITE + targetName + ChatColor.GREEN + " HAS BEEN REVIVED! ✦");
                     Bukkit.broadcastMessage(ChatColor.GRAY + "  " + player.getName() + " used a Phoenix Feather");
                     Bukkit.broadcastMessage(ChatColor.YELLOW + "  They return with " + ChatColor.RED + "5 Lives");
                     Bukkit.broadcastMessage(ChatColor.GOLD + "═══════════════════════════════════");
                     Bukkit.broadcastMessage("");
                     InventoryCloseEvent.getHandlerList().unregister(this);
                     InventoryClickEvent.getHandlerList().unregister(this);
                  }
               }
            }
         }
      }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent event) {
      if (event.getPlayer().equals(this.opener)) {
         InventoryCloseEvent.getHandlerList().unregister(this);
         InventoryClickEvent.getHandlerList().unregister(this);
      }
   }
}
