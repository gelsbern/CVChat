package org.cubeville.cvchat.channels;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.CommandSender;

import org.cubeville.cvchat.Util;
import org.cubeville.cvchat.ranks.RankManager;
import org.cubeville.cvchat.sanctions.SanctionManager;
import org.cubeville.cvchat.playerdata.PlayerDataManager;

public class Channel
{
    protected String name;
    private String viewPermission;
    private String sendPermission;
    private String colorPermission;
    private String leavePermission;
    private String format;
    private boolean isDefault; // Channel is activated for new players
    private boolean autojoin; // Players join automatically when they log in (otherwise serialized)
    private boolean listable; // Shows up in /ch list
    private boolean filtered;
    private Collection<String> commands;
    
    private Set<UUID> members;

    private Map<Integer, String> messageQueue;
    private Integer messageQueueId;

    public Channel(String name, String viewPermission, String sendPermission, String colorPermission, String leavePermission, String format, boolean isDefault, boolean autojoin, boolean listable, boolean filtered, Collection<String> commands) {
        this.name = name;
        this.viewPermission = viewPermission;
        this.sendPermission = sendPermission;
        this.colorPermission = colorPermission;
        this.leavePermission = leavePermission;
        this.format = format;
        this.isDefault = isDefault;
        this.autojoin = autojoin;
        this.listable = listable;
        this.filtered = filtered;
        this.commands = commands;

        members = new HashSet<>();

        messageQueue = new HashMap<>();
        messageQueueId = 0;

    }

    public void playerLogin(ProxiedPlayer player, String configuration) {
        if (viewPermission.equals("default") || player.hasPermission(viewPermission)) {
            if(autojoin || (configuration == null && isDefault)) {
                members.add(player.getUniqueId());
            }
            else if (configuration != null && Util.getBooleanProperty(configuration)) {
                members.add(player.getUniqueId());
            }
        }
    }

    public String getConfigurationString(ProxiedPlayer player) {
        if(autojoin == false) {
            if(members.contains(player.getUniqueId())) return name + ":" + true;
            return name + ":" + false;
        }
        return null;
    }
    
    public void playerDisconnect(ProxiedPlayer player) {
        members.remove(player.getUniqueId());
    }
    
    public void sendMessage(CommandSender player, String message) {
        if(SanctionManager.getInstance().isPlayerMuted(player)) {
            player.sendMessage("§cYou are muted.");
            return;
        }
        
        if(((!sendPermission.equals("default")) && player.hasPermission(sendPermission) == false) || ((!viewPermission.equals("default")) && player.hasPermission(viewPermission) == false)) {
            player.sendMessage("§cPermission denied.");
            return;
        }
        if(player instanceof ProxiedPlayer) {
            if(!members.contains(((ProxiedPlayer) player).getUniqueId())) {
                player.sendMessage("§cYou're currently not a member of this channel. Join with /ch join " + name + ".");
                return;
            }
        }

        if(filtered && (player instanceof ProxiedPlayer)) {
            ProxiedPlayer pp = (ProxiedPlayer) player;
            if(SanctionManager.getInstance().checkFilter(message)) {
                String fm = message; //SanctionManager.getInstance().getFilterHighlight();
                if(player.hasPermission("cvchat.nofilterkick")) {
                    player.sendMessage("§cMessage filtered for swearing!");
                }
                else {
                    pp.disconnect("§cKicked for swearing:\n" + fm);
                    for(ProxiedPlayer p: ProxyServer.getInstance().getPlayers()) {
                        p.sendMessage("§e" + pp.getDisplayName() + "§c got kicked for swearing.");
                        if(p.hasPermission("cvchat.showfiltercause")) p.sendMessage("§cMessage: §r" + fm);
                    }
                }
                return;
            }
            if(SanctionManager.getInstance().checkCaps(message)) {
                if(!player.hasPermission("cvchat.bypasscapsfilter")) {
                    player.sendMessage("§cPlease turn off caps lock or use less caps, message filtered.");
                    return;
                }
            }
            if(SanctionManager.getInstance().checkSpam(message)) {
                if(!player.hasPermission("cvchat.bypasscapsfilter")) {
                    player.sendMessage("§cPlease avoid repetition of the same character or character sequence, message filtered.");
                    return;
                }
            }
        }
        
        String formattedMessage = format;

        if(formattedMessage.indexOf("%prefix%") >= 0 && player instanceof ProxiedPlayer) {
            formattedMessage = formattedMessage.replace("%prefix%", PlayerDataManager.getInstance().getPrefix(((ProxiedPlayer) player).getUniqueId()));
        }
        if(formattedMessage.indexOf("%postfix%") >= 0) {
            if(player instanceof ProxiedPlayer) {
                String postfix = RankManager.getInstance().getPostfix(player);
                formattedMessage = formattedMessage.replace("%postfix%", postfix);
            }
            else {
                formattedMessage = formattedMessage.replace("%postfix%", "(§dCO§f)");
            }
        }
        if(player instanceof ProxiedPlayer) {
            formattedMessage = formattedMessage.replace("%player%", ((ProxiedPlayer) player).getDisplayName());
        }
        else {
            formattedMessage = formattedMessage.replace("%player%", "Console");
        }

        message.replace("§", "");
        if(colorPermission.equals("default") || player.hasPermission(colorPermission)) {
            message = Util.translateAlternateColorCodes(message);
        }
        formattedMessage = formattedMessage.replace("%message%", message);

        if(formattedMessage.indexOf("%health%") >= 0) {
            if(player instanceof ProxiedPlayer) {
                ProxiedPlayer p = (ProxiedPlayer) player;
                messageQueueId++;
                messageQueue.put(messageQueueId, p.getUniqueId().toString() + "|" + formattedMessage);
                ChannelManager.getInstance().getIPC()
                    .sendMessage(p.getServer().getInfo().getName(),
                                 "chatquery" + "|" + name + "|" + messageQueueId + "|"
                                 + p.getUniqueId().toString() + "|health");
            }
            else {
                formattedMessage = formattedMessage.replace("%health%", "[§8||||||||||§r]");
                doSendMessage(player, formattedMessage);
            }
        }
        else {
            doSendMessage(player, formattedMessage);
        }
    }

