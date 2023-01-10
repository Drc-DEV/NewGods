package pro.dracarys.newgods.data;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import pro.dracarys.newgods.utils.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class God implements ConfigurationSerializable {

    String name;
    UUID id;
    int power;
    String spawnLocation; // serialized
    String type;

    public God(String name, UUID id) {
        this.name = name;
        this.id = id;
        this.power = 0;
        this.spawnLocation = null;
        this.type = "AQUA";
    }

    public God(String name, UUID id, int power, String spawnLocation, String type) {
        this.name = name;
        this.id = id;
        this.power = power;
        this.spawnLocation = spawnLocation;
        this.type = type;
    }

    // Deserialization constructor
    public God deserialize(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.id = UUID.fromString((String) map.get("id"));
        this.power = (int) map.getOrDefault("power", 0);
        this.spawnLocation = (String) map.getOrDefault("spawn-loc", null);
        this.type = (String) map.getOrDefault("type", "AQUA");
        return this;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> mapSerializer = new HashMap<>();
        mapSerializer.put("name", this.name);
        mapSerializer.put("id", this.id.toString());
        mapSerializer.put("power", this.power);
        mapSerializer.put("spawnLocation", this.spawnLocation);
        mapSerializer.put("type", this.type);
        return mapSerializer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public void addPower(int power) {
        this.power = this.power + power;
    }

    public Location getSpawnLocation() {
        return Util.locationDeserialize(this.spawnLocation);
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = Util.locationSerialize(spawnLocation);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
