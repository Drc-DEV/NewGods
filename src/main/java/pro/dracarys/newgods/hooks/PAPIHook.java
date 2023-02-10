package pro.dracarys.newgods.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import pro.dracarys.newgods.NewGods;
import pro.dracarys.newgods.api.NewGodsAPI;
import pro.dracarys.newgods.config.Message;
import pro.dracarys.newgods.data.Believer;
import pro.dracarys.newgods.data.God;
import pro.dracarys.newgods.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PAPIHook extends PlaceholderExpansion {

    public static String papi(String str, OfflinePlayer p) {
        return PlaceholderAPI.setPlaceholders(p, str);
    }

    public static List<String> papi(List<String> str, OfflinePlayer p) {
        if (str.isEmpty()) return str;
        List<String> parsed = new ArrayList<>();
        for (String string : str) {
            parsed.add(Util.color(PlaceholderAPI.setPlaceholders(p, string)));
        }
        return parsed;
    }

    @Override
    public String getIdentifier() {
        return "newgods";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "Dracarys";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {

        if (identifier.startsWith("god_")) { // uses player's god if any
            String[] split = identifier.split("_");
            if (!NewGods.data.getBelievers().containsKey(player.getUniqueId())) return "N/A";
            God god = NewGods.data.getGods().get(NewGods.data.getBelievers().get(player.getUniqueId()));
            return NewGodsAPI.parseGodPlaceholders(split[1], god);
        } else if (identifier.startsWith("gods_")) { // uses given god name if exists
            String[] split = identifier.split("_");
            UUID godID = NewGodsAPI.getGodID(identifier.split("_")[1]);
            if (godID == null) return "Invalid God UUID";
            God god = NewGods.data.getGods().get(godID);
            if (split.length < 2) return "Invalid God Placeholder";
            return NewGodsAPI.parseGodPlaceholders(split[2], god);
        } else if (identifier.startsWith("believer_")) { // uses player's god if any
            Believer b = NewGods.data.getBelievers().getOrDefault(player.getUniqueId(), null);
            if (b != null) {
                if (identifier.endsWith("_rank")) return Message.valueOf("RANK_" + b.getRank().name()).getMessage();
                if (identifier.endsWith("_holiness")) return b.getHoliness() + "";
                if (identifier.endsWith("_happiness")) return b.getHappiness() + "";
            } else {
                return "";
            }
        }


        return null;
    }


}
