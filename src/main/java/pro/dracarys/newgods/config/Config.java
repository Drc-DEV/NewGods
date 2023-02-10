package pro.dracarys.newgods.config;

import java.util.Arrays;
import java.util.List;

public enum Config {

    PERM_ADMIN("permissions.admin", "newgods.admin"),

    GOD_SPAWNWORLDS("gods.spawn.allowed-worlds", new String[]{
            "world",
            "world2"
    }),
    GOD_SETSPAWN_ONCREATE("gods.spawn.setspawn-on-creation", true),

    GOD_LIST_MAXPAGE("gods.list.max-per-page", 15),

    GOD_AUTODELETE("gods.auto-delete-when-no-followers", false),

    GOD_AUTORANK("gods.auto-set-leader-when-no-followers", false),

    GOD_PUNISHMENTS_ENABLED("gods.enable-punishments", true),
    GOD_NAME_BLACKLIST("gods.name-blacklist", new String[]{
            "null",
            "server",
            "fuckyou",
            "ass"
    }),
    GOD_TYPE_WHITELIST("gods.type-whitelist", new String[]{
            "WHITE", "GOLD", "LIGHT_PURPLE", "AQUA", "YELLOW",
            "GREEN", "RED", "GRAY", "DARK_AQUA", "DARK_PURPLE",
            "DARK_BLUE", "DARK_GREEN", "DARK_RED"
    }),
    GOD_PRAY_DELAY("gods.pray.delay-seconds", 300),
    GOD_PRAY_REWARD("gods.pray.reward-power", 10),
    GOD_PRAY_ALTAR_SIGN("gods.pray.altar-sign-prefix", "Altar of"),
    MARRIAGES_ENABLED("marriage.enabled", false),
    PREFIXES_ENABLED("prefixes.enabled", false),
    EXP_ENABLED("experience.enabled", true),
    EXP_BLESS_AMOUNT("experience.bless-amount", 200),
    HAPPINESS_DECREASE_AMOUNT("gods.happiness.decrease-amount-per-update", 5),
    HAPPNIESS_UPDATE_SPEED("gods.happiness.update-speed-minutes", 10),

    SACRIFICE_MOBS_SPEED("sacrifice.mobs-update-speed-minutes", 10),

    SACRIFICE_ITEMS_SPEED("sacrifice.items-update-speed-minutes", 15),
    SACRIFICE_ITEMS("sacrifice.items", new String[]{
            "APPLE,16,8",
            "BONE,25,7",
            "ROTTEN_FLESH,32,4",
            "POTATO,16,5",
            "DIRT,64,2",
            "INK_SAC,3,20"
    }),
    SACRIFICE_MOBS("sacrifice.mobs", new String[]{
            "COW,3,25",
            "PLAYER,1,100",
            "CHICKEN,10,10",
            "SHEEP,3,25",
            "ZOMBIE,3,25",
            "SLIME,1,100",
            "SQUID,1,50"
    }),

    DEBUG("Debug", false);

    String config;
    String message;
    Boolean option;
    String[] messages;
    Integer number;
    Double dnumber;

    Config(String config, String message) {
        this.config = config;
        this.message = message;
    }

    Config(String config, String[] messages) {
        this.config = config;
        this.messages = messages;
    }

    Config(String config, Boolean option) {
        this.config = config;
        this.option = option;
    }

    Config(String config, Integer number) {
        this.config = config;
        this.number = number;
    }

    Config(String config, Double dnumber) {
        this.config = config;
        this.dnumber = dnumber;
    }

    public boolean getOption() {
        return option;
    }

    public String getConfig() {
        return config;
    }

    public String getString() {
        return message;
    }

    public Double getDouble() {
        return dnumber;
    }

    public Integer getInt() {
        return number;
    }

    public String[] getStrings() {
        return this.messages;
    }

    public List<String> getStringList() {
        return Arrays.asList(this.messages);
    }

    public void setInt(int number) {
        this.number = number;
    }

    public void setDouble(double dnumber) {
        this.dnumber = dnumber;
    }

    public void setStrings(List<String> list) {
        this.messages = list.stream().toArray(String[]::new);
    }

    public void setString(String message) {
        this.message = message;
    }

    public void setOption(Boolean option) {
        this.option = option;
    }
}