package net.flamesmp.plugin.listeners;

import net.flamesmp.plugin.FlameSMP;
import net.flamesmp.plugin.data.FlamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDeathListener implements Listener {
   private final FlameSMP plugin;

   public PlayerDeathListener(FlameSMP plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      final Player player = event.getEntity();
      if (!(player.getKiller() instanceof Player killer)) return;
      FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(player.getUniqueId());
      fp.removeLife();
      fp.resetHitCounter();
      this.plugin.getDataManager().savePlayer(player.getUniqueId());
      if (killer != player) {
         this.dropHeatShard(player, killer);
      }
      String message = this.plugin.getConfig().getString("messages.life-lost", "&c-1 Life! &7You now have &e{lives} &7lives remaining.");
      message = message.replace("{lives}", String.valueOf(fp.getLives()));
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
      if (fp.getLives() <= 0) {
         this.handleFinalDeath(player);
         (new BukkitRunnable() {
            public void run() {
               if (player.isOnline()) {
                  PlayerDeathListener.this.banPlayer(player);
               }
            }
         }).runTaskLater(this.plugin, 40L);
      }
   }

   private void dropHeatShard(Player victim, Player killer) {
      victim.getWorld().dropItemNaturally(victim.getLocation(), this.plugin.getFlameItems().createHeatShard());
      victim.getWorld().spawnParticle(Particle.FLAME, victim.getLocation().add(0.0, 1.0, 0.0), 50, 0.5, 0.5, 0.5, 0.1);
      victim.getWorld().spawnParticle(Particle.LAVA, victim.getLocation().add(0.0, 1.0, 0.0), 20, 0.3, 0.3, 0.3, 0.0);
      killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 0.5F);
      killer.sendMessage(
         ChatColor.GOLD + "✦ " + ChatColor.YELLOW + "You obtained a " + ChatColor.RED + "Heat Shard" + ChatColor.YELLOW + " from " + victim.getName() + "!"
      );
   }

   @EventHandler
   public void onPlayerRespawn(PlayerRespawnEvent event) {
      final Player player = event.getPlayer();
      FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(player.getUniqueId());
      if (fp.getLives() <= 0) {
         (new BukkitRunnable() {
            public void run() {
               if (player.isOnline()) {
                  PlayerDeathListener.this.banPlayer(player);
               }
            }
         }).runTaskLater(this.plugin, 10L);
      }
   }

   private void handleFinalDeath(Player player) {
      Bukkit.broadcastMessage("");
      Bukkit.broadcastMessage(ChatColor.DARK_RED + "═══════════════════════════════════");
      Bukkit.broadcastMessage(ChatColor.RED + "  ☠ " + ChatColor.GOLD + player.getName() + ChatColor.RED + " HAS BEEN ELIMINATED! ☠");
      Bukkit.broadcastMessage(ChatColor.GRAY + "  Their flame has been extinguished forever...");
      Bukkit.broadcastMessage(ChatColor.DARK_RED + "═══════════════════════════════════");
      Bukkit.broadcastMessage("");

      for (Player p : Bukkit.getOnlinePlayers()) {
         p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0F, 0.5F);
         p.getWorld().spawnParticle(Particle.SOUL, player.getLocation().add(0.0, 1.0, 0.0), 100, 2.0, 2.0, 2.0, 0.05);
      }
   }

   private void banPlayer(Player player) {
      String banMessage = this.plugin.getConfig().getString("lives.ban-message", "&cYou have been consumed by the flames. Your fire has extinguished.");
      banMessage = ChatColor.translateAlternateColorCodes('&', banMessage);
      player.kickPlayer(banMessage);
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + player.getName() + " " + banMessage);
   }
}
