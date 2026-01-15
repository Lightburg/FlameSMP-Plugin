package net.flamesmp.plugin.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.flamesmp.plugin.FlameSMP;
import net.flamesmp.plugin.abilities.PassiveAbility;
import net.flamesmp.plugin.essences.Essence;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DataManager {
   private final FlameSMP plugin;
   private final File dataFolder;
   private final Map<UUID, FlamePlayer> players;

   public DataManager(FlameSMP plugin) {
      this.plugin = plugin;
      this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
      this.players = new HashMap<>();
      if (!this.dataFolder.exists()) {
         this.dataFolder.mkdirs();
      }
   }

   public FlamePlayer getFlamePlayer(UUID uuid) {
      if (!this.players.containsKey(uuid)) {
         this.players.put(uuid, this.loadPlayer(uuid));
      }

      return this.players.get(uuid);
   }

   public FlamePlayer loadPlayer(UUID uuid) {
      File file = new File(this.dataFolder, uuid.toString() + ".yml");
      if (!file.exists()) {
         FlamePlayer newPlayer = new FlamePlayer(uuid);
         newPlayer.setLives(this.plugin.getConfig().getInt("lives.starting-lives", 5));
         return newPlayer;
      } else {
         FileConfiguration config = YamlConfiguration.loadConfiguration(file);
         FlamePlayer player = new FlamePlayer(uuid);
         player.setLives(config.getInt("lives", 5));
         List<String> trustedStrings = config.getStringList("trusted-players");
         Set<UUID> trusted = new HashSet<>();

         for (String s : trustedStrings) {
            try {
               trusted.add(UUID.fromString(s));
            } catch (IllegalArgumentException var13) {
            }
         }

         player.setTrustedPlayers(trusted);
         if (config.contains("essence-slots")) {
            Map<Essence, Integer> slots = new HashMap<>();

            for (String key : config.getConfigurationSection("essence-slots").getKeys(false)) {
               try {
                  Essence essence = Essence.valueOf(key.toUpperCase());
                  slots.put(essence, config.getInt("essence-slots." + key));
               } catch (IllegalArgumentException var12) {
               }
            }

            player.setEssenceSlots(slots);
         }

         if (config.contains("cooldowns")) {
            Map<String, Long> cooldowns = new HashMap<>();

            for (String key : config.getConfigurationSection("cooldowns").getKeys(false)) {
               cooldowns.put(key, config.getLong("cooldowns." + key));
            }

            player.setCooldowns(cooldowns);
         }

         if (config.contains("passive-ability")) {
            try {
               PassiveAbility ability = PassiveAbility.valueOf(config.getString("passive-ability"));
               player.setPassiveAbility(ability);
            } catch (IllegalArgumentException var11) {
            }
         }

         return player;
      }
   }

   public void savePlayer(UUID uuid) {
      if (this.players.containsKey(uuid)) {
         FlamePlayer player = this.players.get(uuid);
         File file = new File(this.dataFolder, uuid.toString() + ".yml");
         FileConfiguration config = YamlConfiguration.loadConfiguration(file);
         config.set("lives", player.getLives());
         List<String> trustedStrings = new ArrayList<>();

         for (UUID trusted : player.getTrustedPlayers()) {
            trustedStrings.add(trusted.toString());
         }

         config.set("trusted-players", trustedStrings);

         for (Entry<Essence, Integer> entry : player.getEssenceSlots().entrySet()) {
            config.set("essence-slots." + entry.getKey().name().toLowerCase(), entry.getValue());
         }

         for (Entry<String, Long> entry : player.getCooldowns().entrySet()) {
            config.set("cooldowns." + entry.getKey(), entry.getValue());
         }

         if (player.getPassiveAbility() != null) {
            config.set("passive-ability", player.getPassiveAbility().name());
         }

         try {
            config.save(file);
         } catch (IOException var8) {
            this.plugin.getLogger().severe("Failed to save player data for " + uuid);
         }
      }
   }

   public void saveAllPlayers() {
      for (UUID uuid : this.players.keySet()) {
         this.savePlayer(uuid);
      }
   }

}
