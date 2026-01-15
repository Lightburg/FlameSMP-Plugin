package net.flamesmp.plugin.listeners;

import net.flamesmp.plugin.FlameSMP;
import net.flamesmp.plugin.data.FlamePlayer;
import net.flamesmp.plugin.essences.Essence;
import net.flamesmp.plugin.essences.EssenceAbilities;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EssenceListener implements Listener {
   private final FlameSMP plugin;
   private final EssenceAbilities abilities;

   public EssenceListener(FlameSMP plugin) {
      this.plugin = plugin;
      this.abilities = new EssenceAbilities(plugin);
   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         Player player = event.getPlayer();
         ItemStack item = event.getItem();
         if (item != null) {
            Essence essence = this.plugin.getEssenceManager().getEssenceFromItem(item);
            if (essence != null) {
               event.setCancelled(true);
               FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(player.getUniqueId());
               if (player.isSneaking()) {
                  fp.toggleEssenceAbility(essence);
                  int slot = fp.getEssenceAbilitySlot(essence);
                  String abilityName = essence.getAbilityNames()[slot];
                  player.sendMessage(essence.getColor() + "Switched to ability: " + ChatColor.WHITE + abilityName);
               } else {
                  boolean hasEssence = false;

                  for (ItemStack invItem : player.getInventory().getContents()) {
                     if (invItem != null && this.plugin.getEssenceManager().getEssenceFromItem(invItem) == essence) {
                        hasEssence = true;
                        break;
                     }
                  }

                  if (!hasEssence) {
                     player.sendMessage(ChatColor.RED + "You don't have this essence in your inventory!");
                  } else {
                     int abilitySlot = fp.getEssenceAbilitySlot(essence);
                     String cooldownKey = essence.name() + "_" + abilitySlot;
                     if (fp.isOnCooldown(cooldownKey)) {
                        long remaining = fp.getCooldownRemaining(cooldownKey);
                        String message = this.plugin.getConfig().getString("messages.ability-cooldown", "&cThis ability is on cooldown! &7({time}s remaining)");
                        message = message.replace("{time}", String.valueOf(remaining));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                     } else {
                        this.abilities.executeAbility(player, fp, essence, abilitySlot);
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onCraftEssence(CraftItemEvent event) {
      if (event.getWhoClicked() instanceof Player player) {
          ItemStack result = event.getRecipe().getResult();
         String essenceType = this.plugin.getEssenceManager().getEssenceTypeFromItem(result);
         if (essenceType != null) {
            if (this.plugin.canCraftEssence(essenceType)) {
               event.setCancelled(true);
               player.sendMessage(ChatColor.RED + "Only 2 of each essence type can exist in the world!");
               player.sendMessage(ChatColor.GRAY + "The limit for " + essenceType + " has been reached.");
               return;
            }

            this.plugin.incrementEssenceCount(essenceType);
            player.sendMessage(ChatColor.GOLD + "✦ " + ChatColor.YELLOW + "You crafted a " + essenceType + " Essence!");
            player.sendMessage(ChatColor.GRAY + "(" + this.plugin.getEssenceCount(essenceType) + "/2 of this type now exist)");
         }
      }
   }
}
