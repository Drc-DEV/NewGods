package pro.dracarys.newgods.api;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pro.dracarys.newgods.NewGods;
import pro.dracarys.newgods.config.Message;
import pro.dracarys.newgods.data.Believer;
import pro.dracarys.newgods.data.BelieverRank;
import pro.dracarys.newgods.data.God;
import pro.dracarys.newgods.utils.Util;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NewGodsAPI {

    public static void broadcastBelievers(UUID godID, String message) {
        Set<UUID> believers = NewGods.data.getBelievers().values().stream()
                .filter(b -> b.getGod() == godID)
                .map(Believer::getPlayerUUID).collect(Collectors.toSet());
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (believers.contains(player.getUniqueId()))
                player.sendMessage(message);
        }
    }

    public static UUID getGodID(String godName) {
        return NewGods.data.getGodID(godName);
    }

    public static UUID getGodID(OfflinePlayer player) {
        Believer b = NewGods.data.getBelievers().getOrDefault(player.getUniqueId(), null);
        if (b == null)
            return null;
        return b.getGod();
    }

    public static God getGod(OfflinePlayer player) {
        UUID godID = getGodID(player);
        if (godID != null) return NewGods.data.getGods().get(godID);
        return null;
    }

    public static void followGod(Player player, String godName) {
        Believer believer = NewGods.data.getBelievers().getOrDefault(player.getUniqueId(), null);
        if (believer != null) {
            player.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_HASGOD.getMessage());
            return;
        }
        Optional<God> optionalGod = NewGods.data.getGods().values().stream().filter(god1 -> god1.getName().equalsIgnoreCase(godName)).findFirst();
        if (optionalGod.isEmpty()) {
            player.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_GODNOTFOUND.getMessage());
            return;
        }
        God god = optionalGod.get();
        NewGods.data.getBelievers().put(player.getUniqueId(), new Believer(player.getUniqueId(), god.getId()));
        player.sendMessage(Message.PREFIX.getMessage() + Message.CMD_JOINGOD.getMessage());

        if (NewGods.data.getBelievers().values().stream().noneMatch(b1 -> b1.getGod() == god.getId() && b1.isLeader())) {
            NewGods.data.getBelievers().get(player.getUniqueId()).setRank(BelieverRank.LEADER);
            player.sendMessage(Message.PREFIX.getMessage() + Message.CMD_RANK_LEADER.getMessage());
        }
        NewGods.data.saveBelieverData();
    }

    public static UUID createGod(String godName, CommandSender creator) {
        UUID godID = UUID.randomUUID();
        NewGods.data.getGods().put(godID, new God(godName, godID));
        NewGods.data.saveGodData();
        creator.sendMessage(Message.PREFIX.getMessage() + Message.GOD_CREATED.getMessage());
        return godID;
    }

    public static boolean deleteGod(String godName, CommandSender creator) {
        UUID godID = NewGodsAPI.getGodID(godName);
        if (godID == null) return false;
        NewGods.data.getGods().remove(godID);
        NewGods.data.saveGodData();
        creator.sendMessage(Message.PREFIX.getMessage() + Message.GOD_DELETED.getMessage());
        return true;
    }

    public static String parseGodPlaceholders(String match, God god) {
        if (match.contains("{") && match.contains("}")) {
            Pattern p = Pattern.compile("(\\{[A-Za-z_-]+})");
            Matcher m = p.matcher(match);
            String parsed = match;
            while (m.find()) {
                parsed = parsed.replace(m.group(1), parseVar(m.group(1), god));
            }
            return parsed;
        } else {
            return parseVar(match, god);
        }
    }

    private static String parseVar(String match, God god) {
        match = match.replace("{", "").replace("}", "");
        return switch (match) {
            case "name" -> god.getName();
            case "power" -> god.getPower() + "";
            case "id" -> god.getId().toString();
            case "leadername" -> {
                Optional<Believer> bel = NewGods.data.getBelievers().values().stream()
                        .filter(b -> b.getGod() == god.getId() && b.isLeader()).findFirst();
                if (bel.isPresent()) yield Bukkit.getPlayer(bel.get().getPlayerUUID()).getName();
                yield "Server";
            }

            case "spawnlocation" -> Util.locationSerialize(god.getSpawnLocation());
            case "type" -> god.getType();
            case "color" -> ChatColor.valueOf(god.getType()) + "";
            case "followersnames" -> String.join(", ", NewGods.data.getBelievers().values().stream()
                    .filter(believer -> believer.getGod() == god.getId())
                    .map(believer -> Bukkit.getPlayer(believer.getPlayerUUID()).getName()).toList());
            case "believersnames" -> String.join(", ", NewGods.data.getBelievers().values().stream()
                    .filter(believer -> believer.getGod() == god.getId() && !believer.isPriest() && !believer.isLeader())
                    .map(believer -> Bukkit.getPlayer(believer.getPlayerUUID()).getName()).toList());
            case "priestsnames" -> String.join(", ", NewGods.data.getBelievers().values().stream()
                    .filter(believer -> believer.getGod() == god.getId() && believer.isPriest())
                    .map(believer -> Bukkit.getPlayer(believer.getPlayerUUID()).getName()).toList());
            case "priests" ->
                    NewGods.data.getBelievers().values().stream().filter(believer -> believer.getGod() == god.getId() && believer.isPriest()).count() + "";
            case "believers" -> // does not include priests and leader
                    NewGods.data.getBelievers().values().stream().filter(believer -> believer.getGod() == god.getId() && !believer.isLeader() && !believer.isPriest()).count() + "";
            case "sm-amount" ->
                    NewGods.data.getMobSacrificeMap().containsKey(god.getId()) ? NewGods.data.getMobSacrificeMap().get(god.getId()).getNumber() + "" : "0";
            case "si-amount" ->
                    NewGods.data.getItemSacrificeMap().containsKey(god.getId()) ? NewGods.data.getItemSacrificeMap().get(god.getId()).getNumber() + "" : "0";
            case "sm-type" ->
                    NewGods.data.getMobSacrificeMap().containsKey(god.getId()) ? NewGods.data.getMobSacrificeMap().get(god.getId()).getType().name() : "N/A";
            case "si-type" ->
                    NewGods.data.getItemSacrificeMap().containsKey(god.getId()) ? NewGods.data.getItemSacrificeMap().get(god.getId()).getType().name() : "N/A";
            case "followers" -> // includes priests and leader
                    NewGods.data.getBelievers().values().stream().filter(believer -> believer.getGod() == god.getId()).count() + "";
            default -> "Invalid God Placeholder";
        };
    }

}
