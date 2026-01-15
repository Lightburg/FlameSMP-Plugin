package net.flamesmp.plugin.essences;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Essence {
   FIRE(
      "Fire Essence",
      ChatColor.RED,
      Material.BLAZE_POWDER,
      new String[]{"Ring of Flames", "Scorching Aura"},
      new String[]{
         "Creates a ring of particles that sends non-trusted players flying 20 blocks", "Sets non-trusted players within 30 blocks on fire for 40 seconds"
      },
      new String[]{"火", "焚"}
   ),
   LAVA(
      "Lava Essence",
      ChatColor.GOLD,
      Material.MAGMA_CREAM,
      new String[]{"Lava Pool", "Lava Wall"},
      new String[]{"Creates a block of lava below enemies for 6 seconds (25 block radius)", "Creates a 3x3 lava wall that pushes enemies back 10 blocks"},
      new String[]{"岩", "壁"}
   ),
   LIGHTNING(
      "Lightning Essence",
      ChatColor.YELLOW,
      Material.NETHER_STAR,
      new String[]{"Thunder Strike", "Acid Storm"},
      new String[]{"Strikes lightning on enemies within 25 blocks dealing 4 hearts", "Creates a 30x30 storm with acid rain dealing fire damage for 30 seconds"},
      new String[]{"雷", "雨"}
   ),
   MAGMA(
      "Magma Essence",
      ChatColor.DARK_RED,
      Material.FIRE_CHARGE,
      new String[]{"Meteor Strike", "Orbiting Rocks"},
      new String[]{"Summons a meteor on nearest enemy dealing 5 hearts (30 block range)", "Summons 5 rocks that orbit you and can be shot for 2 hearts each"},
      new String[]{"隕", "石"}
   ),
   PLASMA(
      "Plasma Essence",
      ChatColor.LIGHT_PURPLE,
      Material.END_CRYSTAL,
      new String[]{"Reactor Overload", "Overheat Mode"},
      new String[]{"Charge for 5 seconds then explode dealing 5 hearts in 5 block radius", "Gain Strength 2 and Speed 2 for 25 seconds"},
      new String[]{"爆", "熱"}
   ),
   LIGHT(
      "Light Essence",
      ChatColor.WHITE,
      Material.GLOWSTONE_DUST,
      new String[]{"Blinding Flash", "Light Beam"},
      new String[]{"Blinds all enemies within 10 blocks for 5 seconds", "Fires a beam of light dealing 4 hearts"},
      new String[]{"閃", "光"}
   );

   private final String displayName;
   private final ChatColor color;
   private final Material material;
   private final String[] abilityNames;
   private final String[] abilityDescriptions;
   private final String[] abilityIcons;

   private Essence(String displayName, ChatColor color, Material material, String[] abilityNames, String[] abilityDescriptions, String[] abilityIcons) {
      this.displayName = displayName;
      this.color = color;
      this.material = material;
      this.abilityNames = abilityNames;
      this.abilityDescriptions = abilityDescriptions;
      this.abilityIcons = abilityIcons;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public ChatColor getColor() {
      return this.color;
   }

   public Material getMaterial() {
      return this.material;
   }

   public String[] getAbilityNames() {
      return this.abilityNames;
   }

   public String[] getAbilityDescriptions() {
      return this.abilityDescriptions;
   }

   public String[] getAbilityIcons() {
      return this.abilityIcons;
   }

   public String getAbilityIcon(int slot) {
      return slot >= 0 && slot < this.abilityIcons.length ? this.abilityIcons[slot] : "?";
   }

   public String getFormattedName() {
      return this.color + this.displayName;
   }
}
