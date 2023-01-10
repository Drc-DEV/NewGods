package pro.dracarys.newgods;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pro.dracarys.configlib.ConfigLib;
import pro.dracarys.newgods.api.NewGodsAPI;
import pro.dracarys.newgods.commands.CmdGod;
import pro.dracarys.newgods.config.Config;
import pro.dracarys.newgods.config.Message;
import pro.dracarys.newgods.config.file.ConfigFile;
import pro.dracarys.newgods.config.file.MessageFile;
import pro.dracarys.newgods.data.*;
import pro.dracarys.newgods.listeners.PrayListener;
import pro.dracarys.newgods.listeners.SacrificeListener;
import pro.dracarys.newgods.utils.Util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class NewGods extends JavaPlugin {

    public static NewGods getInstance() {
        return plugin;
    }

    private static NewGods plugin;

    public static Data data;

    @Override
    public void onEnable() {
        plugin = this;
        ConfigurationSerialization.registerClass(God.class);
        ConfigurationSerialization.registerClass(Believer.class);
        data = new Data();
        registerConfig();
        registerCommand("gods", new CmdGod());
        registerEvents();
        getLogger().info(getDescription().getName() + " has been enabled! Version:" + getDescription().getVersion());
        regularUpdate();
    }

    @Override
    public void onDisable() {
        data.saveGodData();
        data.saveBelieverData();
        getServer().getScheduler().cancelTasks(plugin);
        Logger logger = getLogger();
        org.bukkit.event.HandlerList.unregisterAll(plugin);
        logger.info(getDescription().getName() + " has been disabled! Version:" + getDescription().getVersion());
        plugin = null;
    }

    public void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PrayListener(), this);
        pm.registerEvents(new SacrificeListener(), this);
    }

    private void registerCommand(String command, CommandExecutor executor) {
        PluginCommand cmd = this.getCommand(command);
        if (cmd != null) {
            cmd.setExecutor(executor);
            if (executor instanceof TabCompleter tc)
                cmd.setTabCompleter(tc);
        }
    }

    public void registerConfig() {
        ConfigLib.setPlugin(this);
        ConfigLib.addFile(new ConfigFile());
        ConfigLib.addFile(new MessageFile());
        ConfigLib.initAll();
    }

    public void regularUpdate() {
        // Update mob sacrifice, overwriting current one (time expired)
        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (UUID godID : data.getGods().keySet()) {
                String[] values = Util.getRandom(Config.SACRIFICE_MOBS.getStringList()).get().split(",");
                data.getMobSacrificeMap().put(godID, new MobSacrifice(Integer.parseInt(values[1]), Integer.parseInt(values[2]), EntityType.valueOf(values[0])));
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!data.getBelievers().containsKey(player.getUniqueId())) continue; // skip filthy nonbelievers
                God god = data.getGods().get(data.getBelievers().get(player.getUniqueId()).getGod());
                player.sendMessage(Message.PREFIX.getMessage() + NewGodsAPI.parseGodPlaceholders(Message.SACRIFICE_NOTIF_MOBS.getMessage(), god));
            }
        }, 0, Config.SACRIFICE_MOBS_SPEED.getInt() * 60L * 20L);
        // Update item sacrifice, overwriting current one (time expired)
        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (UUID godID : data.getGods().keySet()) {
                String[] values = Util.getRandom(Config.SACRIFICE_ITEMS.getStringList()).get().split(",");
                data.getItemSacrificeMap().put(godID, new ItemSacrifice(Integer.parseInt(values[1]), Integer.parseInt(values[2]), Material.valueOf(values[0])));
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!data.getBelievers().containsKey(player.getUniqueId())) continue; // skip filthy nonbelievers
                God god = data.getGods().get(data.getBelievers().get(player.getUniqueId()).getGod());
                player.sendMessage(Message.PREFIX.getMessage() + NewGodsAPI.parseGodPlaceholders(Message.SACRIFICE_NOTIF_ITEMS.getMessage(), god));
            }
        }, 0, Config.SACRIFICE_ITEMS_SPEED.getInt() * 60L * 20L);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!data.getBelievers().containsKey(player.getUniqueId())) continue; // skip filthy nonbelievers
                Believer believer = data.getBelievers().get(player.getUniqueId());
                if (Config.EXP_ENABLED.getOption() && believer.getHappiness() > 20 && ThreadLocalRandom.current().nextFloat() > 0.9) {
                    player.giveExp(Config.EXP_BLESS_AMOUNT.getInt());
                    player.sendMessage(Message.PREFIX.getMessage() +
                            NewGodsAPI.parseGodPlaceholders(Message.GOD_BLESSING.getMessage()
                                            .replace("{blessamount}", Config.EXP_BLESS_AMOUNT.getInt() + ""),
                                    NewGods.data.getGods().get(believer.getGod())));
                }

                if (Config.GOD_PUNISHMENTS_ENABLED.getOption() && believer.getHappiness() <= 0) {
                    //curse player
                    player.getWorld().strikeLightning(player.getLocation());
                    player.sendMessage(Message.PREFIX.getMessage() +
                            NewGodsAPI.parseGodPlaceholders(Message.GOD_UNHAPPY.getMessage(), NewGods.data.getGods().get(believer.getGod())));
                    player.getWorld().strikeLightning(player.getLocation().add(1, 0, 1));
                    player.getWorld().strikeLightning(player.getLocation().add(-3, 0, 2));

                }
                // decrease happiness with time
                if (ThreadLocalRandom.current().nextFloat() > 0.75 && believer.getHappiness() > 0) {
                    believer.setHappiness(believer.getHappiness() - 5);
                }
            }
        }, 0, Config.HAPPNIESS_UPDATE_SPEED.getInt() * 60L * 20L);
    }

}
