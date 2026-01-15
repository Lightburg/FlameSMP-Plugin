package net.flamesmp.plugin.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.flamesmp.plugin.abilities.PassiveAbility;
import net.flamesmp.plugin.essences.Essence;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FlamePlayer {
   private final UUID uuid;
   private int lives;
   private Set<UUID> trustedPlayers;
   private Map<Essence, Integer> essenceAbilitySlot;
   private Map<String, Long> cooldowns;
   private int hitCounter;
   private long sprintStartTime;
   private boolean isSprinting;
    private PassiveAbility passiveAbility;

   public FlamePlayer(UUID uuid) {
      this.uuid = uuid;
      this.lives = 5;
      this.trustedPlayers = new HashSet<>();
      this.essenceAbilitySlot = new HashMap<>();
      this.cooldowns = new HashMap<>();
      this.hitCounter = 0;
      this.sprintStartTime = 0L;
      this.isSprinting = false;
      this.passiveAbility = null;
   }

   public Player getPlayer() {
      return Bukkit.getPlayer(this.uuid);
   }

   public int getLives() {
      return this.lives;
   }

   public void setLives(int lives) {
      this.lives = Math.max(0, Math.min(lives, 10));
   }

   public void addLife() {
      this.setLives(this.lives + 1);
   }

   public void removeLife() {
      this.setLives(this.lives - 1);
   }

   public Set<UUID> getTrustedPlayers() {
      return this.trustedPlayers;
   }

   public void addTrustedPlayer(UUID uuid) {
      this.trustedPlayers.add(uuid);
   }

   public void removeTrustedPlayer(UUID uuid) {
      this.trustedPlayers.remove(uuid);
   }

   public boolean isTrusted(UUID uuid) {
      return this.trustedPlayers.contains(uuid) || uuid.equals(this.uuid);
   }

   public int getHitCounter() {
      return this.hitCounter;
   }

   public void incrementHitCounter() {
      this.hitCounter++;
      if (this.hitCounter > 15) {
         this.hitCounter = 15;
      }
   }

   public void decrementHitCounter() {
      this.hitCounter--;
      if (this.hitCounter < 0) {
         this.hitCounter = 0;
      }
   }

   public void resetHitCounter() {
      this.hitCounter = 0;
   }

   public boolean isHeatMaxed() {
      return this.hitCounter >= 15;
   }

   public long getSprintStartTime() {
      return this.sprintStartTime;
   }

    public boolean isSprinting() {
      return this.isSprinting;
   }

   public void setSprinting(boolean sprinting) {
      this.isSprinting = sprinting;
      if (sprinting && this.sprintStartTime == 0L) {
         this.sprintStartTime = System.currentTimeMillis();
      } else if (!sprinting) {
         this.sprintStartTime = 0L;
      }
   }

    public int getEssenceAbilitySlot(Essence essence) {
      return this.essenceAbilitySlot.getOrDefault(essence, 0);
   }

   public void setEssenceAbilitySlot(Essence essence, int slot) {
      this.essenceAbilitySlot.put(essence, slot);
   }

   public void toggleEssenceAbility(Essence essence) {
      int current = this.getEssenceAbilitySlot(essence);
      this.setEssenceAbilitySlot(essence, current == 0 ? 1 : 0);
   }

   public boolean isOnCooldown(String ability) {
      return this.cooldowns.containsKey(ability) && System.currentTimeMillis() < this.cooldowns.get(ability);
   }

   public long getCooldownRemaining(String ability) {
      if (!this.cooldowns.containsKey(ability)) {
         return 0L;
      } else {
         long remaining = this.cooldowns.get(ability) - System.currentTimeMillis();
         return Math.max(0L, remaining / 1000L);
      }
   }

   public void setCooldown(String ability, long durationSeconds) {
      this.cooldowns.put(ability, System.currentTimeMillis() + durationSeconds * 1000L);
   }

   public void clearCooldown(String ability) {
      this.cooldowns.remove(ability);
   }

   public Map<String, Long> getCooldowns() {
      return this.cooldowns;
   }

   public void setCooldowns(Map<String, Long> cooldowns) {
      this.cooldowns = cooldowns;
   }

   public Map<Essence, Integer> getEssenceSlots() {
      return this.essenceAbilitySlot;
   }

   public void setEssenceSlots(Map<Essence, Integer> slots) {
      this.essenceAbilitySlot = slots;
   }

   public void setTrustedPlayers(Set<UUID> trusted) {
      this.trustedPlayers = trusted;
   }

   public PassiveAbility getPassiveAbility() {
      return this.passiveAbility;
   }

   public void setPassiveAbility(PassiveAbility ability) {
      this.passiveAbility = ability;
   }

   public boolean hasPassiveAbility() {
      return this.passiveAbility != null;
   }

   public void assignRandomPassive() {
       this.passiveAbility = PassiveAbility.getRandomPassive();
   }
}
