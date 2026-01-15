package net.flamesmp.plugin.listeners;

import java.util.ArrayList;
import java.util.List;
import net.flamesmp.plugin.FlameSMP;
import net.flamesmp.plugin.abilities.PassiveAbility;
import net.flamesmp.plugin.data.FlamePlayer;
import org.bukkit.*;
import org.bukkit.BanList.Type;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ItemListener implements Listener {
   private final FlameSMP plugin;
   private int solarHitCounter;

   public ItemListener(FlameSMP plugin) {
      this.plugin = plugin;
      solarHitCounter = 0;
   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         Player player = event.getPlayer();
         ItemStack item = event.getItem();
         if (item != null) {
            if (this.plugin.getFlameItems().isLifeItem(item)) {
               event.setCancelled(true);
               this.useLifeItem(player, item);
            } else if (this.plugin.getFlameItems().isReviveItem(item)) {
               event.setCancelled(true);
               this.openReviveMenu(player, item);
            } else if (this.plugin.getFlameItems().isPassiveRerollerItem(item)){
                event.setCancelled(true);
                if(item.getAmount() == 1) player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                else {
                    player.getInventory().getItemInMainHand().setAmount(item.getAmount()-1);
                }
                this.usePassiveRerollerItem(player);
            }
         }
      }
   }

   @EventHandler
   public void onPlayerHitPlayer(EntityDamageByEntityEvent e){
       if (!(e.getDamager() instanceof Player player)) return;
       if (!(e.getEntity() instanceof Player target)) return;
       if (!this.plugin.getFlameItems().isSolarSwordItem(player.getInventory().getItemInMainHand())) return;
       solarHitCounter++;
       if(solarHitCounter >= 30){
           e.setDamage(0);
           target.setHealth(Math.max(0, target.getHealth()-8));
           target.getWorld().strikeLightningEffect(target.getLocation());
           solarHitCounter = 0;
       }
   }

   private void usePassiveRerollerItem(Player player){
       FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(player.getUniqueId());
       fp.assignRandomPassive();
       PassiveAbility passive = fp.getPassiveAbility();
       player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation().add(0.0, 1.0, 0.0), 50, 1.0, 1.0, 1.0, 0.3);
       player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.8F);
       player.sendMessage("");
       player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
       player.sendMessage("");
       player.sendMessage(ChatColor.YELLOW + "  Your flame has been ignited!");
       player.sendMessage("");
       player.sendMessage(ChatColor.GOLD + "  ★ PASSIVE ABILITY GRANTED ★");
       player.sendMessage("  " + passive.getFormattedName());
       player.sendMessage(ChatColor.GRAY + "  " + passive.getDescription());
       player.sendMessage("");
       player.sendMessage(ChatColor.YELLOW + "  Use " + ChatColor.GREEN + "/flame help " + ChatColor.YELLOW + "for commands!");
       player.sendMessage("");
       player.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
       player.sendMessage("");
   }

   private void useLifeItem(Player player, ItemStack item) {
      FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(player.getUniqueId());
      if (fp.getLives() >= 10) {
         player.sendMessage(ChatColor.RED + "You already have maximum lives (10)!");
      } else {
         fp.addLife();
         this.plugin.getDataManager().savePlayer(player.getUniqueId());
         if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
         } else {
            player.getInventory().setItemInMainHand(null);
         }

         player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0.0, 2.0, 0.0), 20, 0.5, 0.5, 0.5, 0.0);
         player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation().add(0.0, 1.0, 0.0), 30, 1.0, 1.0, 1.0, 0.2);
         player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.5F);
         player.sendMessage("");
         player.sendMessage(ChatColor.GREEN + "  ✦ " + ChatColor.WHITE + "Life Absorbed!" + ChatColor.GREEN + " ✦");
         player.sendMessage(ChatColor.YELLOW + "  You now have " + ChatColor.WHITE + fp.getLives() + ChatColor.YELLOW + " lives!");
         player.sendMessage("");
      }
   }

   private void openReviveMenu(Player player, ItemStack item) {
      List<String> bannedPlayers = this.getBannedPlayersByFlameSMP();
      if (bannedPlayers.isEmpty()) {
         player.sendMessage(ChatColor.RED + "No players are currently banned by the lives system!");
      } else {
         int size = Math.min(54, (bannedPlayers.size() / 9 + 1) * 9);
         if (size < 9) {
            size = 9;
         }

         Inventory gui = Bukkit.createInventory(null, size, ChatColor.GOLD + "✦ Revive a Fallen Player ✦");

         for (int i = 0; i < Math.min(bannedPlayers.size(), 54); i++) {
             ItemStack skull = getItemStack(bannedPlayers, i);
             gui.setItem(i, skull);
         }

         player.openInventory(gui);
         Bukkit.getPluginManager().registerEvents(new ReviveMenuListener(this.plugin, player, item), this.plugin);
      }
   }

    private static @NotNull ItemStack getItemStack(List<String> bannedPlayers, int i) {
        String playerName = bannedPlayers.get(i);
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = skull.getItemMeta();
        meta.setDisplayName(ChatColor.RED + playerName);
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Click to revive this player");
        lore.add(ChatColor.GRAY + "with " + ChatColor.RED + "3 Lives");
        lore.add("");
        lore.add(ChatColor.YELLOW + "This will consume the Phoenix Feather");
        meta.setLore(lore);
        skull.setItemMeta(meta);
        return skull;
    }

    private List<String> getBannedPlayersByFlameSMP() {
      List<String> banned = new ArrayList<>();
      for (BanEntry entry : Bukkit.getBanList(Type.NAME).getEntries()) {
            banned.add(entry.getTarget());
      }
      return banned;
   }

   @EventHandler
   public void onPrepareCraft(PrepareItemCraftEvent event) {
      if (event.getRecipe() != null) {
         ItemStack result = event.getRecipe().getResult();
         if (result != null) {
            String essenceType = this.plugin.getEssenceManager().getEssenceTypeFromItem(result);
            if (essenceType != null && this.plugin.canCraftEssence(essenceType)) {
               event.getInventory().setResult(null);
               if (event.getView().getPlayer() instanceof Player) {
                  Player player = (Player)event.getView().getPlayer();
                  player.sendMessage(ChatColor.RED + "Only 2 of each essence type can exist in the world!");
               }
            }
         }
      }
   }
}
