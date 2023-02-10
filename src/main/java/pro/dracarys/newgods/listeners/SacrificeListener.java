package pro.dracarys.newgods.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import pro.dracarys.newgods.NewGods;
import pro.dracarys.newgods.api.NewGodsAPI;
import pro.dracarys.newgods.config.Message;
import pro.dracarys.newgods.data.Believer;
import pro.dracarys.newgods.data.ItemSacrifice;
import pro.dracarys.newgods.data.MobSacrifice;
import pro.dracarys.newgods.utils.Util;

import java.util.UUID;

public class SacrificeListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onMobDeath(EntityDeathEvent e) {
        if (e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent nEvent
                && nEvent.getDamager() instanceof Player p) {
            sacrificeMob(p, e.getEntityType());
        }
    }

    private void sacrificeMob(Player p, EntityType e) {
        if (!NewGods.data.getBelievers().containsKey(p.getUniqueId())) return;
        Believer believer = NewGods.data.getBelievers().get(p.getUniqueId());
        UUID godID = believer.getGod();
        MobSacrifice s = NewGods.data.getMobSacrificeMap().getOrDefault(godID, null);
        if (s == null || s.getType() != e) return;
        s.setNumber(Math.max(0, s.getNumber() - 1));
        int reward = s.getReward();
        if (p.hasPermission("newgods.doublexp"))
            reward = reward * 2;
        if (s.getNumber() <= 0) {
            NewGodsAPI.broadcastBelievers(godID, Message.PREFIX.getMessage() + Message.SACRIFICE_MOB_END.getMessage());
            NewGods.data.getGods().get(godID).addPower(s.getReward()); // god power not affected by multipliers
            NewGods.data.getMobSacrificeMap().remove(godID);
        } else {
            Util.sendActionBar(p, Message.SACRIFICE_MOB.getMessage().replace("{remaining}", s.getNumber() + ""));
        }
        believer.editHoliness(reward);
        believer.editHappiness(reward);
    }


    @EventHandler(ignoreCancelled = true)
    public void onBurn(EntityCombustEvent e) {
        if (e.getEntityType() == EntityType.DROPPED_ITEM && e.getEntity() instanceof Item item && item.getThrower() != null) {
            Believer believer = NewGods.data.getBelievers().getOrDefault(item.getThrower(), null);
            if (believer == null) return;
            UUID godID = believer.getGod();
            ItemSacrifice s = NewGods.data.getItemSacrificeMap().getOrDefault(godID, null);
            if (s == null || s.getType() != item.getItemStack().getType()) return;
            s.setNumber(Math.max(0, s.getNumber() - 1));
            int reward = s.getReward();
            Player p = Bukkit.getPlayer(item.getThrower());
            if (p == null) return;
            if (p.hasPermission("newgods.doublexp"))
                reward = reward * 2;
            if (s.getNumber() <= 0) {
                NewGodsAPI.broadcastBelievers(godID, Message.PREFIX.getMessage() + Message.SACRIFICE_ITEM_END.getMessage());
                NewGods.data.getGods().get(godID).addPower(s.getReward()); // god power not affected by multipliers
                NewGods.data.getMobSacrificeMap().remove(godID);
            } else {
                Util.sendActionBar(p, Message.SACRIFICE_ITEM.getMessage().replace("{remaining}", s.getNumber() + ""));
            }
            believer.editHoliness(reward);
            believer.editHappiness(reward);
        }
    }

}
