package org.cubeville.cvchat;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import org.cubeville.cvchat.commands.BroadcastCommand;
import org.cubeville.cvchat.commands.ChannelCommand;
import org.cubeville.cvchat.commands.ChatCommand;
import org.cubeville.cvchat.commands.HelpCommand;
import org.cubeville.cvchat.commands.MsgCommand;
import org.cubeville.cvchat.commands.RCommand;
import org.cubeville.cvchat.commands.RsCommand;

// TODO: Muting players, muting complete chat except staff
// TODO: Private messaging with afk function :)

public class CVChat extends Plugin {

    Map<String, Channel> channels;

    @Override
    public void onEnable() {
        // Create player data directory
        File statusFolder = new File(getDataFolder(), "status");
        statusFolder.mkdirs();

        // Create channel map
        channels = new HashMap<>();

        // Load configuration (must exist for the plugin to work properly)
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            Collection<String> channelNames = config.getKeys();
            for(String channelName: channelNames) {
                Configuration channelConfig = (Configuration) config.get(channelName);
                Channel channel = new Channel(channelName,
                                              channelConfig.getString("permission"),
                                              channelConfig.getString("sendpermission"),
                                              channelConfig.getString("colorpermission"),
                                              channelConfig.getString("leavepermission"),
                                              channelConfig.getString("format"),
                                              channelConfig.getBoolean("default"),
                                              channelConfig.getBoolean("autojoin"),
                                              channelConfig.getBoolean("listable"));
                channels.put(channelName, channel);
                List<String> commands = (List<String>) channelConfig.getList("commands");
                for(String command: commands) {
                    getProxy().getPluginManager().registerCommand(this, new ChatCommand(command, channel));
                }
            }

            if(channels.containsKey("local")) {
                getProxy().getPluginManager().registerListener(this, new ChatListener(channels.get("local")));
            }

            getProxy().getPluginManager().registerListener(this, new LoginListener(channels.values()));
            getProxy().getPluginManager().registerCommand(this, new ChannelCommand(channels));
        }
        catch (IOException e) {
            System.out.println("Fatal error: CVChat config file not found or readable!");
        }

        // Initialize private message chat commands
        getProxy().getPluginManager().registerCommand(this, new MsgCommand());
        getProxy().getPluginManager().registerCommand(this, new RCommand());
        getProxy().getPluginManager().registerCommand(this, new RsCommand());

    }

}
