package pro.dracarys.newgods.config;

import pro.dracarys.newgods.utils.Util;

import java.util.List;

public enum Message {

    PREFIX("Command.prefix", "&4&lGods &f➤ "),

    ERROR_NOPERM("General.no-permission", "&7[&4✕&7] &cYou lack the required permissions!"),
    ERROR_NOPLAYER("General.must-be-player", "&7[&4✕&7] &cYou must be a player!"),
    ERROR_NORANK("God.unknown-rank", "&7[&4✕&7] &cYou must provide a valid Rank name (believer, priest, leader)!"),

    ERROR_NULLPLAYER("General.no-player-provided", "&7[&4✕&7] &cYou must specify a valid player!"),
    GOD_UNHAPPY("God.unhappy", "&e{name} &cis unhappy with your lack of praying and sacrifices."),
    GOD_BLESSING("God.blessing-received", "&e{name} &ahas blessed you with &f{blessamount} &aenchanting experience."),
    GOD_TELEPORTING("God.teleporting", "&7Teleporting to your faiths spawn point..."),
    GOD_CREATEDALTAR("God.created-altar", "&aYou created a new Altar for your God."),
    GOD_WRONGALTAR("God.wrong-altar", "&7[&4✕&7] &cThis isn't an altar for your god. Type /gods leave to leave your religion."),
    GOD_WRONGGODALTAR("God.wrong-god-altar", "You can only make alters for your god."),
    GOD_CREATED("God.created", "&aCongratulations! You successfully created a new God."),
    GOD_DELETED("God.deleted", "&aCongratulations! You successfully deleted that God."),

    GOD_PRAYED("God.prayed", "&aYou prayed for {color}{name}&a... &7your god's strength increased by &e{gainedpower}."),

    RANK_LEADER("Ranks.LEADER", "Leader"),
    RANK_BELIEVER("Ranks.BELIEVER", "Believer"),
    RANK_PRIEST("Ranks.PRIEST", "Priest"),

    PREFIX_MARRIAGE("ChatPrefix.marriage", "&c" + '\u2764' + " &f"),
    PREFIX_LEADER("ChatPrefix.leader", "&e" + '\u2720' + " &f"),
    PREFIX_PRIEST("ChatPrefix.priest", "&7" + '\u2719' + " &f"),

    GENERIC_COOLDOWN("General.wait-x-seconds", "&cWait another &f{seconds} sec"),

    CMD_RANK_LEADER("Command.rank.you-are-leader", "&aYou are now the Leader of your Faith!"),
    CMD_SETSPAWN("Command.spawn.set", "&aReligion spawn set at your location!"),
    CMD_SETTYPE("Command.type.set", "&aGod Type changed successfully!"),
    ERROR_ONLYLEADER("God.leader-only", "&cSorry, but you must be the Leader of your religion!"),
    ERROR_BLACKLISTED("God.name-blacklisted", "&cSorry, but you cannot use that name."),
    ERROR_NOGOD("God.without-god", "&7[&4✕&7] &cYou're not in a religion."),
    ERROR_ALREADYEXISTS("God.already-exists", "&7[&4✕&7] &cA God with that name already exists, choose a different name."),
    ERROR_NONAME("God.provide-a-name", "&7[&4✕&7] &cYou should input the name of the God."),
    ERROR_NOTSAMEGOD("God.not-the-same-god", "&7[&4✕&7] &cThat player does not worship your same god."),
    ERROR_HASGOD("God.already-has-god", "&7[&4✕&7] &cYou already worship a god. Use /gods leave to abandon your current religion."),
    ERROR_GODNOTFOUND("God.no-god-by-this-name", "&7[&4✕&7] &cThere is no God with that name. Please provide a valid god name."),
    ERROR_TYPENOTFOUND("God.no-type-by-this-name", "&7[&4✕&7] &cThere is no God Type with that name. Please provide a valid god type from this list: {godTypes}"),

    SACRIFICE_NOTIF_MOBS("God.sacrifice.notification-mobs", "{color}{name}&a wishes you to sacrifice &f{sm-amount} &e{sm-type}&a with a sword in their name!"),

