package pro.dracarys.newgods.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pro.dracarys.newgods.config.Config;
import pro.dracarys.newgods.config.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Util {
    public static void sendConsole(String str) {
        Bukkit.getConsoleSender().sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', str));
    }

    public static int roundToInt(double doubleVar) {
        return (int) Math.round(doubleVar);
    }

    public static boolean checkPermission(CommandSender s, String permission) {
        if (s.hasPermission(permission)) return true;
        s.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_NOPERM.getMessage().replace("{permission}", permission));
        return false;
    }

    public static boolean checkPlayer(CommandSender s, String permission) {
        if (s instanceof Player)
            return true;
        s.sendMessage(Message.PREFIX.getMessage() + Message.ERROR_NOPLAYER.getMessage());
        return false;
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> color(List<String> string) {
        List<String> colored = new ArrayList<>();
        for (String line : string) {
            colored.add(color(line));
        }
        return colored;
    }

    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Util.color(message)));
    }

    public static boolean isAlphaNumeric(String s) {
        return s != null && s.matches("^[a-zA-Z0-9]*$");
    }

    public static <E> Optional<E> getRandom(Collection<E> e) {
        return e.stream()
                .skip((int) (e.size() * Math.random()))
                .findFirst();
    }

    public static void debug(String str) {
        if (Config.DEBUG.getOption())
            sendConsole("&e<DEBUG> " + Message.PREFIX.getMessage() + "&6" + str);
    }

    public static void error(String str) {
        sendConsole("&c<ERROR> " + Message.PREFIX.getMessage() + "&6" + str);
    }

    public static String locationSerialize(Location loc) {
        return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch();
    }

    public static Location locationDeserialize(String loc) {
        String serializedLoc = loc.contains("~") ? loc.split("~")[0] : loc; //Account for Aliased Locations
        String[] l = serializedLoc.split(";");
        if (l.length > 4) {
            return new Location(Bukkit.getWorld(l[0]), Double.parseDouble(l[1]), Double.parseDouble(l[2]), Double.parseDouble(l[3]), Float.parseFloat(l[4]), Float.parseFloat(l[5]));
        } else {
            return new Location(Bukkit.getWorld(l[0]), Double.parseDouble(l[1]), Double.parseDouble(l[2]), Double.parseDouble(l[3]));
        }
    }

}
