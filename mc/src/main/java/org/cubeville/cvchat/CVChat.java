package org.cubeville.cvchat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.cubeville.cvipc.CVIPC;
import org.cubeville.cvipc.IPCInterface;

public class CVChat extends JavaPlugin implements Listener, IPCInterface
{
    CVIPC ipc;
    
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        ipc = (CVIPC) pm.getPlugin("CVIPC");
        ipc.registerInterface("locchat", this);
        ipc.registerInterface("chatquery", this);
    }

    public void onDisable() {
        ipc.deregisterInterface("locchat");
        ipc.deregisterInterface("chatquery");
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        // String highestRank = "default";
        // Player player = event.getPlayer();
        // int prio = 0;
        // Set<String> ranks = getConfig().getConfigurationSection("ranks").getKeys(false);
        // for (String rank : ranks) {
        //     ConfigurationSection rankData = getConfig().getConfigurationSection("ranks").getConfigurationSection(rank);
        //     if (player.hasPermission(rankData.getString("permission")) && rankData.getInt("priority") > prio) {
        //         prio = rankData.getInt("priority");
        //         highestRank = rank;
        //     }
        // }
        // ScoreboardManager manager = Bukkit.getScoreboardManager();
        // Scoreboard mainBoard = manager.getMainScoreboard();
        // System.out.println("Set scoreboard team of player " + player.getName() + " to " + highestRank);
        // if(mainBoard.getTeam(highestRank) != null) {
        //     mainBoard.getTeam(highestRank).addEntry(player.getName().toString());
        // }
        // else {
        //     System.out.println("Team not found on scoreboard!");
        // }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("modreq")) {
            if(!(sender instanceof Player)) return false;
            Player player = (Player) sender;
            
            if(args.length == 0) {
                sender.sendMessage("/modreq <message>");
                return true;
            }
            
            String text = "";
            for(int i = 0; i < args.length; i++) {
                if(i > 0) text += " ";
                text += args[i];
            }

            Location loc = player.getLocation();
            ipc.sendMessage("modreq", player.getUniqueId() + "|" + loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "|" + text);
        }
        else if(command.getName().equals("unlocktutorialchat")) {
            if(!(sender instanceof Player)) return false;
            Player player = (Player) sender;
            ipc.sendMessage("unlocktutorialchat", player.getUniqueId().toString());
        }
        return false;
    }

    public void process(String channel, String message) {
        if(channel.equals("chatquery")) {
            StringTokenizer tk = new StringTokenizer(message, "|");
            if(tk.countTokens() != 4) return;
            String channelName = tk.nextToken();
            String mId = tk.nextToken();
            UUID playerId = UUID.fromString(tk.nextToken());
            String parameter = tk.nextToken();
            if(!parameter.equals("health")) return;

            Player player = getServer().getPlayer(playerId);
            if(player == null) return;

            double health = 20.0;
            if(player.getGameMode() == GameMode.SURVIVAL) health = player.getHealth();
            ipc.sendMessage("chatquery|" + channelName + "|" + mId + "|" + playerId.toString() + "|health=" + health);
        }
        else if(channel.equals("locchat")) {
            int idx = message.indexOf("|");
            if(idx == -1) return;
            
            String idList = message.substring(0, idx);
            int sidx = idList.indexOf(";");
            UUID playerId;
            Set<UUID> mutedIds = new HashSet<>();
            if(sidx == -1) {
                playerId = UUID.fromString(idList);
            }
            else {
                playerId = UUID.fromString(idList.substring(0, sidx));
                StringTokenizer tk = new StringTokenizer(idList.substring(sidx + 1), ",");
                while(tk.hasMoreTokens()) {
                    mutedIds.add(UUID.fromString(tk.nextToken()));
                }
            }
            message = message.substring(idx + 1);
            String greyMessage = "§7" + removeColorCodes(message);
            
            Player player = getServer().getPlayer(playerId);
            if(player == null) return;
            Location loc = player.getLocation();
            
            Collection<Player> players = (Collection<Player>) getServer().getOnlinePlayers();
            for(Player p: players) {
                Location pl = p.getLocation();
                if(pl.getWorld().getUID().equals(loc.getWorld().getUID()) && pl.distance(loc) < 55) {
                    p.sendMessage(message);
                }
                else {
                    if(p.hasPermission("cvchat.monitor.local") && false == mutedIds.contains(p.getUniqueId())) {
                        p.sendMessage(greyMessage);
                    }
                }
            }
        }
    }

    private static String[] colorCodes = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "k", "l", "m", "n", "o", "r"};
    public static String removeColorCodes(String text) {
        String ret = text;
        for(int i = 0; i < colorCodes.length; i++) {
            ret = ret.replace("§" + colorCodes[i], "");
        }
        return ret;
    }
}
