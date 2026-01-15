package net.flamesmp.plugin.items;

import java.util.ArrayList;
import java.util.List;
import net.flamesmp.plugin.FlameSMP;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class FlameItems {
   private final FlameSMP plugin;
   private final NamespacedKey heatShardKey;
   private final NamespacedKey lifeItemKey;
   private final NamespacedKey reviveItemKey;
   private final NamespacedKey passiveRerollerItemKey;
   private final NamespacedKey solarSwordItemKey;

   public FlameItems(FlameSMP plugin) {
      this.plugin = plugin;
      this.heatShardKey = new NamespacedKey(plugin, "heat_shard");
      this.lifeItemKey = new NamespacedKey(plugin, "life_item");
      this.reviveItemKey = new NamespacedKey(plugin, "revive_item");
      this.passiveRerollerItemKey = new NamespacedKey(plugin, "passive_reroller_item");
      this.solarSwordItemKey = new NamespacedKey(plugin, "solar_sword_item");
   }

   public ItemStack createHeatShard() {
      ItemStack item = new ItemStack(Material.NETHER_STAR);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(ChatColor.RED + "✦ " + ChatColor.GOLD + "Heat Shard" + ChatColor.RED + " ✦");
      List<String> lore = new ArrayList<>();
      lore.add("");
      lore.add(ChatColor.GRAY + "A fragment of pure heat energy");
      lore.add(ChatColor.GRAY + "dropped by fallen warriors.");
      lore.add("");
      lore.add(ChatColor.YELLOW + "Used in crafting Essences");
      meta.setLore(lore);
      meta.addEnchant(Enchantment.UNBREAKING, 1, true);
      meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      meta.getPersistentDataContainer().set(this.heatShardKey, PersistentDataType.BOOLEAN, true);
      item.setItemMeta(meta);
      return item;
   }

    public ItemStack createLifeItem() {
      ItemStack item = new ItemStack(Material.HEART_OF_THE_SEA);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(ChatColor.RED + "♥ " + ChatColor.LIGHT_PURPLE + "Crystallized Life" + ChatColor.RED + " ♥");
      List<String> lore = new ArrayList<>();
      lore.add("");
      lore.add(ChatColor.GRAY + "A crystallized fragment of life force.");
      lore.add("");
      lore.add(ChatColor.GREEN + "Right-click to absorb");
      lore.add(ChatColor.GRAY + "and gain " + ChatColor.RED + "+1 Life");
      meta.setLore(lore);
      meta.addEnchant(Enchantment.UNBREAKING, 1, true);
      meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      meta.getPersistentDataContainer().set(this.lifeItemKey, PersistentDataType.BOOLEAN, true);
      item.setItemMeta(meta);
      return item;
   }

   public boolean isLifeItem(ItemStack item) {
      return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(this.lifeItemKey, PersistentDataType.BOOLEAN);
   }

   public ItemStack createReviveItem() {
      ItemStack item = new ItemStack(Material.FEATHER);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(ChatColor.GOLD + "✦ " + ChatColor.GREEN + "Phoenix Feather" + ChatColor.GOLD + " ✦");
      List<String> lore = new ArrayList<>();
      lore.add("");
      lore.add(ChatColor.GRAY + "A mystical feather with the power");
      lore.add(ChatColor.GRAY + "to revive fallen warriors.");
      lore.add("");
      lore.add(ChatColor.GREEN + "Right-click to open Revival Menu");
      lore.add(ChatColor.GRAY + "Revives a banned player with " + ChatColor.RED + "3 Lives");
      meta.setLore(lore);
      meta.addEnchant(Enchantment.UNBREAKING, 1, true);
      meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      meta.getPersistentDataContainer().set(this.reviveItemKey, PersistentDataType.BOOLEAN, true);
      item.setItemMeta(meta);
      return item;
   }

   public boolean isReviveItem(ItemStack item) {
      return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(this.reviveItemKey, PersistentDataType.BOOLEAN);
   }

    public ItemStack createPassiveRerollerItem() {
        ItemStack item = new ItemStack(Material.ECHO_SHARD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Passive Reroller");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "A mysterious item that reshuffles");
        lore.add(ChatColor.GRAY + "your powers and abilities.");
        lore.add("");
        lore.add(ChatColor.AQUA + "RIGHT CLICK: Rerolls your passive");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.getPersistentDataContainer().set(this.passiveRerollerItemKey, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isSolarSwordItem(ItemStack item) {
        return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(this.solarSwordItemKey, PersistentDataType.BOOLEAN);
    }

    public ItemStack createSolarSwordItem() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Solar Sword");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "BUFF: "
                + ChatColor.RESET + ChatColor.WHITE + "The blade is enchanted with Fire Aspect III");
        lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "BUFF: "
                + ChatColor.RESET + ChatColor.WHITE + "Every 30 hits to a player, deal 4 hearts of " + ChatColor.RED + "pure "  + ChatColor.WHITE + "damage");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.UNBREAKING, 3, true);
        meta.addEnchant(Enchantment.SHARPNESS, 5, true);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addEnchant(Enchantment.SWEEPING_EDGE, 3, true);
        meta.addEnchant(Enchantment.LOOTING, 3, true);
        meta.addEnchant(Enchantment.FIRE_ASPECT, 3, true);
        meta.getPersistentDataContainer().set(this.solarSwordItemKey, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isPassiveRerollerItem(ItemStack item) {
        return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(this.passiveRerollerItemKey, PersistentDataType.BOOLEAN);
    }

   public void registerRecipes() {
      this.registerReviveItemRecipe();
      this.registerPassiveRerollerItemRecipe();
      this.registerLifeItemRecipe();
   }

   private void registerReviveItemRecipe() {
      ItemStack result = this.createReviveItem();
      NamespacedKey key = new NamespacedKey(this.plugin, "revive_item_recipe");
      ShapedRecipe recipe = new ShapedRecipe(key, result);
      recipe.shape("GHG", "HNH", "GHG");
      recipe.setIngredient('G', Material.GOLD_BLOCK);
      recipe.setIngredient('H', new RecipeChoice.ExactChoice(createHeatShard()));
      recipe.setIngredient('N', Material.NETHERITE_BLOCK);
      this.plugin.getServer().addRecipe(recipe);
   }

   public void registerPassiveRerollerItemRecipe(){
       ItemStack result = this.createPassiveRerollerItem();
       NamespacedKey key = new NamespacedKey(this.plugin, "passive_reroller_item_recipe");
       ShapedRecipe recipe = new ShapedRecipe(key, result);
       recipe.shape("NNN", "DUD", "NCN");
       recipe.setIngredient('N', Material.NETHERITE_INGOT);
       recipe.setIngredient('C', Material.CRYING_OBSIDIAN);
       recipe.setIngredient('D', Material.DIAMOND_BLOCK);
       recipe.setIngredient('U', Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
       plugin.getServer().addRecipe(recipe);
   }

    public void registerLifeItemRecipe(){
        ItemStack result = this.createLifeItem();
        NamespacedKey key = new NamespacedKey(this.plugin, "life_item_recipe");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("DGB", "HNH", "BGD");
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('G', Material.GOLDEN_APPLE);
        recipe.setIngredient('B', Material.BLAZE_ROD);
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('H', new RecipeChoice.ExactChoice(createHeatShard()));
        plugin.getServer().addRecipe(recipe);
    }

}
