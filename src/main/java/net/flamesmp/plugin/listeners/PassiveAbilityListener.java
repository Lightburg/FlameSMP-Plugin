package net.flamesmp.plugin.listeners;

import net.flamesmp.plugin.FlameSMP;
import net.flamesmp.plugin.abilities.PassiveAbility;
import net.flamesmp.plugin.data.FlamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PassiveAbilityListener implements Listener {
   private final FlameSMP plugin;

   public PassiveAbilityListener(FlameSMP plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onPlayerDealDamage(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player) {
         Player attacker = (Player)event.getDamager();
         FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(attacker.getUniqueId());
         if (event.getEntity() instanceof Player) {
            Player target = (Player) event.getEntity();
            if (fp.isTrusted(target.getUniqueId())) {
               return;
            }
         }

         PassiveAbility passive = fp.getPassiveAbility();
         if (passive == PassiveAbility.HEAT_PRESSURE) {
            fp.incrementHitCounter();
            if (fp.isHeatMaxed()) {
               double bonusDamage = this.plugin.getConfig().getDouble("passives.heat-pressure.bonus-damage", 4.0);
               event.setDamage(event.getDamage() + bonusDamage);
               fp.resetHitCounter();
               attacker.sendMessage(ChatColor.RED + "\ud83d\udd25 " + ChatColor.GOLD + "HEAT PRESSURE! " + ChatColor.YELLOW + "Bonus damage dealt!");
               attacker.playSound(attacker.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0F, 1.5F);
               attacker.getWorld().spawnParticle(Particle.FLAME, attacker.getLocation().add(0.0, 1.0, 0.0), 40, 0.5, 0.5, 0.5, 0.15);
               attacker.getWorld().spawnParticle(Particle.LAVA, attacker.getLocation().add(0.0, 1.0, 0.0), 15, 0.3, 0.3, 0.3, 0.0);
               attacker.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, attacker.getLocation().add(0.0, 1.0, 0.0), 20, 0.4, 0.4, 0.4, 0.1);
               if (event.getEntity() instanceof LivingEntity) {
                  LivingEntity target = (LivingEntity)event.getEntity();
                  target.getWorld().spawnParticle(Particle.FLAME, target.getLocation().add(0.0, 1.0, 0.0), 50, 0.5, 1.0, 0.5, 0.15);
                  target.getWorld().spawnParticle(Particle.EXPLOSION, target.getLocation().add(0.0, 1.0, 0.0), 3, 0.3, 0.3, 0.3, 0.0);
               }
            }
         }
      }
   }

   @EventHandler
   public void onPlayerTakeDamage(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player) {
         Player victim = (Player)event.getEntity();
         FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(victim.getUniqueId());
         if (event.getDamager() instanceof Player) {
            Player attackerPlayer = (Player)event.getDamager();
            if (fp.isTrusted(attackerPlayer.getUniqueId())) {
               return;
            }
         }

         PassiveAbility passive = fp.getPassiveAbility();
         if (passive == PassiveAbility.SCORCH_SKIN) {
            double healthPercent = victim.getHealth() / victim.getAttribute(Attribute.MAX_HEALTH).getValue();
            double threshold = this.plugin.getConfig().getDouble("passives.scorch-skin.health-threshold", 0.3);
            if (healthPercent <= threshold && event.getDamager() instanceof LivingEntity) {
               double scorchDamage = this.plugin.getConfig().getDouble("passives.scorch-skin.damage", 2.0);
               int fireTicks = this.plugin.getConfig().getInt("passives.scorch-skin.fire-duration", 60);
               LivingEntity attacker = (LivingEntity)event.getDamager();
               attacker.damage(scorchDamage, victim);
               attacker.setFireTicks(fireTicks);
               victim.getWorld().spawnParticle(Particle.FLAME, victim.getLocation().add(0.0, 1.0, 0.0), 40, 0.5, 0.5, 0.5, 0.1);
               victim.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, victim.getLocation().add(0.0, 1.0, 0.0), 20, 0.3, 0.5, 0.3, 0.05);
               victim.getWorld().spawnParticle(Particle.LAVA, victim.getLocation().add(0.0, 1.0, 0.0), 10, 0.3, 0.3, 0.3, 0.0);
               victim.playSound(victim.getLocation(), Sound.ENTITY_BLAZE_HURT, 1.0F, 1.2F);
               attacker.getWorld().spawnParticle(Particle.FLAME, attacker.getLocation().add(0.0, 1.0, 0.0), 30, 0.5, 0.5, 0.5, 0.1);
               if (attacker instanceof Player) {
                   attacker.sendMessage(ChatColor.RED + "You were burned by " + victim.getName() + "'s Scorch Skin!");
               }
            }
         }
      }
   }
}
