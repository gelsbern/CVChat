package org.cubeville.cvchat;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import org.cubeville.cvipc.CVIPC;

import org.cubeville.cvchat.channels.Channel;
import org.cubeville.cvchat.channels.ChannelManager;
import org.cubeville.cvchat.channels.GroupChannel;
import org.cubeville.cvchat.channels.LocalChannel;

import org.cubeville.cvchat.commands.BanCommand;
import org.cubeville.cvchat.commands.ChannelCommand;
import org.cubeville.cvchat.commands.ChatCommand;
import org.cubeville.cvchat.commands.CheckbanCommand;
import org.cubeville.cvchat.commands.CheckCommand;
import org.cubeville.cvchat.commands.DibsCommand;
import org.cubeville.cvchat.commands.DoneCommand;
import org.cubeville.cvchat.commands.FinishCommand;
import org.cubeville.cvchat.commands.FjCommand;
import org.cubeville.cvchat.commands.ForwardCommand;
import org.cubeville.cvchat.commands.FqCommand;
import org.cubeville.cvchat.commands.GroupCommand;
import org.cubeville.cvchat.commands.HoldCommand;
import org.cubeville.cvchat.commands.KickCommand;
import org.cubeville.cvchat.commands.LocalCommand;
import org.cubeville.cvchat.commands.ModlistCommand;
import org.cubeville.cvchat.commands.MsgCommand;
import org.cubeville.cvchat.commands.MuteCommand;
import org.cubeville.cvchat.commands.NoteCommand;
import org.cubeville.cvchat.commands.PrefixCommand;
import org.cubeville.cvchat.commands.ProfileCommand;
import org.cubeville.cvchat.commands.RCommand;
import org.cubeville.cvchat.commands.ReopenCommand;
import org.cubeville.cvchat.commands.RlCommand;
import org.cubeville.cvchat.commands.TempbanCommand;
import org.cubeville.cvchat.commands.TestCommand;
import org.cubeville.cvchat.commands.TpidCommand;
import org.cubeville.cvchat.commands.UnbanCommand;
import org.cubeville.cvchat.commands.UnclaimCommand;
import org.cubeville.cvchat.commands.UnmuteCommand;
import org.cubeville.cvchat.commands.WhoCommand;

import org.cubeville.cvchat.log.Logger;

import org.cubeville.cvchat.ranks.RankManager;
import org.cubeville.cvchat.sanctions.SanctionManager;
import org.cubeville.cvchat.playerdata.PlayerDataManager;
import org.cubeville.cvchat.playerdata.PlayerDataDao;
import org.cubeville.cvchat.playerdata.ProfilesDao;
import org.cubeville.cvchat.textcommands.TextCommandManager;
import org.cubeville.cvchat.tickets.TicketManager;
import org.cubeville.cvchat.tickets.TicketDao;

// TODO: Muting players, muting complete chat except staff
// TODO: Private messaging with afk function :)
// TODO: hidden staff

public class CVChat extends Plugin {

    private ChannelManager channelManager;
    private SanctionManager sanctionManager;
    private TicketManager ticketManager;
    private PlayerDataManager playerDataManager;

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

        CVIPC ipc = (CVIPC) pm.getPlugin("CVIPC");
        
        // Load configuration (must exist for the plugin to work properly)
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

            // Start auto messager
            AutoMessager messager = new AutoMessager(120, config.getStringList("automessager"), this);

            { // Install ticket system
                Configuration ticketDaoConfig = (Configuration) config.get("tickets");
                if(ticketDaoConfig != null) {
                    TicketDao ticketDao = new TicketDao(ticketDaoConfig.getString("db_user"),
                                                        ticketDaoConfig.getString("db_password"),
                                                        ticketDaoConfig.getString("db_database"));
                    ticketManager = new TicketManager(this, ipc, ticketDao);
                    pm.registerCommand(this, new CheckCommand(ticketManager));
                    pm.registerCommand(this, new DibsCommand(ticketManager));
                    pm.registerCommand(this, new DoneCommand(ticketManager));
                    pm.registerCommand(this, new HoldCommand(ticketManager));
                    pm.registerCommand(this, new ReopenCommand(ticketManager));
                    pm.registerCommand(this, new TpidCommand(ticketManager));
                    pm.registerCommand(this, new UnclaimCommand(ticketManager));
                }
                else {
                    System.out.println("No ticket dao configuration found. Ticket system not available.");
                }
            }
            
