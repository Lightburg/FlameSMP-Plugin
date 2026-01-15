package net.flamesmp.plugin.essences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.flamesmp.plugin.FlameSMP;
import net.flamesmp.plugin.data.FlamePlayer;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class EssenceAbilities {
   private final FlameSMP plugin;
   private final Map<UUID, Integer> rockCounts;
   private final Map<UUID, Boolean> chargingOverload;

    public EssenceAbilities(FlameSMP plugin) {
      this.plugin = plugin;
      this.rockCounts = new HashMap<>();
      this.chargingOverload = new HashMap<>();
    }

   public void executeAbility(Player player, FlamePlayer fp, Essence essence, int slot) {
      switch (essence) {
         case FIRE:
            if (slot == 0) {
               this.fireRingAbility(player, fp);
            } else {
               this.fireScorchAbility(player, fp);
            }
            break;
         case LAVA:
            if (slot == 0) {
               this.lavaPoolAbility(player, fp);
            } else {
               this.lavaWallAbility(player, fp);
            }
            break;
         case LIGHTNING:
            if (slot == 0) {
               this.lightningStrikeAbility(player, fp);
            } else {
               this.acidStormAbility(player, fp);
            }
            break;
         case MAGMA:
            if (slot == 0) {
               this.meteorAbility(player, fp);
            } else {
               this.orbitingRocksAbility(player, fp);
            }
            break;
         case PLASMA:
            if (slot == 0) {
               this.reactorOverloadAbility(player, fp);
            } else {
               this.overheatModeAbility(player, fp);
            }
            break;
         case LIGHT:
            if (slot == 0) {
               this.blindingFlashAbility(player, fp);
            } else {
               this.lightBeamAbility(player, fp);
            }
      }
   }

   private void fireRingAbility(final Player player, final FlamePlayer fp) {
      int cooldown = this.plugin.getConfig().getInt("essences.fire.ring-cooldown", 60);
      final int radius = this.plugin.getConfig().getInt("essences.fire.ring-radius", 10);
      double baseKnockback = this.plugin.getConfig().getInt("essences.fire.knockback-distance", 20);
      final double knockback = baseKnockback * 0.5;
      fp.setCooldown("FIRE_0", cooldown);
      player.sendMessage(ChatColor.RED + "\ud83d\udd25 " + ChatColor.GOLD + "Ring of Flames" + ChatColor.RED + " activated!");
      player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.5F, 0.3F);
      player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.5F, 0.5F);
      player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8F, 1.5F);
      (new BukkitRunnable() {
         double angle = 0.0;
         int ticks = 0;
         double currentRadius = 0.0;

         public void run() {
            if (this.ticks >= 50) {
               player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 0.5F);
               player.playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.5F, 0.8F);
               player.playSound(player.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 1.0F, 1.2F);
               player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, player.getLocation().add(0.0, 1.0, 0.0), 3, 1.0, 1.0, 1.0, 0.0);
               player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 300, 8.0, 3.0, 8.0, 0.3);
               player.getWorld().spawnParticle(Particle.LAVA, player.getLocation(), 100, 6.0, 2.0, 6.0, 0.0);
               player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation().add(0.0, 0.5, 0.0), 150, 6.0, 2.0, 6.0, 0.2);
               player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().add(0.0, 2.0, 0.0), 100, 5.0, 3.0, 5.0, 0.1);
               player.getWorld().spawnParticle(Particle.ASH, player.getLocation().add(0.0, 3.0, 0.0), 200, 8.0, 4.0, 8.0, 0.1);

               for (int ring = 0; ring < 3; ring++) {
                  for (int i = 0; i < 40; i++) {
                     double ringAngle = i * Math.PI * 2.0 / 40.0;
                     double ringRadius = radius * (0.5 + ring * 0.3);
                     double x = Math.cos(ringAngle) * ringRadius;
                     double z = Math.sin(ringAngle) * ringRadius;
                     Location loc = player.getLocation().add(x, 0.5 + ring * 0.5, z);
                     player.getWorld().spawnParticle(Particle.FLAME, loc, 8, 0.2, 0.5, 0.2, 0.05);
                  }
               }

               for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                  if (entity instanceof Player target && !fp.isTrusted(target.getUniqueId())) {
                     Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                     direction.setY(0.8);
                     target.setVelocity(direction.multiply(knockback / 4.0));
                     target.setFireTicks(100);
                     target.setHealth(Math.max(0.0, target.getHealth() - 1.0));
                     target.playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.5F, 1.0F);
                     target.getWorld().spawnParticle(Particle.EXPLOSION, target.getLocation(), 5, 0.5, 0.5, 0.5, 0.0);
                     target.getWorld().spawnParticle(Particle.FLAME, target.getLocation(), 50, 0.5, 1.0, 0.5, 0.2);
                  }
               }

               this.cancel();
            } else {
               this.currentRadius = radius * this.ticks / 50.0;
               float pitch = 0.5F + this.ticks / 50.0F;
               if (this.ticks % 5 == 0) {
                  player.playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 1.0F, pitch);
               }

               for (int i = 0; i < 30; i++) {
                  double particleAngle = this.angle + i * Math.PI * 2.0 / 30.0;
                  double x = Math.cos(particleAngle) * this.currentRadius;
                  double z = Math.sin(particleAngle) * this.currentRadius;
                  Location loc = player.getLocation().add(x, 0.3, z);
                  player.getWorld().spawnParticle(Particle.FLAME, loc, 8, 0.15, 0.3, 0.15, 0.03);
                  player.getWorld().spawnParticle(Particle.SMOKE, loc.clone().add(0.0, 0.2, 0.0), 3, 0.1, 0.15, 0.1, 0.02);
                  if (this.ticks % 3 == 0) {
                     player.getWorld().spawnParticle(Particle.LAVA, loc, 1, 0.0, 0.0, 0.0, 0.0);
                  }
               }

               for (int ix = 0; ix < 16; ix++) {
                  double spiralAngle = this.angle * 3.0 + ix * Math.PI / 8.0;
                  double spiralRadius = this.currentRadius * 0.6;
                  double spiralX = Math.cos(spiralAngle) * spiralRadius;
                  double spiralZ = Math.sin(spiralAngle) * spiralRadius;
                  double height = 1.5 + Math.sin(this.ticks * 0.15 + ix * 0.5);
                  Location spiralLoc = player.getLocation().add(spiralX, height, spiralZ);
                  player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, spiralLoc, 3, 0.08, 0.08, 0.08, 0.02);
               }

               for (int ix = 0; ix < 8; ix++) {
                  double columnAngle = ix * Math.PI / 4.0 + this.angle * 0.5;
                  double columnX = Math.cos(columnAngle) * this.currentRadius * 0.8;
                  double columnZ = Math.sin(columnAngle) * this.currentRadius * 0.8;

                  for (double h = 0.0; h < 3.0; h += 0.3) {
                     Location columnLoc = player.getLocation().add(columnX, h, columnZ);
                     player.getWorld().spawnParticle(Particle.FLAME, columnLoc, 2, 0.1, 0.1, 0.1, 0.01);
                  }
               }

               this.angle += 0.2;
               this.ticks++;
            }
         }
      }).runTaskTimer(this.plugin, 0L, 1L);
   }

   private void fireScorchAbility(final Player player, FlamePlayer fp) {
      int cooldown = this.plugin.getConfig().getInt("essences.fire.burn-cooldown", 60);
      int radius = this.plugin.getConfig().getInt("essences.fire.burn-radius", 30);
      int duration = this.plugin.getConfig().getInt("essences.fire.burn-duration", 40);
      fp.setCooldown("FIRE_1", cooldown);
      player.sendMessage(ChatColor.RED + "\ud83d\udd25 " + ChatColor.GOLD + "Scorching Aura" + ChatColor.RED + " activated!");
      player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 2.0F, 0.3F);
      player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.5F, 1.5F);
      player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.5F, 0.5F);
      player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, player.getLocation().add(0.0, 1.0, 0.0), 2, 0.5, 0.5, 0.5, 0.0);
      player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0.0, 1.0, 0.0), 200, 3.0, 3.0, 3.0, 0.4);
      player.getWorld().spawnParticle(Particle.LAVA, player.getLocation().add(0.0, 1.0, 0.0), 80, 2.0, 2.0, 2.0, 0.0);
      player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation().add(0.0, 2.0, 0.0), 100, 2.0, 2.0, 2.0, 0.3);
      (new BukkitRunnable() {
         int wave = 0;

         public void run() {
            if (this.wave >= 15) {
               this.cancel();
            } else {
               double waveRadius = this.wave * 2.5;

               for (int i = 0; i < 50; i++) {
                  double angle = i * 2 * Math.PI / 50.0;
                  double x = Math.cos(angle) * waveRadius;
                  double z = Math.sin(angle) * waveRadius;
                  Location loc = player.getLocation().add(x, 0.3, z);
                  player.getWorld().spawnParticle(Particle.FLAME, loc, 10, 0.3, 0.5, 0.3, 0.05);
                  if (i % 5 == 0) {
                     for (double h = 0.0; h < 4.0; h += 0.4) {
                        player.getWorld().spawnParticle(Particle.FLAME, loc.clone().add(0.0, h, 0.0), 3, 0.1, 0.1, 0.1, 0.02);
                     }

                     player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc.clone().add(0.0, 3.0, 0.0), 5, 0.2, 0.3, 0.2, 0.03);
                  }
               }

               player.getWorld().playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.8F, 0.5F + this.wave * 0.05F);
               this.wave++;
            }
         }
      }).runTaskTimer(this.plugin, 0L, 2L);

      for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
         if (entity instanceof Player target && !fp.isTrusted(target.getUniqueId())) {
            target.setFireTicks(duration * 20);
            target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration * 20, 1));
            target.setHealth(Math.max(0.0, target.getHealth() - 1.0));
            (new BukkitRunnable() {
                int burst = 0;
                int ticks = 0;
                boolean hu = true;

                public void run() {
                    if (!target.isOnline()) {
                        this.cancel();
                        return;
                    }

                    // Particle and sound bursts
                    if (burst < 5) {
                        Location loc = target.getLocation();
                        target.getWorld().spawnParticle(Particle.FLAME, loc.clone().add(0, 1, 0), 80, 0.8, 1.5, 0.8, 0.15);
                        target.getWorld().spawnParticle(Particle.LAVA, loc.clone().add(0, 1, 0), 30, 0.5, 0.8, 0.5, 0.0);
                        target.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc.clone().add(0, 2, 0), 20, 0.4, 0.5, 0.4, 0.1);
                        target.playSound(loc, Sound.ENTITY_BLAZE_HURT, 1.0F, 0.8F + burst * 0.1F);
                        burst++;
                    }

                    // Apply poison once if player is not on fire
                    if (target.getFireTicks() == 0 && hu) {
                        int poisonDuration = Math.max(duration * 20 - ticks, 1); // prevent negative duration
                        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, poisonDuration, 2));
                        hu = false;
                    }

                    ticks += 1;

                    // Cancel task if bursts are done and poison applied
                    if (burst >= 5 && !hu) {
                        this.cancel();
                    }
            }
            }).runTaskTimer(this.plugin, 0L, 4L);
            target.sendMessage(ChatColor.RED + "\ud83d\udd25 You have been scorched by " + player.getName() + "!");
         }
      }
   }

   private void lavaPoolAbility(final Player player, FlamePlayer fp) {
      int cooldown = this.plugin.getConfig().getInt("essences.lava.block-cooldown", 60);
      int poolSize = 8;
      int duration = 15;
      fp.setCooldown("LAVA_0", cooldown);
      this.giveFireResistanceToAllies(player, fp);
      player.sendMessage(ChatColor.GOLD + "\ud83c\udf0b " + ChatColor.RED + "Lava Pool" + ChatColor.GOLD + " activated!");
      player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1.5F, 0.3F);
      player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8F, 1.2F);
      player.playSound(player.getLocation(), Sound.BLOCK_LAVA_AMBIENT, 2.0F, 0.5F);
      player.getWorld().spawnParticle(Particle.LAVA, player.getLocation().add(0.0, 1.0, 0.0), 100, 2.0, 2.0, 2.0, 0.0);
      player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0.0, 0.5, 0.0), 80, 2.0, 1.0, 2.0, 0.1);
      final Location playerLoc = player.getLocation().clone();
      final Map<Block, Material> originalBlocks = new HashMap<>();
      int halfSize = poolSize / 2;

      for (int x = -halfSize; x < halfSize; x++) {
         for (int z = -halfSize; z < halfSize; z++) {
            final Block block = playerLoc.clone().add(x, -1.0, z).getBlock();
            if (!originalBlocks.containsKey(block)) {
               originalBlocks.put(block, block.getType());
            }

            (new BukkitRunnable() {
               int bubbles = 0;

               public void run() {
                  if (this.bubbles >= 5) {
                     block.setType(Material.LAVA);
                     this.cancel();
                  } else {
                     Location bubbleLoc = block.getLocation().add(0.5, 1.0, 0.5);
                     player.getWorld().spawnParticle(Particle.LAVA, bubbleLoc, 5, 0.3, 0.2, 0.3, 0.0);
                     player.getWorld().spawnParticle(Particle.FLAME, bubbleLoc, 3, 0.2, 0.2, 0.2, 0.02);
                     this.bubbles++;
                  }
               }
            }).runTaskTimer(this.plugin, Math.abs(x) + Math.abs(z), 2L);
         }
      }

      (new BukkitRunnable() {
         public void run() {
            for (Entry<Block, Material> entry : originalBlocks.entrySet()) {
               if (entry.getKey().getType() == Material.LAVA) {
                  entry.getKey().setType(entry.getValue());
                  Location smokeLoc = entry.getKey().getLocation().add(0.5, 1.0, 0.5);
                  entry.getKey().getWorld().spawnParticle(Particle.SMOKE, smokeLoc, 10, 0.3, 0.3, 0.3, 0.05);
               }
            }

            player.getWorld().playSound(playerLoc, Sound.BLOCK_FIRE_EXTINGUISH, 1.5F, 0.8F);
            player.sendMessage(ChatColor.GRAY + "The lava pool has cooled.");
         }
      }).runTaskLater(this.plugin, duration * 20L);
   }

   private void lavaWallAbility(final Player player, final FlamePlayer fp) {
      int cooldown = this.plugin.getConfig().getInt("essences.lava.wall-cooldown", 90);
      final int wallSize = this.plugin.getConfig().getInt("essences.lava.wall-size", 3);
      int distance = this.plugin.getConfig().getInt("essences.lava.wall-distance", 10);
      fp.setCooldown("LAVA_1", cooldown);
      this.giveFireResistanceToAllies(player, fp);
      player.sendMessage(ChatColor.GOLD + "\ud83c\udf0b " + ChatColor.RED + "Lava Wall" + ChatColor.GOLD + " activated!");
      player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.5F, 0.5F);
      player.playSound(player.getLocation(), Sound.BLOCK_LAVA_AMBIENT, 2.0F, 0.3F);
      player.playSound(player.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 1.0F, 0.8F);
      final Vector direction = player.getLocation().getDirection().setY(0).normalize();
      final Location startLoc = player.getLocation().add(direction.clone().multiply(2));
      final Map<Block, Material> originalBlocks = new HashMap<>();
      final Vector perpendicular = new Vector(-direction.getZ(), 0.0, direction.getX());
      final Set<UUID> affectedPlayers = new HashSet<>();

       for (int i = 0; i < distance; i++) {
           final int step = i;

           new BukkitRunnable() {
               @Override
               public void run() {
                   Location baseLoc = startLoc.clone().add(direction.clone().multiply(step));
                   World world = baseLoc.getWorld();

                   assert world != null;
                   world.playSound(baseLoc, Sound.BLOCK_LAVA_POP, 1.5F, 0.6F + step * 0.03F);
                   world.playSound(baseLoc, Sound.ENTITY_BLAZE_SHOOT, 0.5F, 0.5F);

                   for (int w = -wallSize / 2; w <= wallSize / 2; w++) {
                       for (int h = 0; h < wallSize; h++) {
                           Location blockLoc = baseLoc.clone()
                                   .add(perpendicular.clone().multiply(w))
                                   .add(0, h, 0);

                           Block block = blockLoc.getBlock();

                           if (!originalBlocks.containsKey(block)) {
                               originalBlocks.put(block, block.getType());
                           }

                           block.setType(Material.LAVA);

                           world.spawnParticle(Particle.LAVA, blockLoc.add(0.5, 0.5, 0.5), 15, 0.4, 0.4, 0.4, 0);
                           world.spawnParticle(Particle.FLAME, blockLoc, 20, 0.3, 0.3, 0.3, 0.1);
                           world.spawnParticle(Particle.SMOKE, blockLoc.clone().add(0, 0.5, 0), 10, 0.3, 0.4, 0.3, 0.05);

                           for (Entity e : world.getNearbyEntities(blockLoc, 2, 2, 2)) {
                               if (!(e instanceof Player target)) continue;
                               if (target.isDead()) continue;
                               if (target.equals(player)) continue;
                               if (fp.isTrusted(target.getUniqueId())) continue;
                               if (!affectedPlayers.add(target.getUniqueId())) continue;

                               target.setHealth(Math.max(0, target.getHealth()-8));
                               target.setFireTicks(25);
                               target.setVelocity(direction.clone().multiply(0.8).setY(0.3));

                               world.spawnParticle(Particle.FLAME, target.getLocation(), 30, 0.5, 1, 0.5, 0.1);
                           }
                       }
                   }
               }
           }.runTaskLater(plugin, step * 3L);
       }

       (new BukkitRunnable() {
         public void run() {
            for (Entry<Block, Material> entry : originalBlocks.entrySet()) {
               if (entry.getKey().getType() == Material.LAVA) {
                  entry.getKey().setType(entry.getValue());
                  Location smokeLoc = entry.getKey().getLocation().add(0.5, 0.5, 0.5);
                  entry.getKey().getWorld().spawnParticle(Particle.SMOKE, smokeLoc, 20, 0.4, 0.4, 0.4, 0.05);
                  entry.getKey().getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, smokeLoc.add(0.0, 0.5, 0.0), 8, 0.3, 0.3, 0.3, 0.02);
               }
            }

            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1.5F, 0.8F);
         }
      }).runTaskLater(this.plugin, distance * 3L + 80L);
   }

   private void giveFireResistanceToAllies(Player player, FlamePlayer fp) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 30 * 20, 0, true, true));
      player.sendMessage(ChatColor.GOLD + "You gained 30s of fire resistance!");

      for (UUID trustedId : fp.getTrustedPlayers()) {
         Player trusted = this.plugin.getServer().getPlayer(trustedId);
         if (trusted != null && trusted.isOnline() && trusted.getLocation().distance(player.getLocation()) <= 50.0) {
            trusted.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 30 * 20, 0, true, true));
            trusted.sendMessage(ChatColor.GOLD + player.getName() + " granted you " + 30 + "s of fire resistance!");
         }
      }
   }

   private void lightningStrikeAbility(final Player player, FlamePlayer fp) {
      int cooldown = this.plugin.getConfig().getInt("essences.lightning.strike-cooldown", 60);
      int radius = this.plugin.getConfig().getInt("essences.lightning.strike-radius", 25);
      double baseDamage = this.plugin.getConfig().getDouble("essences.lightning.strike-damage", 8.0);
      final double damage = baseDamage * 1.3;
      fp.setCooldown("LIGHTNING_0", cooldown);
      player.sendMessage(ChatColor.YELLOW + "⚡ " + ChatColor.WHITE + "Thunder Strike" + ChatColor.YELLOW + " activated!");
      player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0F, 0.5F);
      player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.3F, 2.0F);

      for (int i = 0; i < 8; i++) {
         double angle = i * Math.PI / 4.0;

         for (double h = 0.0; h < 15.0; h += 0.5) {
            Location boltLoc = player.getLocation().add(Math.cos(angle) * 0.3, h, Math.sin(angle) * 0.3);
            player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, boltLoc, 5, 0.1, 0.1, 0.1, 0.1);
         }
      }

      player.getWorld().spawnParticle(Particle.FLASH, player.getLocation().add(0.0, 2.0, 0.0), 3, 0.0, 0.0, 0.0, 0.0);
      List<Player> targets = new ArrayList<>();

      for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
         if (entity instanceof Player target && !fp.isTrusted(target.getUniqueId())) {
            targets.add(target);
         }
      }

      for (int t = 0; t < targets.size(); t++) {
         final Player target = targets.get(t);
         int delay = t * 5;
         (new BukkitRunnable() {
            public void run() {
               target.getWorld().strikeLightningEffect(target.getLocation());
               target.playSound(target.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0F, 0.8F);
               target.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, target.getLocation().add(0.0, 1.0, 0.0), 100, 1.0, 2.0, 1.0, 0.5);
               target.getWorld().spawnParticle(Particle.FLASH, target.getLocation().add(0.0, 1.0, 0.0), 5, 0.5, 0.5, 0.5, 0.0);

               for (int ring = 0; ring < 3; ring++) {
                  for (int i = 0; i < 20; i++) {
                     double angle = i * Math.PI * 2.0 / 20.0;
                     double ringRadius = 1.0 + ring * 0.5;
                     double x = Math.cos(angle) * ringRadius;
                     double z = Math.sin(angle) * ringRadius;
                     target.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, target.getLocation().add(x, 0.2, z), 3, 0.1, 0.1, 0.1, 0.05);
                  }
               }

               (new BukkitRunnable() {
                  int strikes = 0;

                  public void run() {
                     if (this.strikes >= 5) {
                        this.cancel();
                     } else {
                        Location strikeLoc = target.getLocation().add((Math.random() - 0.5) * 3.0, 0.0, (Math.random() - 0.5) * 3.0);

                        for (double h = 0.0; h < 10.0; h += 0.3) {
                           strikeLoc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, strikeLoc.clone().add(0.0, h, 0.0), 8, 0.15, 0.1, 0.15, 0.1);
                        }

                        strikeLoc.getWorld().spawnParticle(Particle.FLASH, strikeLoc.add(0.0, 1.0, 0.0), 2, 0.0, 0.0, 0.0, 0.0);
                        strikeLoc.getWorld().playSound(strikeLoc, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0F, 1.2F);
                        this.strikes++;
                     }
                  }
               }).runTaskTimer(EssenceAbilities.this.plugin, 3L, 4L);
               target.setHealth(Math.max(0.0, target.getHealth() - damage));
               target.sendMessage(ChatColor.YELLOW + "⚡ You were struck by " + player.getName() + "'s lightning!");
            }
         }).runTaskLater(this.plugin, delay);
      }
   }

   private void acidStormAbility(final Player player, final FlamePlayer fp) {
      int cooldown = this.plugin.getConfig().getInt("essences.lightning.storm-cooldown", 120);
      final int duration = this.plugin.getConfig().getInt("essences.lightning.storm-duration", 30);
      final int size = this.plugin.getConfig().getInt("essences.lightning.storm-size", 30);
      fp.setCooldown("LIGHTNING_1", cooldown);
      player.sendMessage(ChatColor.YELLOW + "⚡ " + ChatColor.DARK_PURPLE + "Acid Storm" + ChatColor.YELLOW + " activated!");
      player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0F, 0.3F);
      player.playSound(player.getLocation(), Sound.WEATHER_RAIN_ABOVE, 2.0F, 0.5F);
      player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1.5F, 0.5F);
      final Location center = player.getLocation().clone();
      (new BukkitRunnable() {
         int cloudTicks = 0;

         public void run() {
            if (this.cloudTicks >= 30) {
               this.cancel();
            } else {
               for (int i = 0; i < 50; i++) {
                  double x = center.getX() + (Math.random() - 0.5) * size;
                  double z = center.getZ() + (Math.random() - 0.5) * size;
                  Location cloudLoc = new Location(center.getWorld(), x, center.getY() + 18.0 + Math.random() * 5.0, z);
                  center.getWorld().spawnParticle(Particle.LARGE_SMOKE, cloudLoc, 5, 3.0, 1.0, 3.0, 0.02);
                  center.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, cloudLoc, 3, 2.0, 0.5, 2.0, 0.01);
               }

               this.cloudTicks++;
            }
         }
      }).runTaskTimer(this.plugin, 0L, 2L);
      (new BukkitRunnable() {
            int ticks = 0;

            public void run() {
               if (this.ticks >= duration * 20) {
                  player.playSound(center, Sound.WEATHER_RAIN_ABOVE, 1.0F, 1.5F);
                  this.cancel();
               } else {
                  for (int i = 0; i < 60; i++) {
                     double x = center.getX() + (Math.random() - 0.5) * size;
                     double z = center.getZ() + (Math.random() - 0.5) * size;
                     Location dropLoc = new Location(center.getWorld(), x, center.getY() + 20.0, z);
                     center.getWorld().spawnParticle(Particle.FALLING_LAVA, dropLoc, 1, 0.0, 0.0, 0.0, 0.0);
                     center.getWorld().spawnParticle(Particle.DRIPPING_LAVA, dropLoc.subtract(0.0, 3.0, 0.0), 1, 0.0, 0.0, 0.0, 0.0);
                     if (i % 3 == 0) {
                        center.getWorld().spawnParticle(Particle.DUST, dropLoc.subtract(0.0, 5.0, 0.0), 2, new DustOptions(Color.fromRGB(100, 200, 50), 1.2F));
                     }
                  }

                  for (int ix = 0; ix < 20; ix++) {
                     double x = center.getX() + (Math.random() - 0.5) * size;
                     double z = center.getZ() + (Math.random() - 0.5) * size;
                     Location cloudLoc = new Location(center.getWorld(), x, center.getY() + 18.0, z);
                     center.getWorld().spawnParticle(Particle.LARGE_SMOKE, cloudLoc, 3, 2.0, 0.5, 2.0, 0.01);
                  }

                  if (this.ticks % 10 == 0) {
                     for (Entity entity : center.getWorld().getNearbyEntities(center, (double) size / 2, 30.0, (double) size / 2)) {
                        if (entity instanceof Player target && !fp.isTrusted(target.getUniqueId())) {
                           target.setFireTicks(60);
                           target.setHealth(Math.max(0.0, target.getHealth() - 2.0));
                           target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 0));
                           target.getWorld().spawnParticle(Particle.FLAME, target.getLocation().add(0.0, 1.0, 0.0), 15, 0.4, 0.5, 0.4, 0.08);
                           target.getWorld()
                              .spawnParticle(Particle.DUST, target.getLocation().add(0.0, 1.5, 0.0), 10, new DustOptions(Color.fromRGB(100, 200, 50), 1.0F));
                        }
                     }
                  }

                  if (this.ticks % 25 == 0 && Math.random() < 0.7) {
                     double x = center.getX() + (Math.random() - 0.5) * size;
                     double z = center.getZ() + (Math.random() - 0.5) * size;
                     Location strikeLoc = new Location(center.getWorld(), x, center.getY(), z);
                     center.getWorld().strikeLightningEffect(strikeLoc);
                     center.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, strikeLoc.add(0.0, 1.0, 0.0), 80, 1.5, 2.0, 1.5, 0.5);
                     center.getWorld().spawnParticle(Particle.FLASH, strikeLoc, 3, 0.5, 0.5, 0.5, 0.0);
                     center.getWorld().playSound(strikeLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.5F, 0.8F);
                  }

                  if (this.ticks % 5 == 0) {
                     center.getWorld().playSound(center, Sound.WEATHER_RAIN, 0.5F, 0.8F);
                  }

                  this.ticks++;
               }
            }
         })
         .runTaskTimer(this.plugin, 30L, 1L);
   }

   private void meteorAbility(final Player player, FlamePlayer fp) {
      int cooldown = this.plugin.getConfig().getInt("essences.magma.meteor-cooldown", 120);
      int radius = this.plugin.getConfig().getInt("essences.magma.meteor-radius", 30);
      double baseDamage = this.plugin.getConfig().getDouble("essences.magma.meteor-damage", 10.0);
      final double damage = baseDamage * 0.7;
      fp.setCooldown("MAGMA_0", cooldown);
      Player nearestEnemy = null;
      double nearestDistance = Double.MAX_VALUE;

      for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
         if (entity instanceof Player target && !fp.isTrusted(target.getUniqueId())) {
            double distance = target.getLocation().distance(player.getLocation());
            if (distance < nearestDistance) {
               nearestDistance = distance;
               nearestEnemy = target;
            }
         }
      }

      if (nearestEnemy == null) {
         player.sendMessage(ChatColor.RED + "No enemies in range!");
         fp.setCooldown("MAGMA_0", 0L);
      } else {
         player.sendMessage(ChatColor.DARK_RED + "☄ " + ChatColor.GOLD + "Meteor Strike" + ChatColor.DARK_RED + " targeting " + nearestEnemy.getName() + "!");
         player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0F, 0.2F);
         player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.5F, 1.5F);
         player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.5F, 0.3F);
         final Location targetLoc = nearestEnemy.getLocation().clone();
         (new BukkitRunnable() {
            int warnTicks = 0;
            double warningRadius = 0.0;

            public void run() {
               if (this.warnTicks >= 60) {
                  this.cancel();
               } else {
                  this.warningRadius = 5.0 * (this.warnTicks / 60.0);

                  for (int j = 0; j < 40; j++) {
                     double angle = j * Math.PI * 2.0 / 40.0;
                     double x = Math.cos(angle) * this.warningRadius;
                     double z = Math.sin(angle) * this.warningRadius;
                     targetLoc.getWorld().spawnParticle(Particle.DUST, targetLoc.clone().add(x, 0.2, z), 2, new DustOptions(Color.RED, 2.0F));
                     targetLoc.getWorld().spawnParticle(Particle.FLAME, targetLoc.clone().add(x, 0.3, z), 1, 0.05, 0.05, 0.05, 0.01);
                  }

                  if (this.warnTicks % 10 == 0) {
                     targetLoc.getWorld().playSound(targetLoc, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 0.5F + this.warnTicks * 0.02F);
                  }

                  this.warnTicks++;
               }
            }
         }).runTaskTimer(this.plugin, 0L, 1L);
         (new BukkitRunnable() {
            final Location meteorLoc = targetLoc.clone().add(0.0, 60.0, 0.0);
            int fallTicks = 0;

            public void run() {
               this.meteorLoc.subtract(0.0, 3.0, 0.0);
               this.fallTicks++;
               if (this.meteorLoc.getY() <= targetLoc.getY() + 1.0) {
                  targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 0.3F);
                  targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 2.0F, 0.5F);
                  targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_WITHER_BREAK_BLOCK, 1.5F, 0.8F);
                  targetLoc.getWorld().createExplosion(targetLoc, 4.0F, true, false);
                  targetLoc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, targetLoc.add(0.0, 1.0, 0.0), 8, 2.0, 1.0, 2.0, 0.0);
                  targetLoc.getWorld().spawnParticle(Particle.FLAME, targetLoc, 500, 8.0, 4.0, 8.0, 0.5);
                  targetLoc.getWorld().spawnParticle(Particle.LAVA, targetLoc, 200, 6.0, 3.0, 6.0, 0.0);
                  targetLoc.getWorld().spawnParticle(Particle.SMOKE, targetLoc.add(0.0, 2.0, 0.0), 200, 6.0, 4.0, 6.0, 0.2);
                  targetLoc.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, targetLoc.add(0.0, 3.0, 0.0), 50, 4.0, 3.0, 4.0, 0.05);
                  targetLoc.getWorld().spawnParticle(Particle.ASH, targetLoc.add(0.0, 5.0, 0.0), 300, 10.0, 5.0, 10.0, 0.1);

                  for (int ring = 0; ring < 5; ring++) {
                     for (int i = 0; i < 30; i++) {
                        double angle = i * Math.PI * 2.0 / 30.0;
                        double ringRadius = 2 + ring * 2;
                        double x = Math.cos(angle) * ringRadius;
                        double z = Math.sin(angle) * ringRadius;
                        targetLoc.getWorld().spawnParticle(Particle.FLAME, targetLoc.clone().add(x, 0.5, z), 10, 0.3, 0.5, 0.3, 0.1);
                     }
                  }

                  for (Entity entity : targetLoc.getWorld().getNearbyEntities(targetLoc, 8.0, 8.0, 8.0)) {
                     if (entity instanceof LivingEntity le && entity != player) {
                         le.setHealth(Math.max(0.0, le.getHealth() - damage));
                        le.setFireTicks(200);
                        Vector knockback = le.getLocation().toVector().subtract(targetLoc.toVector()).normalize();
                        knockback.setY(0.8);
                        le.setVelocity(knockback.multiply(2.0));
                     }
                  }

                  this.cancel();
               } else {
                  for (int i = 0; i < 40; i++) {
                     double angle = Math.random() * Math.PI * 2.0;
                     double trailRadius = 2.0;
                     double trailX = Math.cos(angle) * trailRadius;
                     double trailZ = Math.sin(angle) * trailRadius;
                     Location trailLoc = this.meteorLoc.clone().add(trailX, Math.random() * 3.0, trailZ);
                     this.meteorLoc.getWorld().spawnParticle(Particle.FLAME, trailLoc, 5, 0.3, 0.3, 0.3, 0.05);
                     this.meteorLoc.getWorld().spawnParticle(Particle.LAVA, trailLoc, 2, 0.2, 0.2, 0.2, 0.0);
                  }

                  this.meteorLoc.getWorld().spawnParticle(Particle.SMOKE, this.meteorLoc.clone().add(0.0, 2.0, 0.0), 30, 1.0, 1.5, 1.0, 0.1);
                  this.meteorLoc.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, this.meteorLoc.clone().add(0.0, 3.0, 0.0), 10, 0.8, 1.0, 0.8, 0.02);
                  this.meteorLoc.getWorld().spawnParticle(Particle.FLAME, this.meteorLoc, 50, 1.5, 1.5, 1.5, 0.2);
                  this.meteorLoc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, this.meteorLoc, 20, 1.0, 1.0, 1.0, 0.15);
                  if (this.fallTicks % 3 == 0) {
                     this.meteorLoc.getWorld().playSound(this.meteorLoc, Sound.ENTITY_BLAZE_SHOOT, 1.0F, 0.3F);
                  }
               }
            }
         }).runTaskTimer(this.plugin, 60L, 1L);
      }
   }

   private void orbitingRocksAbility(final Player player, final FlamePlayer fp) {
      int cooldown = this.plugin.getConfig().getInt("essences.magma.rocks-cooldown", 120);
      int rockCount = this.plugin.getConfig().getInt("essences.magma.rock-count", 5);
      final double rockDamage = this.plugin.getConfig().getDouble("essences.magma.rock-damage", 4.0);
      fp.setCooldown("MAGMA_1", cooldown);
      player.sendMessage(ChatColor.DARK_RED + "\ud83e\udea8 " + ChatColor.GOLD + "Orbiting Rocks" + ChatColor.DARK_RED + " activated!");
      player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 2.0F, 0.5F);
      player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0F, 0.5F);
      player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.0F, 0.8F);
      this.rockCounts.put(player.getUniqueId(), rockCount);
      (new BukkitRunnable() {
            double angle = 0.0;
            int ticks = 0;

            public void run() {
               int rocks = EssenceAbilities.this.rockCounts.getOrDefault(player.getUniqueId(), 0);
               if (rocks > 0 && this.ticks < 600 && player.isOnline()) {
                  for (int i = 0; i < rocks; i++) {
                     double rockAngle = this.angle + i * 2 * Math.PI / rocks;
                     double radius = 2.5;
                     double x = Math.cos(rockAngle) * radius;
                     double z = Math.sin(rockAngle) * radius;
                     double y = 1.2 + Math.sin(this.ticks * 0.1 + i) * 0.3;
                     Location rockLoc = player.getLocation().add(x, y, z);
                     player.getWorld().spawnParticle(Particle.BLOCK, rockLoc, 15, 0.2, 0.2, 0.2, 0.0, Material.MAGMA_BLOCK.createBlockData());
                     player.getWorld().spawnParticle(Particle.FLAME, rockLoc, 5, 0.15, 0.15, 0.15, 0.02);
                     player.getWorld().spawnParticle(Particle.LAVA, rockLoc, 1, 0.1, 0.1, 0.1, 0.0);

                     for (int trail = 1; trail <= 3; trail++) {
                        double trailAngle = rockAngle - trail * 0.15;
                        double tx = Math.cos(trailAngle) * radius;
                        double tz = Math.sin(trailAngle) * radius;
                        Location trailLoc = player.getLocation().add(tx, y, tz);
                        player.getWorld().spawnParticle(Particle.SMOKE, trailLoc, 2, 0.1, 0.1, 0.1, 0.01);
                     }

                     for (Entity entity : rockLoc.getWorld().getNearbyEntities(rockLoc, 1.5, 1.5, 1.5)) {
                        if (entity instanceof Player target && entity != player) {
                            if (!fp.isTrusted(target.getUniqueId())) {
                              removeCobwebsAroundTarget(target);
                              target.setHealth(Math.max(0.0, target.getHealth() - rockDamage));
                              target.setFireTicks(60);
                              Vector knockback = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                              knockback.setY(0.5);
                              target.setVelocity(knockback.multiply(1.0));
                              target.getWorld().spawnParticle(Particle.EXPLOSION, target.getLocation(), 3, 0.3, 0.3, 0.3, 0.0);
                              target.getWorld().spawnParticle(Particle.FLAME, target.getLocation(), 30, 0.5, 0.8, 0.5, 0.15);
                              target.getWorld()
                                 .spawnParticle(Particle.BLOCK, target.getLocation(), 40, 0.5, 0.5, 0.5, 0.0, Material.MAGMA_BLOCK.createBlockData());
                              target.playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.2F);
                              EssenceAbilities.this.rockCounts.put(player.getUniqueId(), rocks - 1);
                              target.sendMessage(ChatColor.DARK_RED + "You were hit by " + player.getName() + "'s orbiting rock!");
                              break;
                           }
                        }
                     }
                  }

                  if (this.ticks % 10 == 0) {
                     player.playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 0.3F, 0.8F);
                  }

                  this.angle += 0.15;
                  this.ticks++;
               } else {
                  EssenceAbilities.this.rockCounts.remove(player.getUniqueId());
                  if (player.isOnline()) {
                     player.sendMessage(ChatColor.GRAY + "Your orbiting rocks have dissipated.");
                  }

                  this.cancel();
               }
            }
         })
         .runTaskTimer(this.plugin, 0L, 1L);
   }

    private void removeCobwebsAroundTarget(final Player target) {
        new BukkitRunnable() {
            @Override
            public void run() {
                final Location center = target.getLocation();
                final int radius = 5;
                final int radiusSquared = (int) Math.pow(radius, 2);
                final List<Block> cobwebBlocks = new ArrayList<>();

                // Collect cobweb blocks asynchronously
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            if (x*x + y*y + z*z <= radiusSquared) {
                                Block block = center.clone().add(x, y, z).getBlock();
                                if (block.getType() == Material.COBWEB) {
                                    cobwebBlocks.add(block);
                                }
                            }
                        }
                    }
                }

                // Process block changes on main thread
                if (!cobwebBlocks.isEmpty()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Block block : cobwebBlocks) {
                                block.setType(Material.AIR);
                            }
                        }
                    }.runTask(plugin);
                }
            }
        }.runTaskAsynchronously(this.plugin);
    }

   private void reactorOverloadAbility(final Player player, final FlamePlayer fp) {
      int cooldown = this.plugin.getConfig().getInt("essences.plasma.overload-cooldown", 90);
      final int chargeTime = this.plugin.getConfig().getInt("essences.plasma.overload-charge-time", 5);
      final double overloadDamage = this.plugin.getConfig().getDouble("essences.plasma.overload-damage", 10.0);
      final int overloadRadius = this.plugin.getConfig().getInt("essences.plasma.overload-radius", 5);
      double baseKnockback = this.plugin.getConfig().getInt("essences.plasma.overload-knockback", 5);
      final double overloadKnockback = baseKnockback * 0.4;
      if (this.chargingOverload.getOrDefault(player.getUniqueId(), false)) {
         player.sendMessage(ChatColor.RED + "Already charging!");
      } else {
         fp.setCooldown("PLASMA_0", cooldown);
         this.chargingOverload.put(player.getUniqueId(), true);
         player.sendMessage(ChatColor.LIGHT_PURPLE + "⚛ " + ChatColor.WHITE + "Reactor Overload" + ChatColor.LIGHT_PURPLE + " charging...");
         player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2.0F, 0.5F);
         player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.3F, 2.0F);
         (new BukkitRunnable() {
               int ticks = 0;
               final int maxTicks = chargeTime * 20;

               public void run() {
                  if (player.isOnline() && EssenceAbilities.this.chargingOverload.getOrDefault(player.getUniqueId(), false)) {
                     float progress = (float)this.ticks / this.maxTicks;
                     float pitch = 0.5F + progress * 1.5F;
                     double chargeRadius = 0.5 + progress * 2.0F;

                     for (int i = 0; i < 20; i++) {
                        double angle = i * Math.PI * 2.0 / 20.0 + this.ticks * 0.2;
                        double x = Math.cos(angle) * chargeRadius;
                        double z = Math.sin(angle) * chargeRadius;
                        double y = 1.0 + Math.sin(this.ticks * 0.3) * 0.3;
                        Location particleLoc = player.getLocation().add(x, y, z);
                        player.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 2, 0.05, 0.05, 0.05, 0.02);
                        player.getWorld().spawnParticle(Particle.DUST, particleLoc, 3, new DustOptions(Color.fromRGB(200, 100, 255), 1.0F + progress));
                     }

                     for (int i = 0; i < 8; i++) {
                        double spiralAngle = this.ticks * 0.4 + i * Math.PI / 4.0;
                        double spiralRadius = progress * 1.5;
                        double spiralX = Math.cos(spiralAngle) * spiralRadius;
                        double spiralZ = Math.sin(spiralAngle) * spiralRadius;
                        double spiralY = 0.5 + this.ticks % 20 * 0.1;
                        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation().add(spiralX, spiralY, spiralZ), 3, 0.1, 0.1, 0.1, 0.05);
                     }

                     player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation().add(0.0, 2.0, 0.0), (int)(progress * 30.0F), 1.0, 1.0, 1.0, 0.5);
                     if (this.ticks % 5 == 0) {
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 0.5F, pitch);
                     }

                     if (this.ticks < this.maxTicks) {
                        this.ticks++;
                     } else {
                        EssenceAbilities.this.chargingOverload.remove(player.getUniqueId());
                        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 0.8F);
                        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 2.0F, 1.2F);
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 2.0F, 0.5F);
                        player.playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.5F, 1.0F);
                        player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, player.getLocation().add(0.0, 1.0, 0.0), 5, 1.0, 1.0, 1.0, 0.0);
                        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0.0, 1.0, 0.0), 300, 5.0, 3.0, 5.0, 0.5);
                        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation().add(0.0, 1.0, 0.0), 200, 4.0, 3.0, 4.0, 0.8);
                        player.getWorld().spawnParticle(Particle.FLASH, player.getLocation().add(0.0, 1.0, 0.0), 10, 2.0, 2.0, 2.0, 0.0);
                        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation().add(0.0, 3.0, 0.0), 200, 5.0, 4.0, 5.0, 1.0);

                        for (int ring = 0; ring < 8; ring++) {
                           for (int i = 0; i < 40; i++) {
                              double angle = i * Math.PI * 2.0 / 40.0;
                              double ringRadius = ring + 1;
                              double x = Math.cos(angle) * ringRadius;
                              double z = Math.sin(angle) * ringRadius;
                              player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(x, 0.5, z), 3, 0.1, 0.2, 0.1, 0.02);
                              player.getWorld()
                                 .spawnParticle(Particle.DUST, player.getLocation().add(x, 0.3, z), 2, new DustOptions(Color.fromRGB(200, 100, 255), 1.5F));
                           }
                        }

                        for (Entity entity : player.getNearbyEntities(overloadRadius, overloadRadius, overloadRadius)) {
                           if (entity instanceof Player target && !fp.isTrusted(target.getUniqueId())) {
                              target.setHealth(Math.max(0.0, target.getHealth() - overloadDamage));
                              Vector knockback = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                              knockback.setY(0.8);
                              target.setVelocity(knockback.multiply(overloadKnockback / 2.0));
                              target.getWorld().spawnParticle(Particle.END_ROD, target.getLocation(), 50, 0.5, 1.0, 0.5, 0.3);
                              target.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, target.getLocation(), 30, 0.5, 1.0, 0.5, 0.5);
                              target.sendMessage(ChatColor.LIGHT_PURPLE + "⚛ You were hit by " + player.getName() + "'s reactor overload!");
                           }
                        }

                        player.sendMessage(ChatColor.LIGHT_PURPLE + "⚛ " + ChatColor.WHITE + "REACTOR OVERLOAD!");
                        this.cancel();
                     }
                  } else {
                     EssenceAbilities.this.chargingOverload.remove(player.getUniqueId());
                     this.cancel();
                  }
               }
            })
            .runTaskTimer(this.plugin, 0L, 1L);
      }
   }

   private void overheatModeAbility(final Player player, FlamePlayer fp) {
      int cooldown = this.plugin.getConfig().getInt("essences.plasma.overheat-cooldown", 60);
      final int duration = this.plugin.getConfig().getInt("essences.plasma.overheat-duration", 25);
      int strength = this.plugin.getConfig().getInt("essences.plasma.overheat-strength", 2);
      int speed = this.plugin.getConfig().getInt("essences.plasma.overheat-speed", 2);
      fp.setCooldown("PLASMA_1", cooldown);
      player.sendMessage(ChatColor.LIGHT_PURPLE + "⚛ " + ChatColor.RED + "Overheat Mode" + ChatColor.LIGHT_PURPLE + " activated!");
      player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 2.0F, 0.3F);
      player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.5F, 0.5F);
      player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.5F, 0.8F);
      player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration * 20, strength - 1));
      player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, speed - 1));
      player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration * 20, 0));
      player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0.0, 1.0, 0.0), 150, 2.0, 2.0, 2.0, 0.3);
      player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0.0, 1.0, 0.0), 100, 2.0, 2.0, 2.0, 0.2);
      player.getWorld().spawnParticle(Particle.LAVA, player.getLocation().add(0.0, 1.0, 0.0), 50, 1.5, 1.5, 1.5, 0.0);
      (new BukkitRunnable() {
         int ticks = 0;
         double auraAngle = 0.0;

         public void run() {
            if (this.ticks < duration * 20 && player.isOnline()) {
               for (int i = 0; i < 8; i++) {
                  double angle = this.auraAngle + i * Math.PI / 4.0;
                  double x = Math.cos(angle) * 1.5;
                  double z = Math.sin(angle) * 1.5;
                  double y = 0.5 + Math.sin(this.ticks * 0.2 + i) * 0.3;
                  player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(x, y, z), 3, 0.1, 0.1, 0.1, 0.02);
                  player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(x, y + 0.5, z), 1, 0.05, 0.05, 0.05, 0.01);
               }

               if (this.ticks % 5 == 0) {
                  player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0.0, 0.2, 0.0), 8, 0.3, 0.1, 0.3, 0.03);
                  player.getWorld().spawnParticle(Particle.LAVA, player.getLocation().add(0.0, 0.5, 0.0), 2, 0.2, 0.1, 0.2, 0.0);
               }

               this.auraAngle += 0.15;
               this.ticks++;
            } else {
               if (player.isOnline()) {
                  player.sendMessage(ChatColor.GRAY + "Overheat mode has ended.");
                  player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.0F);
                  player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().add(0.0, 1.0, 0.0), 50, 1.0, 1.0, 1.0, 0.1);
               }

               this.cancel();
            }
         }
      }).runTaskTimer(this.plugin, 0L, 1L);
   }

   private void blindingFlashAbility(Player player, FlamePlayer fp) {
      int cooldown = this.plugin.getConfig().getInt("essences.light.flash-cooldown", 60);
      int radius = this.plugin.getConfig().getInt("essences.light.flash-radius", 10);
      int duration = this.plugin.getConfig().getInt("essences.light.flash-duration", 5);
      fp.setCooldown("LIGHT_0", cooldown);
      player.sendMessage(ChatColor.WHITE + "✦ " + ChatColor.YELLOW + "Blinding Flash" + ChatColor.WHITE + " activated!");
      player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2.0F, 1.5F);
      player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0F, 2.0F);
      player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.5F, 2.0F);
      player.getWorld().spawnParticle(Particle.FLASH, player.getLocation().add(0.0, 1.0, 0.0), 20, 0.0, 0.0, 0.0, 0.0);
      player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0.0, 1.0, 0.0), 300, 5.0, 3.0, 5.0, 0.8);
      player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation().add(0.0, 1.0, 0.0), 150, 4.0, 3.0, 4.0, 0.5);
      player.getWorld().spawnParticle(Particle.DUST, player.getLocation().add(0.0, 1.0, 0.0), 200, new DustOptions(Color.WHITE, 2.0F));

      for (int ring = 0; ring < 10; ring++) {
         for (int i = 0; i < 40; i++) {
            double angle = i * Math.PI * 2.0 / 40.0;
            double ringRadius = ring + 1;
            double x = Math.cos(angle) * ringRadius;
            double z = Math.sin(angle) * ringRadius;
            player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(x, 0.5, z), 2, 0.1, 0.2, 0.1, 0.02);
         }
      }

      for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
         if (entity instanceof Player target && !fp.isTrusted(target.getUniqueId())) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration * 20, 0));
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration * 20, 1));
            target.playSound(target.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2.0F, 2.0F);
            target.getWorld().spawnParticle(Particle.FLASH, target.getLocation().add(0.0, 1.0, 0.0), 10, 0.5, 0.5, 0.5, 0.0);
            target.getWorld().spawnParticle(Particle.END_ROD, target.getLocation().add(0.0, 1.0, 0.0), 50, 1.0, 1.5, 1.0, 0.3);
            target.sendMessage(ChatColor.WHITE + "✦ You were blinded by " + player.getName() + "'s flash!");
         }
      }
   }

   private void lightBeamAbility(final Player player, final FlamePlayer fp) {
      int cooldown = this.plugin.getConfig().getInt("essences.light.beam-cooldown", 60);
      double baseDamage = this.plugin.getConfig().getDouble("essences.light.beam-damage", 8.0);
      final double damage = baseDamage * 1.2;
      final int range = this.plugin.getConfig().getInt("essences.light.beam-range", 30);
      fp.setCooldown("LIGHT_1", cooldown);
      player.sendMessage(ChatColor.WHITE + "✦ " + ChatColor.YELLOW + "Light Beam" + ChatColor.WHITE + " activated!");
      player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 2.0F, 0.5F);
      player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.5F, 1.5F);
      player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0F, 2.0F);
      final Vector direction = player.getLocation().getDirection().normalize();
      final Location startLoc = player.getEyeLocation();
      (new BukkitRunnable() {
         int distance = 0;
         Set<UUID> hitPlayers = new HashSet<>();

         public void run() {
            if (this.distance >= range) {
               this.cancel();
            } else {
               for (int step = 0; step < 3; step++) {
                  Location beamLoc = startLoc.clone().add(direction.clone().multiply(this.distance + step));
                  player.getWorld().spawnParticle(Particle.END_ROD, beamLoc, 15, 0.15, 0.15, 0.15, 0.05);
                  player.getWorld().spawnParticle(Particle.DUST, beamLoc, 20, new DustOptions(Color.YELLOW, 1.5F));
                  player.getWorld().spawnParticle(Particle.FIREWORK, beamLoc, 5, 0.1, 0.1, 0.1, 0.02);

                  for (int ring = 0; ring < 3; ring++) {
                     for (int i = 0; i < 8; i++) {
                        double angle = i * Math.PI / 4.0 + this.distance * 0.3;
                        double ringRadius = 0.3 + ring * 0.15;
                        Vector perX = new Vector(-direction.getZ(), 0.0, direction.getX()).normalize();
                        Vector perY = direction.clone().crossProduct(perX).normalize();
                        double x = Math.cos(angle) * ringRadius;
                        double y = Math.sin(angle) * ringRadius;
                        Location ringLoc = beamLoc.clone().add(perX.clone().multiply(x)).add(perY.clone().multiply(y));
                        player.getWorld().spawnParticle(Particle.END_ROD, ringLoc, 1, 0.0, 0.0, 0.0, 0.0);
                     }
                  }

                  for (Entity entity : beamLoc.getWorld().getNearbyEntities(beamLoc, 1.5, 1.5, 1.5)) {
                     if (entity instanceof Player target && entity != player) {
                         if (!fp.isTrusted(target.getUniqueId()) && !this.hitPlayers.contains(target.getUniqueId())) {
                           this.hitPlayers.add(target.getUniqueId());
                           target.setHealth(Math.max(0.0, target.getHealth() - damage));
                           target.setFireTicks(60);
                           target.getWorld().spawnParticle(Particle.FLASH, target.getLocation().add(0.0, 1.0, 0.0), 5, 0.3, 0.3, 0.3, 0.0);
                           target.getWorld().spawnParticle(Particle.END_ROD, target.getLocation().add(0.0, 1.0, 0.0), 80, 1.0, 1.5, 1.0, 0.5);
                           target.getWorld().spawnParticle(Particle.FIREWORK, target.getLocation().add(0.0, 1.0, 0.0), 40, 0.8, 1.0, 0.8, 0.3);
                           target.playSound(target.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.5F, 1.5F);
                           target.sendMessage(ChatColor.WHITE + "✦ You were hit by " + player.getName() + "'s light beam!");
                        }
                     }
                  }

                  if (beamLoc.getBlock().getType().isSolid()) {
                     player.getWorld().spawnParticle(Particle.END_ROD, beamLoc, 50, 0.5, 0.5, 0.5, 0.2);
                     player.getWorld().spawnParticle(Particle.FLASH, beamLoc, 5, 0.3, 0.3, 0.3, 0.0);
                     player.getWorld().playSound(beamLoc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0F, 1.0F);
                     this.cancel();
                     return;
                  }
               }

               this.distance += 3;
            }
         }
      }).runTaskTimer(this.plugin, 0L, 1L);
   }
}
