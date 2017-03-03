package org.cubeville.cvchat;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import org.cubeville.cvchat.channels.Channel;
import org.cubeville.cvchat.channels.ChannelManager;
import org.cubeville.cvchat.channels.GroupChannel;

import org.cubeville.cvchat.commands.ChannelCommand;
import org.cubeville.cvchat.commands.ChatCommand;
import org.cubeville.cvchat.commands.GroupCommand;
import org.cubeville.cvchat.commands.HelpCommand;
import org.cubeville.cvchat.commands.MsgCommand;
import org.cubeville.cvchat.commands.RCommand;
import org.cubeville.cvchat.commands.RsCommand;
import org.cubeville.cvchat.commands.TestCommand;

import org.cubeville.cvchat.postfix.PostfixManager;

import org.cubeville.cvchat.log.Logger;

// TODO: Muting players, muting complete chat except staff
// TODO: Private messaging with afk function :)
// TODO: hidden staff

public class CVChat extends Plugin {

    private ChannelManager channelManager;
    private Logger logger;
    
    private static CVChat instance;
    public static CVChat getInstance() {
        return instance;
    }
    
    @Override
    public void onEnable() {
        instance = this;

        logger = new Logger(new File(getDataFolder(), "logs"));
        ProxyServer.getInstance().getScheduler().schedule(this, logger, 2000, 2000, TimeUnit.MILLISECONDS);

        // Create player data directory
        File statusFolder = new File(getDataFolder(), "status");
        statusFolder.mkdirs();

        PluginManager pm = getProxy().getPluginManager();

        // Load configuration (must exist for the plugin to work properly)
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

            // Initialize channel manager from configuration
            Configuration channelList = (Configuration) config.get("channels");
            channelManager = new ChannelManager(channelList, statusFolder);

            // Add commands and listeners
            for(Channel channel: channelManager.getChannels()) {
                for(String command: channel.getCommands()) {
                    pm.registerCommand(this, new ChatCommand(command, channel));
                }
            }

            GroupChannel group = channelManager.getGroupChannel();
            if(group != null) {
                pm.registerCommand(this, new GroupCommand(group));
            }

            Channel local = channelManager.getLocalChannel();
            if(local != null) {
                pm.registerListener(this, new ChatListener(local));
            }

            pm.registerListener(this, new LoginListener(channelManager));
            pm.registerCommand(this, new ChannelCommand(channelManager));

            // Load postfix configuration
            Configuration postfixList = (Configuration) config.get("postfix");
            PostfixManager postfixManager = new PostfixManager(postfixList);
        }
        catch (IOException e) {
            System.out.println("Fatal error: CVChat config file not found or readable!");
        }

        // Initialize private message chat commands
        pm.registerCommand(this, new MsgCommand());
        pm.registerCommand(this, new RCommand());
        pm.registerCommand(this, new RsCommand());

        pm.registerCommand(this, new TestCommand());
    }

}