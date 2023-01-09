package pro.dracarys.newgods.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pro.dracarys.newgods.NewGods;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Data {

    // Persistent data

    private Map<UUID, God> gods;
    private Map<UUID, Believer> believers;

    // Temporary data

    private Map<UUID, MobSacrifice> mobSacrificeMap = new HashMap<>(); // only 1 at the time, god UUID
    private Map<UUID, ItemSacrifice> itemSacrificeMap = new HashMap<>(); // only 1 at the time, god UUID

    public Data() {
        reloadData();
    }

    FileConfiguration bFile;
    File believerFile;
    FileConfiguration gFile;
    File godFile;

    public Map<UUID, God> getGods() {
        return this.gods;
    }

    public Map<UUID, Believer> getBelievers() {
        return believers;
    }

    public Map<UUID, MobSacrifice> getMobSacrificeMap() {
        return mobSacrificeMap;
    }

    public Map<UUID, ItemSacrifice> getItemSacrificeMap() {
        return itemSacrificeMap;
    }

    private void loadGods() {
        File gdata = new File(NewGods.getInstance().getDataFolder(), File.separator + "data");
        godFile = new File(gdata, File.separator + "gods" + ".yml");
        gFile = YamlConfiguration.loadConfiguration(godFile);
        if (!godFile.exists()) {
            try {
                gFile.createSection("gods");
                gFile.save(godFile);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        this.gods = new HashMap<>();
        for (String key : gFile.getConfigurationSection("gods").getKeys(false)) {
            this.gods.put(UUID.fromString(key), (God) gFile.get("gods." + key));
        }
    }

    private void loadBelievers() {
        File gdata = new File(NewGods.getInstance().getDataFolder(), File.separator + "data");
        believerFile = new File(gdata, File.separator + "believers" + ".yml");
        bFile = YamlConfiguration.loadConfiguration(believerFile);
        if (!believerFile.exists()) {
            try {
                bFile.createSection("believers");
                bFile.save(believerFile);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        this.believers = new HashMap<>();
        for (String key : bFile.getConfigurationSection("believers").getKeys(false)) {
            this.believers.put(UUID.fromString(key), (Believer) bFile.get("believers." + key));
        }
    }

    public UUID getGodID(String god) {
        if (god == null) return null;
        Optional<God> foundGod = gods.values().stream().filter(godObj -> godObj.getName().equalsIgnoreCase(god)).findFirst();
        return foundGod.map(God::getId).orElse(null);
    }

    public void saveGodData() {
        try {
            gFile.set("gods", gods);
            gFile.save(godFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveBelieverData() {
        try {
            bFile.set("believers", believers);
            bFile.save(believerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadData() {
        loadGods();
        loadBelievers();
    }

}
