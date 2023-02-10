package pro.dracarys.newgods.listeners;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import pro.dracarys.newgods.NewGods;
import pro.dracarys.newgods.api.NewGodsAPI;
import pro.dracarys.newgods.commands.CmdGod;
import pro.dracarys.newgods.config.Config;
import pro.dracarys.newgods.config.Message;
import pro.dracarys.newgods.data.Believer;
import pro.dracarys.newgods.data.God;
import pro.dracarys.newgods.utils.Util;

import java.util.HashMap;
import java.util.UUID;

public class PrayListener implements Listener {

    static HashMap<UUID, Long> plrs = new HashMap<>();

    private String getAltarGodName(Sign sign) {
        if (sign.getLine(0).equals("=========") && sign.getLine(1).equalsIgnoreCase(Config.GOD_PRAY_ALTAR_SIGN.getString())) {
            return sign.getLine(2);
        }
        return null;
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        String title = event.getLine(0);
        if (title == null) return;
        if (title.equalsIgnoreCase("god") || title.equalsIgnoreCase("gods")) {
            String godName = event.getLine(1);
            if (godName == null) return;
            if (godName.length() > 0 && Util.isAlphaNumeric(godName)) {
                godName = godName.trim();
                UUID godID = null;
                for (God god : NewGods.data.getGods().values()) {
                    if (god.getName().equalsIgnoreCase(godName)) {
                        godName = god.getName();
                        godID = god.getId();
                        break;
                    }
                }
                Player p = event.getPlayer();
                Believer b = NewGods.data.getBelievers().getOrDefault(p.getUniqueId(), null);
                if (b != null && b.getGod() != godID) {
                    p.sendMessage(Message.PREFIX.getMessage() + Message.GOD_WRONGGODALTAR.getMessage());
                    return;
                }
                event.setLine(0, "=========");
                event.setLine(1, Config.GOD_PRAY_ALTAR_SIGN.getString());
                event.setLine(2, godName);
                event.setLine(3, "=========");
                CmdGod.createAltar(event, godName);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock().getState() instanceof Sign sign) {
            String godName = getAltarGodName(sign);
            if (godName == null) return;
            Player p = e.getPlayer();
            if (!p.hasPermission("newgods.pray")) {
                p.sendMessage(Message.ERROR_NOPERM.getMessage());
                return;
            }
            UUID godID = NewGodsAPI.getGodID(godName);
            if (godID == null) return;
            Believer believer = NewGods.data.getBelievers().getOrDefault(p.getUniqueId(), null);
            if (believer == null) { // if you have no god, you'll join the religion
                NewGodsAPI.followGod(p, godName);
                return;
            }
            int timeBetweenPrays = Config.GOD_PRAY_DELAY.getInt();
            if (p.hasPermission("newgods.halfpraytime"))
                timeBetweenPrays = timeBetweenPrays / 2;
            if (plrs.containsKey(p.getUniqueId())) {
                long time = System.currentTimeMillis() / 1000L;
                if (plrs.get(p.getUniqueId()) > time - timeBetweenPrays) {
                    long remaining = timeBetweenPrays - (time - plrs.get(p.getUniqueId()));
                    Util.sendActionBar(p, Message.GENERIC_COOLDOWN.getMessage()
                            .replace("{seconds}", remaining + ""));
                    return;
                }
            }
            if (godID == believer.getGod()) {
                int prayReward = Config.GOD_PRAY_REWARD.getInt();
                if (p.hasPermission("newgods.doublexp"))
                    prayReward = prayReward * 2;
                NewGods.data.getGods().get(godID).addPower(Config.GOD_PRAY_REWARD.getInt()); // power not affected by multiplier
                believer.editHoliness(prayReward);
                believer.editHappiness(prayReward);

                p.sendMessage(Message.PREFIX.getMessage() + NewGodsAPI.parseGodPlaceholders(Message.GOD_PRAYED.getMessage()
                        .replace("{gainedpower}", Config.GOD_PRAY_REWARD.getInt() + ""), NewGods.data.getGods().get(godID)));

                plrs.put(p.getUniqueId(), System.currentTimeMillis() / 1000L);
            } else {
                // you have another god
                p.sendMessage(ChatColor.DARK_RED + "This isn't an altar for your god. Type /gleave to leave your god.");
            }
        }
    }
}