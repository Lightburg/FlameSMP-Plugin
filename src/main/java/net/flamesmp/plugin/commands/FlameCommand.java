package net.flamesmp.plugin.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.flamesmp.plugin.FlameSMP;
import net.flamesmp.plugin.abilities.PassiveAbility;
import net.flamesmp.plugin.data.FlamePlayer;
import net.flamesmp.plugin.essences.Essence;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class  FlameCommand implements CommandExecutor, TabCompleter {
   private final FlameSMP plugin;

   public FlameCommand(FlameSMP plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player player)) {
         sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
         return true;
      } else {
         FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(player.getUniqueId());
         if (args.length == 0) {
            this.showHelp(player);
            return true;
         } else {
            String var7 = args[0].toLowerCase();
            switch (var7) {
               case "help":
                  this.showHelp(player);
                  break;
               case "stats":
               case "status":
                  this.showStats(player, fp);
                  break;
               case "passive":
                  this.showPassive(player, fp);
                  break;
               case "trust":
                  if (args.length < 2) {
                     player.sendMessage(ChatColor.RED + "Usage: /flame trust <player>");
                     return true;
                  }

                  this.trustPlayer(player, fp, args[1]);
                  break;
               case "untrust":
                  if (args.length < 2) {
                     player.sendMessage(ChatColor.RED + "Usage: /flame untrust <player>");
                     return true;
                  }

                  this.untrustPlayer(player, fp, args[1]);
                  break;
               case "trusted":
               case "trustlist":
                  this.showTrustedPlayers(player, fp);
                  break;
               case "essences":
                  this.showEssences(player);
                  break;
               case "ability":
                  if (args.length < 2) {
                     player.sendMessage(ChatColor.RED + "Usage: /flame ability <essence> <slot>");
                     return true;
                  }

                  this.useAbility(player, fp, args[1], args.length > 2 ? args[2] : "0");
                  break;
               default:
                  player.sendMessage(ChatColor.RED + "Unknown command. Use /flame help for a list of commands.");
            }

            return true;
         }
      }
   }

   private void showHelp(Player player) {
      player.sendMessage("");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
      player.sendMessage(ChatColor.RED + "  \ud83d\udd25 " + ChatColor.GOLD + "FlameSMP Commands" + ChatColor.RED + " \ud83d\udd25");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
      player.sendMessage("");
      player.sendMessage(ChatColor.YELLOW + "/flame stats " + ChatColor.GRAY + "- View your stats");
      player.sendMessage(ChatColor.YELLOW + "/flame passive " + ChatColor.GRAY + "- View your passive ability");
      player.sendMessage(ChatColor.YELLOW + "/flame ability <essence> <slot> " + ChatColor.GRAY + "- Use an ability");
      player.sendMessage(ChatColor.YELLOW + "/flame trust <player> " + ChatColor.GRAY + "- Trust a player");
      player.sendMessage(ChatColor.YELLOW + "/flame untrust <player> " + ChatColor.GRAY + "- Untrust a player");
      player.sendMessage(ChatColor.YELLOW + "/flame trusted " + ChatColor.GRAY + "- View trusted players");
      player.sendMessage(ChatColor.YELLOW + "/flame essences " + ChatColor.GRAY + "- View essence info");
      player.sendMessage(ChatColor.YELLOW + "/withdraw " + ChatColor.GRAY + "- Withdraw a life as item");
      player.sendMessage("");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
   }

   private void showStats(Player player, FlamePlayer fp) {
      player.sendMessage("");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
      player.sendMessage(ChatColor.RED + "  \ud83d\udd25 " + ChatColor.GOLD + "Your Stats" + ChatColor.RED + " \ud83d\udd25");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
      player.sendMessage("");
      player.sendMessage(ChatColor.YELLOW + "  Lives: " + ChatColor.WHITE + fp.getLives() + ChatColor.GRAY + "/10");
      player.sendMessage(ChatColor.YELLOW + "  Trusted Players: " + ChatColor.WHITE + fp.getTrustedPlayers().size());
      if (fp.getPassiveAbility() != null) {
         player.sendMessage(ChatColor.YELLOW + "  Passive: " + fp.getPassiveAbility().getFormattedName());
      }

      player.sendMessage("");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
   }

   private void showPassive(Player player, FlamePlayer fp) {
      player.sendMessage("");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
      player.sendMessage(ChatColor.RED + "  ★ " + ChatColor.GOLD + "Your Passive Ability" + ChatColor.RED + " ★");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
      player.sendMessage("");
      PassiveAbility passive = fp.getPassiveAbility();
      if (passive != null) {
         player.sendMessage("  " + passive.getFormattedName());
         player.sendMessage(ChatColor.GRAY + "  " + passive.getDescription());
      } else {
         player.sendMessage(ChatColor.RED + "  No passive ability assigned!");
      }

      player.sendMessage("");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
   }

   private void trustPlayer(Player player, FlamePlayer fp, String targetName) {
      Player target = Bukkit.getPlayer(targetName);
      if (target == null) {
         player.sendMessage(ChatColor.RED + "Player not found!");
      } else if (target.equals(player)) {
         player.sendMessage(ChatColor.RED + "You can't trust yourself!");
      } else if (fp.isTrusted(target.getUniqueId())) {
         player.sendMessage(ChatColor.RED + "You already trust this player!");
      } else {
         int maxTrusted = this.plugin.getConfig().getInt("trust.max-trusted-players", 4);
         if (fp.getTrustedPlayers().size() >= maxTrusted) {
            player.sendMessage(ChatColor.RED + "You can only trust a maximum of " + maxTrusted + " players!");
         } else {
            fp.addTrustedPlayer(target.getUniqueId());
            this.plugin.getDataManager().savePlayer(player.getUniqueId());
            String message = this.plugin.getConfig().getString("messages.trust-added", "&aYou now trust &e{player}&a!");
            message = message.replace("{player}", target.getName());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            target.sendMessage(ChatColor.GREEN + player.getName() + " now trusts you!");
         }
      }
   }

   private void untrustPlayer(Player player, FlamePlayer fp, String targetName) {
      Player target = Bukkit.getPlayer(targetName);
      if (target == null) {
         player.sendMessage(ChatColor.RED + "Player not found!");
      } else if (!fp.isTrusted(target.getUniqueId())) {
         player.sendMessage(ChatColor.RED + "You don't trust this player!");
      } else {
         fp.removeTrustedPlayer(target.getUniqueId());
         this.plugin.getDataManager().savePlayer(player.getUniqueId());
         String message = this.plugin.getConfig().getString("messages.trust-removed", "&cYou no longer trust &e{player}&c!");
         message = message.replace("{player}", target.getName());
         player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
      }
   }

   private void showTrustedPlayers(Player player, FlamePlayer fp) {
      player.sendMessage("");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
      player.sendMessage(ChatColor.GREEN + "  ✓ " + ChatColor.GOLD + "Trusted Players" + ChatColor.GREEN + " ✓");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
      player.sendMessage("");
      if (fp.getTrustedPlayers().isEmpty()) {
         player.sendMessage(ChatColor.GRAY + "  You haven't trusted anyone yet.");
      } else {
         for (UUID uuid : fp.getTrustedPlayers()) {
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            if (name != null) {
               player.sendMessage(ChatColor.YELLOW + "  - " + ChatColor.WHITE + name);
            }
         }
      }

      player.sendMessage("");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
   }

   private void showEssences(Player player) {
      player.sendMessage("");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
      player.sendMessage(ChatColor.RED + "  \ud83d\udd25 " + ChatColor.GOLD + "Essences" + ChatColor.RED + " \ud83d\udd25");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
      player.sendMessage("");
      player.sendMessage(ChatColor.RED + "  ⚠ Only 2 of each essence can exist!");
      player.sendMessage("");

      for (Essence essence : Essence.values()) {
         int count = this.plugin.getEssenceCount(essence.name());
         player.sendMessage(essence.getFormattedName() + ChatColor.GRAY + " (" + count + "/2 crafted)");

         for (int i = 0; i < essence.getAbilityNames().length; i++) {
            player.sendMessage(ChatColor.GRAY + "  " + (i + 1) + ". " + ChatColor.WHITE + essence.getAbilityNames()[i]);
         }
      }

      player.sendMessage("");
      player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
   }

   private void useAbility(Player player, FlamePlayer fp, String essenceName, String slotStr) {
      try {
         Essence essence = Essence.valueOf(essenceName.toUpperCase());
         int slot = Integer.parseInt(slotStr);
         if (slot < 0 || slot > 1) {
            player.sendMessage(ChatColor.RED + "Slot must be 0 or 1!");
            return;
         }

         boolean hasEssence = false;

         for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && this.plugin.getEssenceManager().getEssenceFromItem(item) == essence) {
               hasEssence = true;
               break;
            }
         }

         if (!hasEssence) {
            player.sendMessage(ChatColor.RED + "You don't have this essence in your inventory!");
            return;
         }

         String cooldownKey = essence.name() + "_" + slot;
         if (fp.isOnCooldown(cooldownKey)) {
            long seconds = fp.getCooldownRemaining(cooldownKey);
            String cooldownMsg = this.plugin.getConfig().getString("messages.ability-cooldown", "&cThis ability is on cooldown! &7({time}s remaining)");
            cooldownMsg = cooldownMsg.replace("{time}", String.valueOf(seconds));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', cooldownMsg));
            return;
         }

         this.plugin.getEssenceAbilities().executeAbility(player, fp, essence, slot);
      } catch (IllegalArgumentException var12) {
         player.sendMessage(ChatColor.RED + "Invalid essence or slot format!");
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> completions = new ArrayList<>();
      if (args.length == 1) {
         for (String sub : Arrays.asList("help", "stats", "passive", "trust", "untrust", "trusted", "essences")) {
            if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
               completions.add(sub);
            }
         }
      } else if (args.length == 2 && (args[0].equalsIgnoreCase("trust") || args[0].equalsIgnoreCase("untrust"))) {
         for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
               completions.add(p.getName());
            }
         }
      }

      return completions;
   }
}
