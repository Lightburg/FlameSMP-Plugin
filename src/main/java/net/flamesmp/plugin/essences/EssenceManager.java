package net.flamesmp.plugin.essences;

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
import org.jetbrains.annotations.NotNull;

public class EssenceManager {
    private final FlameSMP plugin;
    private final NamespacedKey essenceKey;

    public EssenceManager(FlameSMP plugin) {
        this.plugin = plugin;
        this.essenceKey = new NamespacedKey(plugin, "essence_type");
    }

    public ItemStack createEssenceItem(Essence essence) {
        ItemStack item = new ItemStack(essence.getMaterial());
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(essence.getFormattedName());
        List<String> lore = getStrings(essence);
        meta.setLore(lore);
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.getPersistentDataContainer().set(this.essenceKey, PersistentDataType.STRING, essence.name());
        item.setItemMeta(meta);
        return item;
    }

    private static @NotNull List<String> getStrings(Essence essence) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Right-click to activate ability");
        lore.add(ChatColor.GRAY + "Shift + Right-click to switch ability");
        lore.add("");
        lore.add(ChatColor.GOLD + "✦ Abilities ✦");

        for (int i = 0; i < essence.getAbilityNames().length; i++) {
            String icon = essence.getAbilityIcon(i);
            lore.add(ChatColor.YELLOW + "  " + icon + " " + ChatColor.WHITE + essence.getAbilityNames()[i]);
            lore.add(ChatColor.GRAY + "     " + essence.getAbilityDescriptions()[i]);
        }

        lore.add("");
        lore.add(ChatColor.RED + "⚠ Only 2 of each essence can exist!");
        return lore;
    }

    public ItemStack createHeatShard() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
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
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "heat_shard"), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public Essence getEssenceFromItem(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            if (!meta.getPersistentDataContainer().has(this.essenceKey, PersistentDataType.STRING)) {
                return null;
            } else {
                String essenceName = meta.getPersistentDataContainer().get(this.essenceKey, PersistentDataType.STRING);

                try {
                    return Essence.valueOf(essenceName);
                } catch (IllegalArgumentException var5) {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public String getEssenceTypeFromItem(ItemStack item) {
        Essence essence = this.getEssenceFromItem(item);
        return essence != null ? essence.name() : null;
    }

    public void registerRecipes() {
        this.registerEssenceRecipe(Essence.FIRE);
        this.registerEssenceRecipe(Essence.LAVA);
        this.registerEssenceRecipe(Essence.LIGHTNING);
        this.registerEssenceRecipe(Essence.MAGMA);
        this.registerEssenceRecipe(Essence.PLASMA);
        this.registerEssenceRecipe(Essence.LIGHT);
    }

    private void registerEssenceRecipe(Essence essence) {
        try {
            switch (essence) {
                case FIRE:
                    this.registerFireEssenceRecipe();
                    break;
                case LAVA:
                    this.registerLavaEssenceRecipe();
                    break;
                case LIGHTNING:
                    this.registerLightningEssenceRecipe();
                    break;
                case MAGMA:
                    this.registerMagmaEssenceRecipe();
                    break;
                case PLASMA:
                    this.registerPlasmaEssenceRecipe();
                    break;
                case LIGHT:
                    this.registerLightEssenceRecipe();
                    break;
                default:
                    plugin.getLogger().warning("Unknown essence type: " + essence.name());
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register recipe for " + essence.name() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void registerFireEssenceRecipe() {
        ItemStack result = this.createEssenceItem(Essence.FIRE);
        NamespacedKey key = new NamespacedKey(this.plugin, "fire_essence");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("WHW", "HNH", "WHW");
        recipe.setIngredient('W', Material.WITHER_SKELETON_SKULL);
        recipe.setIngredient('H', new RecipeChoice.ExactChoice(createHeatShard()));
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        this.plugin.getServer().addRecipe(recipe);
    }

    private void registerLavaEssenceRecipe() {
        ItemStack result = this.createEssenceItem(Essence.LAVA);
        NamespacedKey key = new NamespacedKey(this.plugin, "lava_essence");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("HLH", "LML", "HLH");
        recipe.setIngredient('M', Material.MAGMA_CREAM);
        recipe.setIngredient('H', new RecipeChoice.ExactChoice(createHeatShard()));
        recipe.setIngredient('L', Material.LAVA_BUCKET);
        this.plugin.getServer().addRecipe(recipe);
    }

    private void registerLightningEssenceRecipe() {
        ItemStack result = this.createEssenceItem(Essence.LIGHTNING);
        NamespacedKey key = new NamespacedKey(this.plugin, "lightning_essence");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("CTC", "HSH", "CHC");
        recipe.setIngredient('C', Material.COPPER_BLOCK);
        recipe.setIngredient('T', Material.TRIDENT);
        recipe.setIngredient('H', new RecipeChoice.ExactChoice(createHeatShard()));
        recipe.setIngredient('S', Material.CREEPER_HEAD);
        this.plugin.getServer().addRecipe(recipe);
    }

    private void registerMagmaEssenceRecipe() {
        ItemStack result = this.createEssenceItem(Essence.MAGMA);
        NamespacedKey key = new NamespacedKey(this.plugin, "magma_essence");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("FHM", "HSH", "MHF");
        recipe.setIngredient('F', Material.FIRE_CHARGE);
        recipe.setIngredient('H', new RecipeChoice.ExactChoice(createHeatShard()));
        recipe.setIngredient('M', Material.MAGMA_BLOCK);
        recipe.setIngredient('S', Material.WITHER_SKELETON_SKULL);
        this.plugin.getServer().addRecipe(recipe);
    }

    private void registerPlasmaEssenceRecipe() {
        ItemStack result = this.createEssenceItem(Essence.PLASMA);
        NamespacedKey key = new NamespacedKey(this.plugin, "plasma_essence");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("HEH", "ENE", "HEH");
        recipe.setIngredient('E', Material.END_CRYSTAL);
        recipe.setIngredient('H', new RecipeChoice.ExactChoice(createHeatShard()));
        recipe.setIngredient('N', Material.NETHER_STAR);
        this.plugin.getServer().addRecipe(recipe);
    }

    private void registerLightEssenceRecipe() {
        ItemStack result = this.createEssenceItem(Essence.LIGHT);
        NamespacedKey key = new NamespacedKey(this.plugin, "light_essence");
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("GHG", "HBH", "GHG");
        recipe.setIngredient('G', Material.GLOWSTONE);
        recipe.setIngredient('H', new RecipeChoice.ExactChoice(createHeatShard()));
        recipe.setIngredient('B', Material.BEACON);
        this.plugin.getServer().addRecipe(recipe);
    }

}