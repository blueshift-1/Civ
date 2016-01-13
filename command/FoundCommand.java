// File: FoundCommand.java
// Purpose: This class establishes the command that creates a settlement
 
package com.survivorserver.Civ.command;
 
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
 
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
 
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
 
import com.survivorserver.Civ.Civ;
import com.survivorserver.Civ.Settlement;
import com.survivorserver.Civ.lib.SubCommand;
 
public class FoundCommand extends SubCommand {
 
    public FoundCommand(Civ civ) {
        super(civ);
    }
 
    @Override
    public String getCommand() {
        return "found";
    }
 
    @Override
    public String[] getAliases() {
        return new String[] {"create","cr"};
    }
 
    @Override
    public String getPermissionNode() {
        return "civ.found";
    }
 
    @Override
    public String[] getHelp() {
        return new String[] {"&6found &f<&osettlement-name&r&f>&7 [Founds a settlement]"};
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
         
        // Creates a continuous string of the string name
        String townName = args[1];
        if (args.length > 2) {
            for (int i = 2; i < args.length; i++) {
                townName += " " + args[i];
            }
        }
         
        // Verifies name isn't taken
        if (!civ.isNameTaken(townName)) {
             
            // Verifies founder doesn't own a town
            Settlement ownedTowns = civ.getSettlementsFromOwner(sender.getName());
            if (ownedTowns == null) {
                 
                // Gathers players coordinates in order to link GP ID to civ
                Player player = (Player) sender;
                GriefPrevention gp = civ.getGp();
                Claim claim = gp.dataStore.getClaimAt(player.getLocation(), true, null);
                 
                // Verifies the player is in the claim
                if (claim != null) {
                     
                    // Verifies the claim doesn't already hold a settlement
                    if (civ.getSettlementFromClaim(claim.getID()) == null) {
                         
                        // Verifies the sender isn't already a member of another settlement
                        if (civ.getSettlementWithMember(sender.getName()) == null) {
                             
                            // Sets important information to the new thread
                            Settlement set = new Settlement();
                            set.setName(townName);
                            set.addClaim(claim.getID());
                            set.setFounder(player.getName());
                            set.setTheme("not set");
                            set.setSpawnMobs(true);
                             
                            List <String> members = new ArrayList<String>();
                            members.add(sender.getName());
                            set.setMembers(members);
                             
                            // Puts on track and saves
                            set.checkStatus();
                            civ.addSettlement(set);
                            try {
                                civ.saveTowns();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                             
                            // Notifies sender
                            sender.sendMessage(ChatColor.GOLD + "Your settlement has been created. Use /civ setThread <thread-id> to set your forum thread ID. After you set the thread ID, " + townName + " will need to be approved by a moderator.");
                        }
                        else {
                            sender.sendMessage(ChatColor.RED+"You cannot found a settlement if you are already a member of another");
                        }
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "You cannot found a settlement here. One already exists in this claim");
                    }
                } 
                else {
                    sender.sendMessage(ChatColor.RED + "You must be standing in a land claim to do this.");
                }
            } 
            else {
                String ownedName = ownedTowns.getName();
                sender.sendMessage(ChatColor.RED + "You can't be mayor of more than one settlement. You'll need to delete " + ownedName + " or transfer ownership to someone else.");
            }
        } 
        else {
            sender.sendMessage(ChatColor.RED + "A town by the name of " + townName + " already exists, try another. DOOM");
        }
        return true;
    }
}
