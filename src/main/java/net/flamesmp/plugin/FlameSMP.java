package net.flamesmp.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.flamesmp.plugin.commands.FlameAdminCommand;
import net.flamesmp.plugin.commands.FlameCommand;
import net.flamesmp.plugin.commands.WithdrawCommand;
import net.flamesmp.plugin.data.DataManager;
import net.flamesmp.plugin.essences.Essence;
import net.flamesmp.plugin.essences.EssenceAbilities;
import net.flamesmp.plugin.essences.EssenceManager;
import net.flamesmp.plugin.items.FlameItems;
import net.flamesmp.plugin.listeners.CombatListener;
import net.flamesmp.plugin.listeners.EssenceListener;
import net.flamesmp.plugin.listeners.ItemListener;
import net.flamesmp.plugin.listeners.PassiveAbilityListener;
import net.flamesmp.plugin.listeners.PassiveAbilityManager;
import net.flamesmp.plugin.listeners.PlayerDeathListener;
import net.flamesmp.plugin.listeners.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FlameSMP extends JavaPlugin {
   private static FlameSMP instance;
   private DataManager dataManager;
   private EssenceManager essenceManager;
   private EssenceAbilities essenceAbilities;
   private PassiveAbilityManager passiveAbilityManager;
   private FlameItems flameItems;
   private Map<String, Integer> essenceCounts;

   public void onEnable() {
      instance = this;
      this.saveDefaultConfig();
      this.dataManager = new DataManager(this);
      this.essenceManager = new EssenceManager(this);
      this.essenceAbilities = new EssenceAbilities(this);
      this.flameItems = new FlameItems(this);
      this.passiveAbilityManager = new PassiveAbilityManager(this);
      this.essenceCounts = new HashMap<>();
      this.updateEssenceCounts();
      this.registerListeners();
      this.registerCommands();
      this.essenceManager.registerRecipes();
      this.flameItems.registerRecipes();
      this.getLogger().info("FlameSMP Plugin enabled! Fire burns bright!");
   }

   public void onDisable() {
      if (this.dataManager != null) {
         this.dataManager.saveAllPlayers();
      }
      saveEssenceCounts();

      this.getLogger().info("FlameSMP Plugin disabled. The flames rest...");
   }

   private void registerListeners() {
      Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
      Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(this), this);
      Bukkit.getPluginManager().registerEvents(new PassiveAbilityListener(this), this);
      Bukkit.getPluginManager().registerEvents(new EssenceListener(this), this);
      Bukkit.getPluginManager().registerEvents(new CombatListener(this), this);
      Bukkit.getPluginManager().registerEvents(new ItemListener(this), this);
   }

   private void registerCommands() {
      this.getCommand("flame").setExecutor(new FlameCommand(this));
      this.getCommand("flameadmin").setExecutor(new FlameAdminCommand(this));
      this.getCommand("withdraw").setExecutor(new WithdrawCommand(this));
   }

   public static FlameSMP getInstance() {
      return instance;
   }

   public DataManager getDataManager() {
      return this.dataManager;
   }

   private void updateEssenceCounts(){
       essenceCounts.put(Essence.FIRE.name(), this.getConfig().getInt("essences.fire-count"));
       essenceCounts.put(Essence.LAVA.name(), this.getConfig().getInt("essences.lava-count"));
       essenceCounts.put(Essence.LIGHTNING.name(), this.getConfig().getInt("essences.lightning-count"));
       essenceCounts.put(Essence.MAGMA.name(), this.getConfig().getInt("essences.magma-count"));
       essenceCounts.put(Essence.PLASMA.name(), this.getConfig().getInt("essences.plasma-count"));
       essenceCounts.put(Essence.LIGHT.name(), this.getConfig().getInt("essences.light-count"));
   }

   private void saveEssenceCounts(){
       this.getConfig().set("essences.fire-count", essenceCounts.getOrDefault(Essence.FIRE.name(), 0));
       this.getConfig().set("essences.lava-count", essenceCounts.getOrDefault(Essence.LAVA.name(), 0));
       this.getConfig().set("essences.lightning-count", essenceCounts.getOrDefault(Essence.LIGHTNING.name(), 0));
       this.getConfig().set("essences.magma-count", essenceCounts.getOrDefault(Essence.MAGMA.name(), 0));
       this.getConfig().set("essences.plasma-count", essenceCounts.getOrDefault(Essence.PLASMA.name(), 0));
       this.getConfig().set("essences.light-count", essenceCounts.getOrDefault(Essence.LIGHT.name(), 0));
       this.saveConfig();
   }

   public EssenceManager getEssenceManager() {
      return this.essenceManager;
   }

   public EssenceAbilities getEssenceAbilities() {
      return this.essenceAbilities;
   }

   public PassiveAbilityManager getPassiveAbilityManager() {
      return this.passiveAbilityManager;
   }

   public FlameItems getFlameItems() {
      return this.flameItems;
   }

   public int getEssenceCount(String essenceType) {
      return this.essenceCounts.getOrDefault(essenceType, 0);
   }

   public void incrementEssenceCount(String essenceType) {
      this.essenceCounts.put(essenceType, this.getEssenceCount(essenceType) + 1);
      saveEssenceCounts();
   }

   public void decrementEssenceCount(String essenceType) {
      int count = this.getEssenceCount(essenceType);
      if (count > 0) {
         this.essenceCounts.put(essenceType, count - 1);
      }
      saveEssenceCounts();
   }

   public boolean canCraftEssence(String essenceType) {
      int maxPerType = this.getConfig().getInt("essences.max-per-type", 2);
      return this.getEssenceCount(essenceType) >= maxPerType;
   }
}