    SACRIFICE_NOTIF_ITEMS("God.sacrifice.notification-items", "{color}{name}&a wishes you to sacrifice &f{si-amount} &e{si-type}&a in their name!"),

    SACRIFICE_MOB("God.sacrifice.killed-mob", "&aSacrificed Mob &7[-{remaining}]"),
    SACRIFICE_MOB_END("God.sacrifice.completed-mob", "{color}{name}&a is pleased with your blood sacrifice! Your Faith gains strength as a result."),
    SACRIFICE_ITEM("God.sacrifice.burnt-item", "&aSacrificed Item &7[-{remaining}]"),
    SACRIFICE_ITEM_END("God.sacrifice.completed-item", "{color}{name}&a is pleased with your generosity! Your Faith gains strength as a result."),

    CMD_DELHOME("Command.home.home-deleted", "&aThe Spawn location of the Faith of {name} was deleted successfully!"),
    CMD_HOME_TP("Command.home.teleported", "&7Teleporting to your Faith Home..."),
    CMD_LEAVEGOD("God.left-religion", "You have abandoned your god. Your faith power is now 0."),
    CMD_JOINGOD("God.joined-religion", "&aYou have joined the faith of &e{name}&a."),

    CMD_MAIN_HEADER("Command.main-header", "&f«&m-------------&f»  &4&lGods &7{version}&f  «&m-------------&f»"),
    CMD_MAIN_FOOTER("Command.main-footer", " "),
    CMD_RELOAD("Command.reload-success", "&aConfig reloaded successfully"),
    CMD_LIST_FORMAT("Command.list.format", " &e{color}{name} &7» &f{power} - {followers} - {leadername}"),
    CMD_LIST_HEADER("Command.list.header", "&f«&m-------------&f»  &4&lGods List&7 [&a{page}&8/&2{nextPage}&7]&f  «&m-------------&f»"),
    CMD_LIST_FOOTER("Command.list.footer", "&7Use /gods list {nextPage} to browse the next page"),

    CMD_INFO_FORMAT("Command.info.format", new String[]{
            "&f«&m-------------&f»  &4&lFaith of {color}{god}&f  «&m-------------&f»\"",
            "&eLeader: &f{leadername} &7| &cPower: &f{power} &7| &eTotal Followers: &f{followers}",
            "&bPriests &7[&e{priests}&7]: &f{priestsnames}",
            "&fBelievers &7[&e{believers}&7]: &7{believersnames}"
    }),

    CMD_HELP_ADMIN("Command.help.admin-commands", new String[]{
            "/gods type <type> <godname>",
            "/gods rank <player> <rank> <godname>",
            "/gods delhome <godname>",
            "/gods create <godname>",
            "/gods delete <godname>",
            "/gods home <godname>",
            "/gods leave <player>",
            "/gods join <player>"
    }),
    CMD_HELP_LEADER("Command.help.leader-commands", new String[]{
            "&6===Leader Commands===", "/gods rank <player> <rank>",
            "/gods type <type>"}),
    CMD_HELP_PRIEST("Command.help.priest-commands", new String[]{
            "===Priest Commands===", "/gods sethome", "/gods delhome"}),
    CMD_HELP_MARRIAGE("Command.help.marriage-commands", new String[]{
            "===Marriage Commands===",
            "/gods marry <player>", "/gods divorce"}),
    CMD_HELP_GENERAL("Command.help.general-commands", new String[]{
            "===Regular Commands===", "/gods",
            "/gods list", "/gods info [godname]", "/gods leave", "/gods home",
            "To create a new god place a sign and write 'god' on the first line and your gods name on the second line."
    }),
    CMD_HELP_FORMAT("Command.help-format", " &e/gods &6%args% &7» &f%desc%");
    String config, message;
    String[] messages;

    Message(String config, String message) {
        this.config = config;
        this.message = message;
    }

    Message(String config, String[] messages) {
        this.config = config;
        this.messages = messages;
    }

    public String getConfig() {
        return config;
    }

    public String getMessage() {
        return message;
    }

    public String[] getMessages() {
        return this.messages;
    }

    public void setMessages(List<String> list) {
        this.messages = Util.color(list).toArray(new String[0]);
    }

    public void setMessage(String message) {
        this.message = Util.color(message);
    }

}