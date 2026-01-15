package net.flamesmp.plugin.commands;

import net.flamesmp.plugin.FlameSMP;
import net.flamesmp.plugin.data.FlamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WithdrawCommand implements CommandExecutor {
   private final FlameSMP plugin;

   public WithdrawCommand(FlameSMP plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player player)) {
         sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
         return true;
      } else {
         FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(player.getUniqueId());
         if (fp.getLives() <= 1) {
            player.sendMessage(ChatColor.RED + "You need more than 1 life to withdraw!");
            player.sendMessage(ChatColor.GRAY + "Current lives: " + ChatColor.YELLOW + fp.getLives());
            return true;
         } else {
            fp.removeLife();
            this.plugin.getDataManager().savePlayer(player.getUniqueId());
            player.getInventory().addItem(this.plugin.getFlameItems().createLifeItem());
            player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0.0, 2.0, 0.0), 10, 0.5, 0.5, 0.5, 0.0);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            player.sendMessage("");
            player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
            player.sendMessage(ChatColor.RED + "  ♥ " + ChatColor.LIGHT_PURPLE + "Life Withdrawn" + ChatColor.RED + " ♥");
            player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
            player.sendMessage("");
            player.sendMessage(ChatColor.GRAY + "  You crystallized one of your lives!");
            player.sendMessage(ChatColor.YELLOW + "  Remaining lives: " + ChatColor.WHITE + fp.getLives());
            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "  Right-click the item to absorb it back,");
            player.sendMessage(ChatColor.GREEN + "  or give it to another player!");
            player.sendMessage("");
            player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
            return true;
         }
      }
   }
}
