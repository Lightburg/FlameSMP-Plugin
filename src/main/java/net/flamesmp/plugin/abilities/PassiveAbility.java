package net.flamesmp.plugin.abilities;

import org.bukkit.ChatColor;

public enum PassiveAbility {
   THERMAL_MOMENTUM("Thermal Momentum", ChatColor.AQUA, "After sprinting for 10 seconds, gain Speed 1 until you stop moving"),
   SCORCH_SKIN("Scorch Skin", ChatColor.GOLD, "Below 30% health, attackers take fire damage (thorns-style)"),
   LAVABORN("Lavaborn", ChatColor.RED, "Gain 2 extra hearts when on fire"),
   EMBER_RECOVERY("Ember Recovery", ChatColor.YELLOW, "Slow health regeneration in sunlight"),
   HEAT_PRESSURE("Heat Pressure", ChatColor.DARK_RED, "15 hits builds up heat for bonus damage (2 extra hearts)"),
   PYRO_ADRENALINE("Pyro Adrenaline", ChatColor.LIGHT_PURPLE, "Lower HP = faster movement speed"),
   SOLAR_CHARGED("Solar Charged", ChatColor.GOLD, "Daytime gives increased mining speed (Haste)");

   private final String displayName;
   private final ChatColor color;
   private final String description;

   PassiveAbility(String displayName, ChatColor color, String description) {
      this.displayName = displayName;
      this.color = color;
      this.description = description;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public ChatColor getColor() {
      return this.color;
   }

   public String getDescription() {
      return this.description;
   }

   public String getFormattedName() {
      return this.color + this.displayName;
   }

   public static PassiveAbility getRandomPassive() {
      PassiveAbility[] abilities = values();
      return abilities[(int)(Math.random() * abilities.length)];
   }
}
