package net.flamesmp.plugin.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.flamesmp.plugin.FlameSMP;
import net.flamesmp.plugin.abilities.PassiveAbility;
import net.flamesmp.plugin.data.FlamePlayer;
import net.flamesmp.plugin.essences.Essence;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.BanList.Type;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FlameAdminCommand implements CommandExecutor, TabCompleter {
    private final FlameSMP plugin;

    public FlameAdminCommand(FlameSMP plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command zzcommand, String label, String[] args) {
        if (!sender.hasPermission("flamesmp.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        } else if (args.length == 0) {
            this.showHelp(sender);
            return true;
        } else {
            String var5 = args[0].toLowerCase();
            switch (var5) {
                case "help":
                    this.showHelp(sender);
                    break;
                case "setlives":
                    if (args.length < 3) {
                        sender.sendMessage(ChatColor.RED + "Usage: /flameadmin setlives <player> <amount>");
                        return true;
                    }

                    this.setLives(sender, args[1], args[2]);
                    break;
                case "deathstats":
                    this.invinciblePlayers(sender);
                    break;
                case "addlives":
                    if (args.length < 3) {
                        sender.sendMessage(ChatColor.RED + "Usage: /flameadmin addlives <player> <amount>");
                        return true;
                    }

                    this.addLives(sender, args[1], args[2]);
                    break;
                case "setpassive":
                    if (args.length < 3) {
                        sender.sendMessage(ChatColor.RED + "Usage: /flameadmin setpassive <player> <passive>");
                        return true;
                    }

                    this.setPassive(sender, args[1], args[2]);
                    break;
                case "giveessence":
                    if (args.length < 3) {
                        sender.sendMessage(ChatColor.RED + "Usage: /flameadmin giveessence <player> <essence>");
                        return true;
                    }

                    this.giveEssence(sender, args[1], args[2]);
                    break;
                case "giveshard":
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /flameadmin giveshard <player> [amount]");
                        return true;
                    }

                    this.giveHeatShard(sender, args[1], args.length > 2 ? args[2] : "1");
                    break;
                case "givereviveitem":
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /flameadmin givereviveitem <player>");
                        return true;
                    }
                    this.giveReviveItem(sender, args[1], args.length > 2 ? args[2] : "1");
                    break;
                case "givesolarsword":
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /flameadmin givesolarsword <player>");
                        return true;
                    }
                    this.giveSolarSword(sender, args[1]);
                    break;
                case "unban":
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /flameadmin unban <player>");
                        return true;
                    }

                    this.unbanPlayer(sender, args[1]);
                    break;
                case "revive":
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /flameadmin revive <player>");
                        return true;
                    }

                    this.revivePlayer(sender, args[1]);
                    break;
                case "info":
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /flameadmin info <player>");
                        return true;
                    }

                    this.showPlayerInfo(sender, args[1]);
                    break;
                case "resetessence":
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /flameadmin resetessence <essence>");
                        return true;
                    }

                    this.resetEssenceCount(sender, args[1]);
                    break;
                case "reload":
                    this.reloadConfig(sender);
                    break;
                case "clearcooldown":
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /flameadmin clearcooldown <player> [ability]");
                        return true;
                    }

                    this.clearCooldown(sender, args[1], args.length > 2 ? args[2] : null);
                    break;
                case "useability":
                    if (args.length < 3) {
                        sender.sendMessage(ChatColor.RED + "Usage: /flameadmin useability <player> <essence> [slot]");
                        return true;
                    }

                    this.useAbility(sender, args[1], args[2], args.length > 3 ? args[3] : "0");
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "Unknown command. Use /flameadmin help for a list of commands.");
            }

            return true;
        }
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
        sender.sendMessage(ChatColor.RED + "  \ud83d\udd25 " + ChatColor.GOLD + "FlameSMP Admin Commands" + ChatColor.RED + " \ud83d\udd25");
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin setlives <player> <amount>");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin addlives <player> <amount>");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin setflame <player> <level>");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin setheat <player> <amount>");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin setpassive <player> <passive>");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin giveessence <player> <essence>");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin giveshard <player> [amount]");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin givesolarsword <player>");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin givereviveitem <player> [amount]");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin resetessence <essence>");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin unban <player>");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin revive <player>");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin info <player>");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin deathstats");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin clearcooldown <player> [ability]");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin useability <player> <essence> [slot]");
        sender.sendMessage(ChatColor.YELLOW + "/flameadmin reload");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
    }

    private void setLives(CommandSender sender, String playerName, String amountStr) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
        } else {
            try {
                int amount = Integer.parseInt(amountStr);
                FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(target.getUniqueId());
                fp.setLives(amount);
                this.plugin.getDataManager().savePlayer(target.getUniqueId());
                sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s lives to " + amount);
                target.sendMessage(ChatColor.YELLOW + "Your lives have been set to " + amount + " by an admin.");
            } catch (NumberFormatException var7) {
                sender.sendMessage(ChatColor.RED + "Invalid number!");
            }
        }
    }

    private void invinciblePlayers(CommandSender p){
        String s = "";
        for(OfflinePlayer op : Bukkit.getOfflinePlayers()){
            if(op.getStatistic(Statistic.DEATHS) == 0){
                s += ChatColor.YELLOW + op.getName() + "\n";
            }
        }
        if(s.length() < 3){
            p.sendMessage(ChatColor.YELLOW + "There are no invincible players");
        }
    }

    private void addLives(CommandSender sender, String playerName, String amountStr) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
        } else {
            try {
                int amount = Integer.parseInt(amountStr);
                FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(target.getUniqueId());
                fp.setLives(fp.getLives() + amount);
                this.plugin.getDataManager().savePlayer(target.getUniqueId());
                sender.sendMessage(ChatColor.GREEN + "Added " + amount + " lives to " + target.getName());
                target.sendMessage(ChatColor.YELLOW + "You received " + amount + " lives from an admin.");
            } catch (NumberFormatException var7) {
                sender.sendMessage(ChatColor.RED + "Invalid number!");
            }
        }
    }

    private void setPassive(CommandSender sender, String playerName, String passiveName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
        } else {
            try {
                PassiveAbility passive = PassiveAbility.valueOf(passiveName.toUpperCase());
                FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(target.getUniqueId());
                fp.setPassiveAbility(passive);
                this.plugin.getDataManager().savePlayer(target.getUniqueId());
                sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s passive to " + passive.getFormattedName());
                target.sendMessage(ChatColor.YELLOW + "Your passive ability has been changed to " + passive.getFormattedName() + ChatColor.YELLOW + " by an admin.");
            } catch (IllegalArgumentException var7) {
                sender.sendMessage(ChatColor.RED + "Invalid passive! Options: THERMAL_MOMENTUM, SCORCH_SKIN, LAVABORN, EMBER_RECOVERY, HEAT_PRESSURE, PYRO_ADRENALINE, SOLAR_CHARGED");
            }
        }
    }

    private void giveEssence(CommandSender sender, String playerName, String essenceName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
        } else {
            try {
                Essence essence = Essence.valueOf(essenceName.toUpperCase());
                target.getInventory().addItem(new ItemStack[]{this.plugin.getEssenceManager().createEssenceItem(essence)});
                this.plugin.incrementEssenceCount(essence.name());
                sender.sendMessage(ChatColor.GREEN + "Gave " + essence.getDisplayName() + " to " + target.getName());
                target.sendMessage(ChatColor.YELLOW + "You received " + essence.getFormattedName() + ChatColor.YELLOW + " from an admin.");
            } catch (IllegalArgumentException var6) {
                sender.sendMessage(ChatColor.RED + "Invalid essence! Options: FIRE, LAVA, LIGHTNING, MAGMA, PLASMA, LIGHT");
            }
        }
    }

    private void giveHeatShard(CommandSender sender, String playerName, String amountStr) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
        } else {
            try {
                int amount = Integer.parseInt(amountStr);

                for (int i = 0; i < amount; i++) {
                    target.getInventory().addItem(new ItemStack[]{this.plugin.getFlameItems().createHeatShard()});
                }

                sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " Heat Shard(s) to " + target.getName());
                target.sendMessage(ChatColor.YELLOW + "You received " + amount + " Heat Shard(s) from an admin.");
            } catch (NumberFormatException var7) {
                sender.sendMessage(ChatColor.RED + "Invalid number!");
            }
        }
    }

    private void giveReviveItem(CommandSender sender, String playerName, String amountStr) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
        } else {
            try {
                int amount = Integer.parseInt(amountStr);

                for (int i = 0; i < amount; i++) {
                    target.getInventory().addItem(this.plugin.getFlameItems().createReviveItem());
                }

                sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " Phoenix Feather(s) to " + target.getName());
                target.sendMessage(ChatColor.YELLOW + "You received " + amount + " Phoenix Feather(s) from an admin.");
            } catch (NumberFormatException var7) {
                sender.sendMessage(ChatColor.RED + "Invalid number!");
            }
        }
    }

    private void giveSolarSword(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
        } else {
            target.getInventory().addItem(this.plugin.getFlameItems().createSolarSwordItem());
            target.sendMessage(ChatColor.YELLOW + "You received a Solar Sword from an admin.");
        }
    }

    private void resetEssenceCount(CommandSender sender, String essenceName) {
        try {
            Essence essence = Essence.valueOf(essenceName.toUpperCase());
            int currentCount = this.plugin.getEssenceCount(essence.name());

            for (int i = 0; i < currentCount; i++) {
                this.plugin.decrementEssenceCount(essence.name());
            }

            sender.sendMessage(ChatColor.GREEN + "Reset " + essence.getDisplayName() + " count to 0.");
        } catch (IllegalArgumentException var10) {
            if (essenceName.equalsIgnoreCase("all")) {
                for (Essence essence : Essence.values()) {
                    int currentCount = this.plugin.getEssenceCount(essence.name());

                    for (int i = 0; i < currentCount; i++) {
                        this.plugin.decrementEssenceCount(essence.name());
                    }
                }

                sender.sendMessage(ChatColor.GREEN + "Reset all essence counts to 0.");
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid essence! Options: FIRE, LAVA, LIGHTNING, MAGMA, PLASMA, LIGHT, ALL");
            }
        }
    }

    private void unbanPlayer(CommandSender sender, String playerName) {
        Bukkit.getBanList(Type.NAME).pardon(playerName);
        sender.sendMessage(ChatColor.GREEN + "Unbanned " + playerName);
    }

    private void revivePlayer(CommandSender sender, String playerName) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        Bukkit.getBanList(Type.NAME).pardon(playerName);
        FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(target.getUniqueId());
        fp.setLives(this.plugin.getConfig().getInt("lives.starting-lives", 5));
        this.plugin.getDataManager().savePlayer(target.getUniqueId());
        sender.sendMessage(ChatColor.GREEN + "Revived " + playerName + " with " + fp.getLives() + " lives.");
    }

    private void showPlayerInfo(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found or offline!");
        } else {
            FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(target.getUniqueId());
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
            sender.sendMessage(ChatColor.YELLOW + "  Player Info: " + ChatColor.WHITE + target.getName());
            sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
            sender.sendMessage(ChatColor.YELLOW + "  Lives: " + ChatColor.WHITE + fp.getLives());
            sender.sendMessage(ChatColor.YELLOW + "  Trusted Players: " + ChatColor.WHITE + fp.getTrustedPlayers().size());
            if (fp.getPassiveAbility() != null) {
                sender.sendMessage(ChatColor.YELLOW + "  Passive: " + fp.getPassiveAbility().getFormattedName());
            }

            sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════════");
        }
    }

    private void reloadConfig(CommandSender sender) {
        this.plugin.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "FlameSMP configuration reloaded!");
    }

    private void clearCooldown(CommandSender sender, String playerName, String abilityName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found or offline!");
        } else {
            FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(target.getUniqueId());
            if (abilityName == null) {
                for (Essence essence : Essence.values()) {
                    fp.clearCooldown(essence.name() + "_0");
                    fp.clearCooldown(essence.name() + "_1");
                }

                sender.sendMessage(ChatColor.GREEN + "Cleared all cooldowns for " + target.getName());
                target.sendMessage(ChatColor.YELLOW + "An admin has cleared all your ability cooldowns!");
            } else {
                String cooldownKey = abilityName.toUpperCase();
                fp.clearCooldown(cooldownKey);
                sender.sendMessage(ChatColor.GREEN + "Cleared cooldown '" + cooldownKey + "' for " + target.getName());
                target.sendMessage(ChatColor.YELLOW + "An admin has cleared your " + abilityName + " cooldown!");
            }
        }
    }

    private void useAbility(CommandSender sender, String playerName, String essenceName, String slotStr) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found or offline!");
        } else {
            Essence essence;
            try {
                essence = Essence.valueOf(essenceName.toUpperCase());
            } catch (IllegalArgumentException var10) {
                sender.sendMessage(ChatColor.RED + "Invalid essence! Valid: " + Arrays.toString((Object[]) Essence.values()));
                return;
            }

            int slot;
            try {
                slot = Integer.parseInt(slotStr);
                if (slot < 0 || slot > 1) {
                    sender.sendMessage(ChatColor.RED + "Slot must be 0 or 1!");
                    return;
                }
            } catch (NumberFormatException var11) {
                sender.sendMessage(ChatColor.RED + "Invalid slot number!");
                return;
            }

            FlamePlayer fp = this.plugin.getDataManager().getFlamePlayer(target.getUniqueId());
            String cooldownKey = essence.name() + "_" + slot;
            fp.clearCooldown(cooldownKey);
            this.plugin.getEssenceAbilities().executeAbility(target, fp, essence, slot);
            sender.sendMessage(ChatColor.GREEN + "Executed " + essence.name() + " ability (slot " + slot + ") for " + target.getName());
            target.sendMessage(ChatColor.YELLOW + "An admin triggered your " + essence.name() + " ability!");
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (!sender.hasPermission("flamesmp.admin")) {
            return completions;
        } else {
            if (args.length == 1) {
                for (String sub : Arrays.asList("help", "deathstats", "setlives", "addlives", "setflame", "giveessence", "giveshard", "resetessence", "unban", "revive", "info", "reload", "clearcooldown", "useability", "givesolarsword", "givereviveitem"
                )) {
                    if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(sub);
                    }
                }
            } else if (args.length == 2) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(p.getName());
                    }
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("giveessence") || args[0].equalsIgnoreCase("resetessence")) {
                    for (Essence e : Essence.values()) {
                        if (e.name().toLowerCase().startsWith(args[2].toLowerCase())) {
                            completions.add(e.name().toLowerCase());
                        }
                    }

                    if (args[0].equalsIgnoreCase("resetessence") && "all".startsWith(args[2].toLowerCase())) {
                        completions.add("all");
                    }
                } else if (args[0].equalsIgnoreCase("setpassive")) {
                    for (PassiveAbility px : PassiveAbility.values()) {
                        if (px.name().toLowerCase().startsWith(args[2].toLowerCase())) {
                            completions.add(px.name().toLowerCase());
                        }
                    }
                } else if (args[0].equalsIgnoreCase("useability")) {
                    for (Essence ex : Essence.values()) {
                        if (ex.name().toLowerCase().startsWith(args[2].toLowerCase())) {
                            completions.add(ex.name().toLowerCase());
                        }
                    }
                } else if (args[0].equalsIgnoreCase("clearcooldown")) {
                    for (Essence exx : Essence.values()) {
                        String key0 = exx.name() + "_0";
                        String key1 = exx.name() + "_1";
                        if (key0.toLowerCase().startsWith(args[2].toLowerCase())) {
                            completions.add(key0);
                        }

                        if (key1.toLowerCase().startsWith(args[2].toLowerCase())) {
                            completions.add(key1);
                        }
                    }
                }
            } else if (args.length == 4 && args[0].equalsIgnoreCase("useability")) {
                if ("0".startsWith(args[3])) {
                    completions.add("0");
                }

                if ("1".startsWith(args[3])) {
                    completions.add("1");
                }
            }

            return completions;
        }
    }
}