    protected void processIpcQuery(String message) {
        StringTokenizer tk = new StringTokenizer(message, "|");
        if(tk.countTokens() != 4) return;
        tk.nextToken();
        Integer mId = Integer.valueOf(tk.nextToken());
        if(!messageQueue.containsKey(mId)) return;
        String playerId = tk.nextToken();
        String values = tk.nextToken();
        if(!values.startsWith("health=")) return;
        Double healthd = Double.valueOf(values.substring(values.indexOf("=") + 1)) / 2.0;
        int health = healthd.intValue();
        if(health < 0) health = 0;
        if(health > 10) health = 10;
        String healthBar = health <= 3 ? "§4" : (health <= 8 ? "§e" : "§2");
        healthBar += repeatString("|", health) + "§8" + repeatString("|", 10 - health);
        String chatMessage = messageQueue.get(mId);
        if(!chatMessage.startsWith(playerId + "|")) return;
        chatMessage = chatMessage.substring(chatMessage.indexOf("|") + 1);
        chatMessage = chatMessage.replace("%health%", healthBar);
        CommandSender sender = ProxyServer.getInstance().getPlayer(UUID.fromString(playerId));
        doSendMessage(sender, chatMessage);
    }

    private String repeatString(String s, int count) {
        String ret = "";
        for(int i = 0; i < count; i++) ret += s;
        return ret;
    }
    
    protected void doSendMessage(CommandSender sender, String formattedMessage) {
        Collection<ProxiedPlayer> recipientList = getRecipientList(sender);
        if(recipientList == null) {
            sendFailureMessage(sender);
            return;
        }
        for(ProxiedPlayer p: recipientList) {
            if(members.contains(p.getUniqueId())) {
                p.sendMessage(formattedMessage);
            }
        }
    }
    
    protected Collection<ProxiedPlayer> getRecipientList(CommandSender player) {
        return ProxyServer.getInstance().getPlayers();
    }

    protected void sendFailureMessage(CommandSender player) {
        player.sendMessage("§cNobody hears your message.");
    }
    
    private boolean hasViewPermission(ProxiedPlayer player) {
        if(viewPermission.equals("default")) return true;
        return player.hasPermission(viewPermission);
    }
    
    public boolean canList(ProxiedPlayer player) {
        if(!listable) return false;
        return hasViewPermission(player);
    }
    
    public boolean join(ProxiedPlayer player) {
        if(members.contains(player.getUniqueId())) {
            player.sendMessage("§cYou are already in that channel.");
        }
        else if(hasViewPermission(player)) {
            members.add(player.getUniqueId());
            player.sendMessage("§aYou have joined channel '" + name + "'.");
            return true;
        }
        else {
            player.sendMessage("§cYou do not have permission to join that channel.");
        }
        return false;
    }

    public boolean leave(ProxiedPlayer player) {
        if(!members.contains(player.getUniqueId())) {
            player.sendMessage("§cYou are not in that channel.");
        }
        else if(leavePermission.equals("default") || player.hasPermission(leavePermission)) {
            members.remove(player.getUniqueId());
            player.sendMessage("§aYou have left channel '" + name + "'.");
            return true;
        }
        else {
            player.sendMessage("§cYou can't leave this channel.");
        }
        return false;
    }
    
    public boolean isListable() {
        return listable;
    }

    public Collection<String> getCommands() {
        return commands;
    }
}