            // Initialize channel manager from configuration
            Configuration channelList = (Configuration) config.get("channels");
            channelManager = new ChannelManager(channelList, statusFolder, ipc);

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

            LocalChannel local = channelManager.getLocalChannel();
            Set<String> commandWhitelist = new HashSet<String>(((Configuration)config.get("whitelist")).getStringList("standard"));
            Set<String> commandWhitelistTutorial = new HashSet<String>(((Configuration)config.get("whitelist")).getStringList("tutorial"));
            Configuration textCommandConfig = (Configuration)config.get("textcommands");
            TextCommandManager textCommandManager = new TextCommandManager(textCommandConfig);
            ChatListener chatListener = new ChatListener(local, commandWhitelist, commandWhitelistTutorial, textCommandManager, ipc);
            pm.registerListener(this, chatListener);
            
            pm.registerListener(this, new LoginListener(channelManager, ticketManager));
            pm.registerCommand(this, new ChannelCommand(channelManager));
            
            pm.registerCommand(this, new FinishCommand(ipc, textCommandManager));
            
            // Load ranks configuration
            Configuration ranksList = (Configuration) config.get("ranks");
            Configuration prefixesList = (Configuration) config.get("prefixes");
            RankManager rankManager = new RankManager(ranksList, prefixesList);
            
            // Load swear filter words 
            sanctionManager = new SanctionManager(config.getStringList("filter"));

            {
                // Initialize private message chat commands
                pm.registerCommand(this, new MsgCommand());
                pm.registerCommand(this, new RCommand());
                pm.registerCommand(this, new RlCommand());
                
                // Sanction commands
                pm.registerCommand(this, new KickCommand());
                pm.registerCommand(this, new MuteCommand());
                pm.registerCommand(this, new UnmuteCommand());
                pm.registerCommand(this, new BanCommand());
                pm.registerCommand(this, new TempbanCommand());
                pm.registerCommand(this, new UnbanCommand());
                pm.registerCommand(this, new CheckbanCommand());
                
                // Player list commands
                pm.registerCommand(this, new WhoCommand());
                pm.registerCommand(this, new ModlistCommand());
                
                // Little helper command, remove when done! TODO
                pm.registerCommand(this, new TestCommand());

                // other commands
                pm.registerCommand(this, new PrefixCommand());
                pm.registerCommand(this, new LocalCommand(local));
            }

            { // Install playerdata system
                Configuration playerDataDaoConfig = (Configuration) config.get("playerdata");
                if(playerDataDaoConfig != null) {
                    PlayerDataDao playerDataDao = new PlayerDataDao(playerDataDaoConfig.getString("db_user"),
                                                                    playerDataDaoConfig.getString("db_password"),
                                                                    playerDataDaoConfig.getString("db_database"));
                    playerDataManager = new PlayerDataManager(playerDataDao);

                    ProfilesDao profilesDao = new ProfilesDao(playerDataDaoConfig.getString("db_user"),
                                                              playerDataDaoConfig.getString("db_password"),
                                                              playerDataDaoConfig.getString("db_database"));

                    pm.registerCommand(this, new ProfileCommand(this));
                    pm.registerCommand(this, new NoteCommand(this));
                }
                else {
                    System.out.println("No playerdata dao configuration found.");
                }
            }

            { // Other commands
                pm.registerCommand(this, new FjCommand());
                pm.registerCommand(this, new FqCommand());
            }
            
            { // Chat forward commands for quest
                for(int i = 0; i < 10; i++) {
                    pm.registerCommand(this, new ForwardCommand(String.valueOf(i), String.valueOf(i)));
                }
            }
        }

        catch (IOException e) {
            System.out.println("Fatal error: CVChat config file not found or readable!");
            throw new RuntimeException("CVChat initialization failed!");
        }

    }
}
