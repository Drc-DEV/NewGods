package pro.dracarys.newgods.config.file;

import net.md_5.bungee.api.ChatColor;
import pro.dracarys.configlib.config.CustomFile;
import pro.dracarys.newgods.config.Message;
import pro.dracarys.newgods.utils.Util;

public class MessageFile extends CustomFile {

    public MessageFile() {
        super("");
        for (Message message : Message.values()) {
            if (message.getMessages() != null) {
                for (String string : message.getMessages()) {
                    ChatColor.translateAlternateColorCodes('&', string);
                    getConfig().addDefault(message.getConfig(), message.getMessages());
                }
            } else {
                getConfig().addDefault(message.getConfig(), message.getMessage());
            }
        }
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public MessageFile init() {
        reloadConfig();
        for (Message message : Message.values()) {
            if (message.getMessages() == null) {
                message.setMessage(getConfig().getString(Util.color(message.getConfig())));
            } else {
                message.setMessages(getConfig().getStringList(message.getConfig()));
            }
        }
        return this;
    }

    @Override
    public String getName() {
        return "messages";
    }
}

