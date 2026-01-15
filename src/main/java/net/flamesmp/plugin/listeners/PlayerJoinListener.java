package net.flamesmp.plugin.listeners;

import net.flamesmp.plugin.FlameSMP;
import net.flamesmp.plugin.abilities.PassiveAbility;
import net.flamesmp.plugin.data.FlamePlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinListener implements Listener {
   private final FlameSMP plugin;

   public PlayerJoinListener(FlameSMP plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      final Player player = event.getPlayer();
      final FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(player.getUniqueId());
      if (fp.getLives() <= 0 && !player.hasPermission("flamesmp.bypass.ban")) {
         String banMessage = this.plugin.getConfig().getString("lives.ban-message", "&cYou have been consumed by the flames. Your fire has extinguished.");
         player.kickPlayer(ChatColor.translateAlternateColorCodes('&', banMessage));
      } else {
         boolean isNewPlayer = !fp.hasPassiveAbility();
         if (isNewPlayer) {
            fp.assignRandomPassive();
            this.plugin.getDataManager().savePlayer(player.getUniqueId());
            (new BukkitRunnable() {
               public void run() {
                  PlayerJoinListener.this.showNewPlayerAnimation(player, fp);
               }
            }).runTaskLater(this.plugin, 40L);
         } else {
            this.showWelcomeMessage(player, fp);
         }
      }
   }

   private void showNewPlayerAnimation(final Player player, final FlamePlayer fp) {
      player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
      final PassiveAbility[] allPassives = PassiveAbility.values();
      final PassiveAbility actualPassive = fp.getPassiveAbility();
      (new BukkitRunnable() {
            int ticks = 0;
            int displayIndex = 0;
            double angle = 0.0;

            public void run() {
               if (this.ticks >= 80) {
                  this.cancel();
                  PlayerJoinListener.this.showPassiveReveal(player, fp);
               } else {
                  for (int i = 0; i < 4; i++) {
                     double particleAngle = this.angle + i * Math.PI / 2.0;
                     double x = Math.cos(particleAngle) * 2.0;
                     double z = Math.sin(particleAngle) * 2.0;
                     player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(x, 1.0 + Math.sin(this.ticks * 0.1), z), 5, 0.1, 0.1, 0.1, 0.02);
                  }

                  int cycleSpeed = Math.max(3, 15 - this.ticks / 6);
                  if (this.ticks % cycleSpeed == 0) {
                     PassiveAbility displayPassive;
                     if (this.ticks >= 60) {
                        displayPassive = actualPassive;
                     } else {
                        displayPassive = allPassives[this.displayIndex % allPassives.length];
                        this.displayIndex++;
                     }

                     Component title = Component.text("").append(Component.text("⚡ ", NamedTextColor.GOLD))
                           .append(Component.text(displayPassive.getDisplayName(), this.ticks >= 60 ? NamedTextColor.GOLD : NamedTextColor.GRAY))
                        .append(Component.text(" ⚡", NamedTextColor.GOLD));
                     ((Audience) player).sendActionBar(title);
                     if (this.ticks < 60) {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F + this.ticks * 0.01F);
                     } else {
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.2F);
                     }
                  }

                  this.angle += 0.15;
                  this.ticks++;
               }
            }
         })
         .runTaskTimer(this.plugin, 0L, 1L);
   }

   private void showPassiveReveal(Player player, FlamePlayer fp) {
      PassiveAbility passive = fp.getPassiveAbility();
      player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation().add(0.0, 1.0, 0.0), 50, 1.0, 1.0, 1.0, 0.3);
      player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.8F);
      player.sendMessage("");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
      player.sendMessage(ChatColor.RED + "  \ud83d\udd25 " + ChatColor.GOLD + "Welcome to " + ChatColor.RED + "FlameSMP" + ChatColor.GOLD + " \ud83d\udd25");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
      player.sendMessage("");
      player.sendMessage(ChatColor.YELLOW + "  Your flame has been ignited!");
      player.sendMessage("");
      player.sendMessage(ChatColor.GOLD + "  ★ PASSIVE ABILITY GRANTED ★");
      player.sendMessage("  " + passive.getFormattedName());
      player.sendMessage(ChatColor.GRAY + "  " + passive.getDescription());
      player.sendMessage("");
      player.sendMessage(ChatColor.YELLOW + "  Lives: " + ChatColor.WHITE + fp.getLives() + ChatColor.GRAY + "/10");
      player.sendMessage(ChatColor.YELLOW + "  Use " + ChatColor.GREEN + "/flame help " + ChatColor.YELLOW + "for commands!");
      player.sendMessage("");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
      player.sendMessage("");
   }

   private void showWelcomeMessage(Player player, FlamePlayer fp) {
      player.sendMessage("");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
      player.sendMessage(
         ChatColor.RED + "  \ud83d\udd25 " + ChatColor.GOLD + "Welcome Back to " + ChatColor.RED + "FlameSMP" + ChatColor.GOLD + " \ud83d\udd25"
      );
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
      player.sendMessage("");
      player.sendMessage(ChatColor.YELLOW + "  Lives: " + ChatColor.WHITE + fp.getLives() + ChatColor.GRAY + "/10");
      if (fp.getPassiveAbility() != null) {
         player.sendMessage(ChatColor.YELLOW + "  Passive: " + fp.getPassiveAbility().getFormattedName());
      }

      player.sendMessage("");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
      player.sendMessage("");
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
      this.plugin.getDataManager().savePlayer(event.getPlayer().getUniqueId());
   }
}
