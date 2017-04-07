package org.cubeville.cvchat.textcommands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class TextCommandManager
{
    List<TextCommand> textCommands;
    Map<UUID, Set<String>> playerUsage;
    
    public TextCommandManager(Configuration config) {
        Collection<String> commands = config.getKeys();
        textCommands = new ArrayList<>();
        for(String key: commands) {
            Configuration c = (Configuration) config.get(key);
            Set<String> aliases = new HashSet<String>(c.getStringList("commands"));
            TextCommand tc = new TextCommand(aliases, c.getStringList("text"));
            textCommands.add(tc);
        }
    }

    public boolean executeTextCommand(CommandSender sender, String command) {
        command = command.substring(1);

        if(sender instanceof ProxiedPlayer) {
            UUID playerId = ((ProxiedPlayer) sender).getUniqueId();
            if(!playerUsage.containsKey(playerId)) {
                playerUsage.put(playerId, new HashSet<>());
            }
            playerUsage.get(playerId).add(command);
        }
        
        for(TextCommand textCommand: textCommands) {
            if(textCommand.matches(command)) {
                for(String s: textCommand.getText()) {
                    sender.sendMessage(s);
                }
                return true;
            }
        }
        return false;
    }
}
