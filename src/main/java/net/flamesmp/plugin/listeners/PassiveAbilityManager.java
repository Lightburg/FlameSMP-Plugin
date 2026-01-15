package net.flamesmp.plugin.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.flamesmp.plugin.FlameSMP;
import net.flamesmp.plugin.abilities.PassiveAbility;
import net.flamesmp.plugin.data.FlamePlayer;
import net.flamesmp.plugin.essences.Essence;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.audience.Audience;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World.Environment;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PassiveAbilityManager implements Listener {
   private final FlameSMP plugin;
    private final Map<UUID, Long> lastSpeedBoostTime;
   private final Map<UUID, Boolean> lavabornActive;
   private final Map<UUID, AttributeModifier> lavabornModifiers;
   private static final UUID LAVABORN_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

   public PassiveAbilityManager(FlameSMP plugin) {
      this.plugin = plugin;
       this.lastSpeedBoostTime = new HashMap<>();
      this.lavabornActive = new HashMap<>();
      this.lavabornModifiers = new HashMap<>();
      plugin.getServer().getPluginManager().registerEvents(this, plugin);
      this.startPassiveChecks();
      this.startHeatDecay();
   }

   private void startPassiveChecks() {
      (new BukkitRunnable() {
         public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
               FlamePlayer fp = PassiveAbilityManager.this.plugin.getDataManager().getFlamePlayer(player.getUniqueId());
               PassiveAbility passive = fp.getPassiveAbility();
               if (passive != null) {
                  switch (passive) {
                     case THERMAL_MOMENTUM:
                        PassiveAbilityManager.this.checkThermalMomentum(player, fp);
                        break;
                     case SCORCH_SKIN:
                        PassiveAbilityManager.this.checkScorchSkin(player, fp);
                        break;
                     case LAVABORN:
                        PassiveAbilityManager.this.checkLavaborn(player, fp);
                        break;
                     case EMBER_RECOVERY:
                        PassiveAbilityManager.this.checkEmberRecovery(player);
                        break;
                     case HEAT_PRESSURE:
                        PassiveAbilityManager.this.checkHeatPressure(player, fp);
                        break;
                     case PYRO_ADRENALINE:
                        PassiveAbilityManager.this.checkPyroAdrenaline(player, fp);
                        break;
                     case SOLAR_CHARGED:
                        PassiveAbilityManager.this.checkSolarCharged(player, fp);
                  }
               }

               PassiveAbilityManager.this.updateHeatDisplay(player, fp);
            }
         }
      }).runTaskTimer(this.plugin, 20L, 10L);
   }

   private void startHeatDecay() {
      int decayInterval = this.plugin.getConfig().getInt("heat.decay-interval", 60) * 20;
      (new BukkitRunnable() {
         public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
               FlamePlayer fp = PassiveAbilityManager.this.plugin.getDataManager().getFlamePlayer(player.getUniqueId());
               if (fp.getHitCounter() > 0) {
                  fp.decrementHitCounter();
               }
            }
         }
      }).runTaskTimer(this.plugin, decayInterval, decayInterval);
   }

   private void checkThermalMomentum(Player player, FlamePlayer fp) {
      UUID playerId = player.getUniqueId();
      long currentTime = System.currentTimeMillis();
      if (player.isSprinting()) {
         if (!fp.isSprinting()) {
            fp.setSprinting(true);
         }

         long sprintDuration = currentTime - fp.getSprintStartTime();
         int requiredTime = this.plugin.getConfig().getInt("passives.thermal-momentum.sprint-time-required", 10) * 1000;
         Long lastBoost = this.lastSpeedBoostTime.get(playerId);
         boolean canBoost = lastBoost == null || currentTime - lastBoost > 2500L;
         if (sprintDuration >= requiredTime && canBoost) {
            int speedLevel = this.plugin.getConfig().getInt("passives.thermal-momentum.speed-level", 1);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, speedLevel - 1, true, true));
            this.lastSpeedBoostTime.put(playerId, currentTime);
            player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.5F, 1.5F);

            for (int i = 0; i < 8; i++) {
               double angle = Math.random() * Math.PI * 2.0;
               double x = Math.cos(angle) * 0.5;
               double z = Math.sin(angle) * 0.5;
               player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(x, 0.2, z), 3, 0.1, 0.1, 0.1, 0.02);
               player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().add(x, 0.1, z), 2, 0.05, 0.05, 0.05, 0.01);
            }

            fp.setSprinting(false);
         }
      } else if (fp.isSprinting()) {
         fp.setSprinting(false);
      }
   }

   private void checkScorchSkin(Player player, FlamePlayer fp) {
      double health = player.getHealth();
      double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
      double healthPercent = health / maxHealth;
      double threshold = this.plugin.getConfig().getDouble("passives.scorch-skin.health-threshold", 0.4);
      if (healthPercent <= threshold && Math.random() < 0.3) {
         player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0.0, 1.0, 0.0), 5, 0.3, 0.5, 0.3, 0.02);
         player.getWorld().spawnParticle(Particle.LAVA, player.getLocation().add(0.0, 0.5, 0.0), 2, 0.2, 0.2, 0.2, 0.0);
      }
   }

   @EventHandler
   public void onPlayerDamaged(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player) {
         Player victim = (Player)event.getEntity();
         FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(victim.getUniqueId());
         if (fp.getPassiveAbility() == PassiveAbility.SCORCH_SKIN) {
            double health = victim.getHealth();
            double maxHealth = victim.getAttribute(Attribute.MAX_HEALTH).getValue();
            double healthPercent = health / maxHealth;
            double threshold = this.plugin.getConfig().getDouble("passives.scorch-skin.health-threshold", 0.4);
            if (healthPercent <= threshold && event.getDamager() instanceof Player) {
               Player attacker = (Player)event.getDamager();
               if (!fp.isTrusted(attacker.getUniqueId())) {
                  double damage = this.plugin.getConfig().getDouble("passives.scorch-skin.damage", 2.0);
                  int fireDuration = this.plugin.getConfig().getInt("passives.scorch-skin.fire-duration", 60);
                  attacker.damage(damage);
                  attacker.setFireTicks(fireDuration);
                  attacker.getWorld().spawnParticle(Particle.FLAME, attacker.getLocation().add(0.0, 1.0, 0.0), 30, 0.5, 0.8, 0.5, 0.1);
                  attacker.getWorld().spawnParticle(Particle.LAVA, attacker.getLocation().add(0.0, 1.0, 0.0), 15, 0.4, 0.6, 0.4, 0.0);
                  victim.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, victim.getLocation().add(0.0, 1.0, 0.0), 20, 0.3, 0.5, 0.3, 0.05);
                  attacker.playSound(attacker.getLocation(), Sound.ENTITY_BLAZE_HURT, 1.0F, 0.8F);
                  victim.playSound(victim.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.2F);
                  attacker.sendMessage(ChatColor.RED + "You were burned by " + victim.getName() + "'s Scorch Skin!");
               }
            }
         }

         if (fp.getPassiveAbility() == PassiveAbility.HEAT_PRESSURE) {
            fp.incrementHitCounter();
            if (fp.isHeatMaxed()) {
               victim.getWorld().spawnParticle(Particle.FLAME, victim.getLocation().add(0.0, 1.0, 0.0), 40, 0.5, 0.8, 0.5, 0.1);
               victim.getWorld().spawnParticle(Particle.LAVA, victim.getLocation().add(0.0, 1.0, 0.0), 20, 0.4, 0.6, 0.4, 0.0);
               victim.playSound(victim.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1.0F, 0.5F);
            }
         }
      }
   }

   private void checkLavaborn(Player player, FlamePlayer fp) {
      UUID playerId = player.getUniqueId();
      boolean wasActive = this.lavabornActive.getOrDefault(playerId, false);
      boolean isOnFire = player.getFireTicks() > 0;
      int extraHearts = this.plugin.getConfig().getInt("passives.lavaborn.extra-hearts", 2);
      double bonusHealth = extraHearts * 2.0;
      if (isOnFire && !wasActive) {
         AttributeModifier existingMod = this.lavabornModifiers.get(playerId);
         if (existingMod != null) {
            try {
               player.getAttribute(Attribute.MAX_HEALTH).removeModifier(existingMod);
            } catch (Exception var16) {
            }
         }

         for (AttributeModifier mod : player.getAttribute(Attribute.MAX_HEALTH).getModifiers()) {
            if (mod.getName().equals("lavaborn_bonus") || mod.getKey().getKey().equals(LAVABORN_MODIFIER_UUID.toString())) {
               try {
                  player.getAttribute(Attribute.MAX_HEALTH).removeModifier(mod);
               } catch (Exception var15) {
               }
            }
         }

         AttributeModifier modifier = new AttributeModifier(new NamespacedKey(plugin, "lavaborn_bonus"), bonusHealth, Operation.ADD_NUMBER, EquipmentSlotGroup.ANY);
         player.getAttribute(Attribute.MAX_HEALTH).addModifier(modifier);
         this.lavabornModifiers.put(playerId, modifier);
         this.lavabornActive.put(playerId, true);
         player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 0.5F, 1.2F);
         player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0.0, 1.0, 0.0), 20, 0.5, 0.8, 0.5, 0.1);
         player.getWorld().spawnParticle(Particle.LAVA, player.getLocation().add(0.0, 1.5, 0.0), 10, 0.4, 0.5, 0.4, 0.0);
      } else if (!isOnFire && wasActive) {
         AttributeModifier modifier = this.lavabornModifiers.get(playerId);
         if (modifier != null) {
            try {
               player.getAttribute(Attribute.MAX_HEALTH).removeModifier(modifier);
            } catch (Exception var14) {
            }

            this.lavabornModifiers.remove(playerId);
         }

         for (AttributeModifier modx : player.getAttribute(Attribute.MAX_HEALTH).getModifiers()) {
            if (modx.getName().equals("lavaborn_bonus")) {
               try {
                  player.getAttribute(Attribute.MAX_HEALTH).removeModifier(modx);
               } catch (Exception var13) {
               }
            }
         }

         this.lavabornActive.put(playerId, false);
         if (player.getHealth() > player.getAttribute(Attribute.MAX_HEALTH).getValue()) {
            player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
         }

         player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().add(0.0, 1.0, 0.0), 15, 0.3, 0.5, 0.3, 0.02);
      }

      if (isOnFire && Math.random() < 0.3) {
         player.getWorld().spawnParticle(Particle.LAVA, player.getLocation().add(0.0, 1.0, 0.0), 2, 0.3, 0.5, 0.3, 0.0);
         player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0.0, 0.5, 0.0), 3, 0.2, 0.2, 0.2, 0.02);
      }
   }

   private void checkEmberRecovery(Player player) {
       if (isDaytime(player)) {
         int regenInterval = this.plugin.getConfig().getInt("passives.ember-recovery.regen-interval", 40);
         if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, regenInterval + 10, 0, true, true));
             player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0.0, 1.0, 0.0), 5, 0.2, 0.3, 0.2, 0.01);
             player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0.0, 2.0, 0.0), 1, 0.2, 0.1, 0.2, 0.0);
         }
      }
   }

   private boolean isDaytime(Player player) {
      World world = player.getWorld();
      if (world.getEnvironment() != Environment.NORMAL) {
         return false;
      } else {
         long time = world.getTime();
         return time < 13081 || time > 22920;
      }
   }

   private void checkHeatPressure(Player player, FlamePlayer fp) {
      int hitCount = fp.getHitCounter();
      int maxHits = this.plugin.getConfig().getInt("passives.heat-pressure.hits-required", 15);
      if (hitCount > 0) {
         float intensity = (float)hitCount / maxHits;
         if (Math.random() < intensity * 0.3) {
            player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0.0, 1.0, 0.0), (int)(intensity * 10.0F), 0.3, 0.5, 0.3, 0.02);
            if (intensity > 0.5) {
               player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().add(0.0, 1.5, 0.0), 3, 0.2, 0.3, 0.2, 0.01);
            }
         }
      }
   }

   private void checkPyroAdrenaline(Player player, FlamePlayer fp) {
      double health = player.getHealth();
      double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
      double healthPercent = health / maxHealth;
      int maxSpeedLevel = this.plugin.getConfig().getInt("passives.pyro-adrenaline.max-speed-level", 2);
      if (healthPercent <= 0.3) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, maxSpeedLevel - 1, true, true));
         player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0.0, 0.5, 0.0), 8, 0.4, 0.4, 0.4, 0.05);
         player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().add(0.0, 0.3, 0.0), 4, 0.2, 0.2, 0.2, 0.02);
         player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation().add(0.0, 0.2, 0.0), 3, 0.3, 0.1, 0.3, 0.02);
      } else if (healthPercent <= 0.5) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, Math.max(0, maxSpeedLevel - 2), true, true));
         player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0.0, 0.5, 0.0), 4, 0.2, 0.2, 0.2, 0.03);
      }
   }

   private void checkSolarCharged(Player player, FlamePlayer fp) {
      if (this.isInSunlight(player)) {
         if (this.isDaytime(player)) {
            int hasteLevel = this.plugin.getConfig().getInt("passives.solar-charged.haste-level", 1);
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 40, hasteLevel - 1, true, true));
            if (Math.random() < 0.2) {
               player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0.0, 2.5, 0.0), 2, 0.2, 0.1, 0.2, 0.01);
               player.getWorld().spawnParticle(Particle.DUST, player.getLocation().add(0.0, 2.0, 0.0), 3, 0.3, 0.2, 0.3, new DustOptions(Color.YELLOW, 1.0F));
            }
         }
      }
   }

    private boolean isInSunlight(Player player) {
      World world = player.getWorld();
      if (!isDaytime(player)) {
         return false;
      } else {
         int highestY = world.getHighestBlockYAt(player.getLocation());
         return player.getLocation().getY() >= highestY && !world.hasStorm();
      }
   }

   private Essence getHeldEssence(Player player) {
      ItemStack mainHand = player.getInventory().getItemInMainHand();
      ItemStack offHand = player.getInventory().getItemInOffHand();
      Essence mainEssence = this.plugin.getEssenceManager().getEssenceFromItem(mainHand);
      return mainEssence != null ? mainEssence : this.plugin.getEssenceManager().getEssenceFromItem(offHand);
   }

   private TextColor getEssenceTextColor(Essence essence) {
       return switch (essence) {
           case FIRE -> NamedTextColor.RED;
           case LAVA -> NamedTextColor.GOLD;
           case LIGHTNING -> NamedTextColor.YELLOW;
           case MAGMA -> NamedTextColor.DARK_RED;
           case PLASMA -> NamedTextColor.LIGHT_PURPLE;
           case LIGHT -> NamedTextColor.WHITE;
       };
   }

   private void updateHeatDisplay(Player player, FlamePlayer fp) {
      Component actionBar = Component.empty();
      Essence heldEssence = this.getHeldEssence(player);
      if (heldEssence != null) {
         int abilitySlot = fp.getEssenceAbilitySlot(heldEssence);
         String icon = heldEssence.getAbilityIcon(abilitySlot);
         String cooldownKey = heldEssence.name() + "_" + abilitySlot;
         long cooldownRemaining = fp.getCooldownRemaining(cooldownKey);
         TextColor essenceColor = this.getEssenceTextColor(heldEssence);
         if (cooldownRemaining > 0L) {
            actionBar = actionBar.append(Component.text(icon, essenceColor, TextDecoration.BOLD))
               .append(Component.text(" ", NamedTextColor.GRAY))
               .append(Component.text(cooldownRemaining + "s", NamedTextColor.RED));
         } else {
            actionBar = actionBar.append(Component.text(icon, essenceColor, TextDecoration.BOLD))
               .append(Component.text(" ", NamedTextColor.GRAY))
               .append(Component.text("READY", NamedTextColor.GREEN));
         }
         Audience audience = (Audience) player;
         audience.sendActionBar(actionBar);
      }
   }
}
