// File: AddMemberCommand.java
// Purpose: This is an override command to automatically add a player to civilization without their consent.
 
package com.survivorserver.Civ.command;
 
import java.io.IOException;
 
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
 
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
 
import com.survivorserver.Civ.Civ;
import com.survivorserver.Civ.Settlement;
import com.survivorserver.Civ.lib.SubCommand;
 
public class AddMemberCommand extends SubCommand {
 
    public AddMemberCommand(Civ civ) {
        super(civ);
    }
 
    @Override
    public String getCommand() {
        return "addmember";
    }
 
    @Override
    public String[] getAliases() {
        return new String[] {"add","addnoble"};
    }
 
    @Override
    public String getPermissionNode() {
        return "civ.addmember";
    }
 
    @Override
    public String[] getHelp() {
        return new String[] {"&6addmember &f<&omember-name&r&f> (&osettlement-name&r&f) &7[Adds a member to a settlement]"};
    }
 
    @Override
    public boolean allowConsoleSender() {
        return false;
    }
 
    @Override
    public int getMinimumArgs() {
        return 1;
    }
 
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        // Gets player name and verifies it
        String memberName = args[1];
        OfflinePlayer off = civ.getServer().getOfflinePlayer(memberName);
        if (off.hasPlayedBefore()) {
            memberName = off.getName();
             
            // Creates List of Towns and determines if a player is part of one
            if (civ.getSettlementWithMember(memberName) == null) {
                                     
                // If the a settlement named is provided, the settlement is obtained from it.
                if (args.length >= 3) {
                    String townName = args[2];
                    if (args.length > 3) {
                        for (int i = 3; i < args.length; i++) {
                            townName += " " + args[i];
                        }
                    }
                    Settlement set = civ.getSettlementFromName(townName);
                    if (set != null) {
                        if (set.getFounder().equalsIgnoreCase(sender.getName())
                                || sender.hasPermission("civ.bypass")) {
                            if (set.addMember(memberName)) {
                                set.checkStatus();
                                try {
                                    civ.saveTowns();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                sender.sendMessage(ChatColor.GOLD + memberName + " has been added to " + set.getName() + ".");
                                 
                                // Sends player notification of their addition
                                Player[] onlinePlayers = civ.getServer().getOnlinePlayers();
                                for (int i=0; i<onlinePlayers.length; i++){
                                    if (memberName.equalsIgnoreCase(onlinePlayers[i].getName())){
                                        onlinePlayers[i].sendMessage(ChatColor.GOLD + "You have been added to the civilization: " + set.getName());
                                    }
                                }
                                 
                            } else {
                                sender.sendMessage(ChatColor.RED + memberName + " is already a member of " + set.getName() + ".");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You do not have permission to do this.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "There is no settlement by the name of " + townName);
                    }
                } else {
                    Player player = (Player) sender;
                    GriefPrevention gp = civ.getGp();
                    Claim claim = gp.dataStore.getClaimAt(player.getLocation(), true, null);
                    if (claim != null) {
                        Settlement set = civ.getSettlementFromClaim(claim.getID());
                        if (set != null) {
                            if (set.addMember(memberName)) {
                                set.checkStatus();
                                try {
                                    civ.saveTowns();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                 
                                // Sends player notification of their addition
                                Player[] onlinePlayers = civ.getServer().getOnlinePlayers();
                                for (int i=0; i<onlinePlayers.length; i++){
                                    if (memberName.equalsIgnoreCase(onlinePlayers[i].getName())){
                                        onlinePlayers[i].sendMessage(ChatColor.GOLD + "You have been added to the civilization: " + set.getName());
                                    }
                                }
                                sender.sendMessage(ChatColor.GOLD + memberName + " has been added to " + set.getName() + ".");
                            } else {
                                sender.sendMessage(ChatColor.RED + memberName + " is already a member of " + set.getName() + ".");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "This claim is not part of a settlement");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You need to be standing in a land claim to do this");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + memberName + " already belongs to another settlement");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "There is no player by the name of " + memberName + ".");
        }
        return true;
    }
 
}
