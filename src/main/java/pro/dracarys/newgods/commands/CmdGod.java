package pro.dracarys.newgods.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import pro.dracarys.configlib.ConfigLib;
import pro.dracarys.newgods.NewGods;
import pro.dracarys.newgods.api.NewGodsAPI;
import pro.dracarys.newgods.config.Config;
import pro.dracarys.newgods.config.Message;
import pro.dracarys.newgods.data.Believer;
import pro.dracarys.newgods.data.BelieverRank;
import pro.dracarys.newgods.data.God;
import pro.dracarys.newgods.utils.Util;

import java.util.*;

public class CmdGod implements TabExecutor {

    List<String> subCommands = List.of("reload", "type", "delete");

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> lista = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("newgods.admin")) {
                lista.addAll(StringUtil.copyPartialMatches(args[0], subCommands, new ArrayList<>()));
            }

        }
        return lista;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        Believer believer = null;
        if (sender instanceof Player p) {
            believer = NewGods.data.getBelievers().getOrDefault(p.getUniqueId(), null);
        }
        if ((args.length == 0 || args[0].equalsIgnoreCase("help"))
                && Util.checkPermission(sender, "newgods.help")) {
            sender.sendMessage(Message.CMD_MAIN_HEADER.getMessage());
            for (String helpMsg : Message.CMD_HELP_LEADER.getMessages()) {
                sender.sendMessage(helpMsg);
            }
            for (String helpMsg : Message.CMD_HELP_PRIEST.getMessages()) {
                sender.sendMessage(helpMsg);
            }
            if (Config.MARRIAGES_ENABLED.getOption())
                for (String helpMsg : Message.CMD_HELP_MARRIAGE.getMessages()) {
                    sender.sendMessage(helpMsg);
                }
            for (String helpMsg : Message.CMD_HELP_GENERAL.getMessages()) {
                sender.sendMessage(helpMsg);
            }
            sender.sendMessage(Message.CMD_MAIN_FOOTER.getMessage());
        } else if (args[0].equalsIgnoreCase("reload") && Util.checkPermission(sender, "newgods.reload")) {
            ConfigLib.initAll();
            sender.sendMessage(Message.PREFIX.getMessage() + Message.CMD_RELOAD.getMessage());
        } else if (args[0].equalsIgnoreCase("type") && Util.checkPermission(sender, "newgods.type")) {

            if (args.length < 2) {
                sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_TYPENOTFOUND.getMessage()
                        .replace("{godTypes}", String.join(", ", Config.GOD_TYPE_WHITELIST.getStrings())));
                return true;
            }
            if (!isValidType(sender, args[1]))
                return true;
            if (sender instanceof Player) { // Uses Player's God as target god
                if (believer == null || !believer.isLeader()) {
                    sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_ONLYLEADER.getMessage());
                    return true;
                }
                NewGods.data.getGods().get(believer.getGod()).setType(args[1].toUpperCase());
            } else {
                if (args.length < 3) { // arg[2] is god name
                    sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_GODNOTFOUND.getMessage());
                    return true;
                }
                NewGods.data.getGods().get(NewGodsAPI.getGodID(args[2])).setType(args[1].toUpperCase());
            }
            NewGods.data.saveGodData();
            sender.sendMessage(Message.PREFIX.getMessage() + Message.CMD_SETTYPE.getMessage());
        } else if (args[0].equalsIgnoreCase("list") && Util.checkPermission(sender, "newgods.list")) {
            long page = 0;
            if (args.length > 1) {
                try {
                    page = Long.parseLong(args[1]);
                } catch (NumberFormatException nfe) {
                    page = 0;
                }
            }
            boolean hasNext = NewGods.data.getGods().size() > (page + 1) * Config.GOD_LIST_MAXPAGE.getInt();
            sender.sendMessage(Message.CMD_LIST_HEADER.getMessage()
                    .replace("{page}", page + "")
                    .replace("{nextPage}", hasNext ? "" + (page + 1) : page + ""));
            NewGods.data.getGods().values().stream()
                    .sorted(Comparator.comparingInt(God::getPower)) // sort by power
                    .skip(page * Config.GOD_LIST_MAXPAGE.getInt()) // skip if page>0
                    .forEach(god -> sender.sendMessage(NewGodsAPI.parseGodPlaceholders(Message.CMD_LIST_FORMAT.getMessage(), god)));
            if (hasNext)
                sender.sendMessage(Message.CMD_LIST_FOOTER.getMessage()
                        .replace("{page}", page + "")
                        .replace("{nextPage}", (page + 1) + ""));
        } else if (args[0].equalsIgnoreCase("info") && Util.checkPermission(sender, "newgods.info")) {
            UUID godID = null;
            if (args.length > 1) {
                godID = NewGodsAPI.getGodID(args[1]);
            } else if (sender instanceof Player p) {
                godID = NewGodsAPI.getGodID(p);
            }
            if (godID == null) {
                sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_GODNOTFOUND.getMessage());
                return true;
            }
            God god = NewGods.data.getGods().get(godID);
            Arrays.stream(Message.CMD_INFO_FORMAT.getMessages())
                    .forEach(str -> sender.sendMessage(NewGodsAPI.parseGodPlaceholders(str, god)));
        } else if (args[0].equalsIgnoreCase("sethome") && Util.checkPermission(sender, "newgods.sethome")) {
            if (sender instanceof Player p) {
                if (believer == null || !believer.isLeader()) {
                    sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_ONLYLEADER.getMessage());
                    return true;
                }
                NewGods.data.getGods().get(believer.getGod()).setSpawnLocation(p.getLocation());
                NewGods.data.saveGodData();
            } else {
                sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_NOPLAYER.getMessage());
                return true;
            }
        } else if (args[0].equalsIgnoreCase("delhome") && Util.checkPermission(sender, "newgods.delhome")) {
            UUID godID = null;
            if (args.length > 1) {
                godID = NewGodsAPI.getGodID(args[1]);
            } else if (sender instanceof Player p) {
                if (believer == null || !believer.isLeader()) {
                    sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_ONLYLEADER.getMessage());
                    return true;
                }
                godID = NewGodsAPI.getGodID(p);
            }
            NewGods.data.getGods().get(godID).setSpawnLocation(null);
            NewGods.data.saveGodData();
            sender.sendMessage(Message.PREFIX.getMessage() + NewGodsAPI.parseGodPlaceholders(Message.CMD_DELHOME.getMessage(), NewGods.data.getGods().get(godID)));
        } else if (args[0].equalsIgnoreCase("home") && Util.checkPermission(sender, "newgods.home")) {
            Location loc = null;
            if (sender instanceof Player p) {
                if (believer == null) {
                    sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_NOGOD.getMessage());
                    return true;
                }
                loc = NewGods.data.getGods().get(believer.getGod()).getSpawnLocation();
                if (loc != null) {
                    p.teleport(loc);
                    sender.sendMessage(Message.PREFIX.getMessage() + Message.CMD_HOME_TP.getMessage());
                }
            } else if (args.length > 1) {
                Player p = Bukkit.getPlayer(args[1]);
                if (p == null) {
                    sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_NULLPLAYER.getMessage());
                    return true;
                }
                Believer tempB = NewGods.data.getBelievers().getOrDefault(p.getUniqueId(), null);
                if (tempB != null)
                    loc = NewGods.data.getGods().get(tempB.getGod()).getSpawnLocation();
                if (loc != null) {
                    p.teleport(loc);
                    sender.sendMessage(Message.PREFIX.getMessage() + Message.CMD_HOME_TP.getMessage());
                }
            }

        } else if (args[0].equalsIgnoreCase("leave") && Util.checkPermission(sender, "newgods.leave")) {
            if (sender instanceof Player p) {
                leaveGod(p);
            } else if (args.length > 1) {
                Player p = Bukkit.getPlayer(args[1]);
                if (p == null) {
                    sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_NULLPLAYER.getMessage());
                    return true;
                }
                leaveGod(p);
            }
        } else if (args[0].equalsIgnoreCase("rank") && Util.checkPermission(sender, "newgods.rank")) {
            if (sender instanceof Player && (believer == null || !believer.isLeader())) {
                sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_ONLYLEADER.getMessage());
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_NOPLAYER.getMessage());
                return true;
            }
            if (args.length < 3 || Arrays.stream(BelieverRank.values()).noneMatch(brank -> brank.name().equalsIgnoreCase(args[2]))) {
                sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_NORANK.getMessage());
                return true;
            }
            BelieverRank rank = BelieverRank.valueOf(args[2]);
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_NULLPLAYER.getMessage());
                return true;
            }
            UUID targetGod = NewGodsAPI.getGodID(target);
            if (targetGod == null || (believer != null && targetGod != believer.getGod())) {
                sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_NOTSAMEGOD.getMessage());
                return true;
            }
            if (rank == BelieverRank.LEADER)
                NewGods.data.getBelievers().values().stream()
                        .filter(b1 -> b1.getGod() == targetGod && b1.isLeader())
                        .forEach(b1 -> b1.setRank(BelieverRank.PRIEST));
            NewGods.data.getBelievers().get(target.getUniqueId()).setRank(rank);
            NewGods.data.saveBelieverData();
            return true;
        }

        return true;
    }

    public static void leaveGod(Player player) {
        Believer believer = NewGods.data.getBelievers().getOrDefault(player.getUniqueId(), null);
        if (believer == null) {
            player.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_NOGOD.getMessage());
            return;
        }
        God god = NewGods.data.getGods().getOrDefault(believer.getGod(), null);
        if (god == null) {
            player.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_NOGOD.getMessage());
            return;
        }
        NewGods.data.getBelievers().remove(player.getUniqueId());
        // Make a priest the new leader
        if (believer.isLeader()) {
            Optional<Believer> newLeader = NewGods.data.getBelievers().values().stream().filter(bel -> bel.isPriest() && bel.getGod() == god.getId()).findFirst();
            if (newLeader.isPresent()) {
                newLeader.get().setRank(BelieverRank.LEADER);
            } else { // if no priest is present, make anybody leader
                NewGods.data.getBelievers().values().stream()
                        .filter(bel -> bel.getGod() == god.getId()).findFirst()
                        .ifPresent(b1 -> b1.setRank(BelieverRank.LEADER));
            }
        }
        // Delete god if no followers
        if (Config.GOD_AUTODELETE.getOption() && NewGods.data.getBelievers().values().stream().noneMatch(bel -> bel.getGod() == god.getId())) {
            NewGods.data.getGods().remove(god.getId());
            NewGods.data.saveGodData();
        }
        NewGods.data.saveBelieverData();
        player.sendMessage(Message.PREFIX.getMessage() + Message.CMD_LEAVEGOD.getMessage());
    }


    public static void createAltar(SignChangeEvent event, String godName) {
        Player player = event.getPlayer();
        if (!Util.checkPermission(player, "newgods.create"))
            return;
        for (String bannedName : Config.GOD_NAME_BLACKLIST.getStringList()) {
            if (godName.toLowerCase().contains(bannedName.toLowerCase())) {
                player.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_BLACKLISTED.getMessage());
                return;
            }
        }
        if (NewGodsAPI.getGodID(godName) == null) {
            UUID godID = UUID.randomUUID();
            NewGods.data.getGods().put(godID, new God(godName, godID));
            player.sendMessage(Message.PREFIX.getMessage() + Message.GOD_CREATED.getMessage());
            if (Config.GOD_SETSPAWN_ONCREATE.getOption()
                    && Config.GOD_SPAWNWORLDS.getStringList().stream().anyMatch(s -> s.equalsIgnoreCase(event.getBlock().getWorld().getName()))) {
                NewGods.data.getGods().get(godID).setSpawnLocation(event.getBlock().getLocation());
                player.sendMessage(Message.PREFIX.getMessage() + Message.CMD_SETSPAWN.getMessage());
            }
            NewGods.data.saveGodData();
            NewGodsAPI.followGod(player, godName);
        } else {
            if (NewGodsAPI.getGodID(player) == null) {
                NewGodsAPI.followGod(player, godName);
            }
            player.sendMessage(Message.PREFIX.getMessage() + Message.GOD_CREATEDALTAR.getMessage());
        }
    }

    // only colors that are easy to read in chat
    public static boolean isValidType(CommandSender sender, String type) {
        if (Arrays.stream(ChatColor.values()).noneMatch(chatColor -> chatColor.name().equalsIgnoreCase(type))
                || Config.GOD_TYPE_WHITELIST.getStringList().stream().noneMatch(s -> s.equalsIgnoreCase(type))) {
            sender.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_TYPENOTFOUND.getMessage()
                    .replace("{godTypes}", String.join(", ", Config.GOD_TYPE_WHITELIST.getStrings())));
            return false;
        }
        return true;
    }

}
