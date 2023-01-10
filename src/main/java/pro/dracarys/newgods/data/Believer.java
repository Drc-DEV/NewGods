package pro.dracarys.newgods.data;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Believer implements ConfigurationSerializable {

    UUID playerUUID;
    BelieverRank rank;
    int happiness;
    int holiness;
    UUID playerPartner;
    UUID god;

    public Believer(UUID playerUUID, BelieverRank rank, int happiness, int holiness, UUID playerPartner, UUID god) {
        this.playerUUID = playerUUID;
        this.rank = rank;
        this.happiness = happiness;
        this.holiness = holiness;
        this.playerPartner = playerPartner;
        this.god = god;
    }

    public Believer(UUID playerUUID, UUID god) {
        this.playerUUID = playerUUID;
        this.rank = BelieverRank.BELIEVER;
        this.happiness = 100;
        this.holiness = 0;
        this.playerPartner = null;
        this.god = god;
    }

    // Deserialization constructor
    public Believer deserialize(Map<String, Object> map) {
        this.playerUUID = UUID.fromString((String) map.get("uuid"));
        this.rank = BelieverRank.valueOf((String) map.get("rank"));
        this.holiness = (int) map.get("holiness");
        this.happiness = (int) map.get("happiness");
        this.playerPartner = UUID.fromString((String) map.get("partner"));
        this.god = UUID.fromString((String) map.get("god"));
        return this;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> mapSerializer = new HashMap<>();
        mapSerializer.put("uuid", this.playerUUID.toString());
        mapSerializer.put("priest", this.rank.name());
        mapSerializer.put("rank", this.rank);
        mapSerializer.put("holiness", this.holiness);
        mapSerializer.put("happiness", this.happiness);
        mapSerializer.put("partner", this.playerPartner.toString());
        mapSerializer.put("god", this.god.toString());
        return mapSerializer;
    }

    public UUID getGod() {
        return god;
    }

    public void setGod(UUID god) {
        this.god = god;
    }

    public boolean isLeader() {
        return this.rank == BelieverRank.LEADER;
    }

    public BelieverRank getRank() {
        return rank;
    }

    public void setRank(BelieverRank rank) {
        this.rank = rank;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public boolean isPriest() {
        return this.rank == BelieverRank.PRIEST;
    }

    public int getHappiness() {
        return happiness;
    }

    public void setHappiness(int happiness) {
        this.happiness = happiness;
    }

    public int getHoliness() {
        return holiness;
    }

    public void setHoliness(int holiness) {
        this.holiness = holiness;
    }

    public UUID getPlayerPartner() {
        return playerPartner;
    }

    public void setPlayerPartner(UUID playerPartner) {
        this.playerPartner = playerPartner;
    }
}
