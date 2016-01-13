// File: ApproveCommand.java
// Purpose: This class for the Approve civilizations on the tier promotion track.
//          Once promoted: mod approve/disapprovals are reset, and and a tier status check is preformed.
 
package com.survivorserver.Civ.command;
 
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
 
import java.io.IOException;
 
import com.survivorserver.Civ.Civ;
import com.survivorserver.Civ.Settlement;
import com.survivorserver.Civ.lib.SubCommand;
 
public class ApproveCommand extends SubCommand {
 
    public ApproveCommand(Civ civ) {
        super(civ);
    }
 
    @Override
    public String getCommand() {
        return "approve";
    }
 
    @Override
    public String[] getAliases() {
        return new String[] {"a"};
    }
 
    @Override
    public String getPermissionNode() { 
        return "civ.approve";
    }
 
    @Override
    public String[] getHelp() {
        return new String[] {"&6approve &f(&osettlement-name&r&f) &7[Approves a settlement]"};
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
        String townName = args[1];
        if (args.length > 2) {
            for (int i = 2; i < args.length; i++) {
                townName += " " + args[i];
            }
        }
        Settlement set = civ.getSettlementFromName(townName);
        if (set !=null) {
            if (set.getStatus()==3){
                sender.sendMessage(ChatColor.GOLD + "You have given your approval for the promotion of " + set.getName());
                set.addApproval(sender.getName());
                 
                try{
                    civ.saveTowns();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
            else if(set.getStatus() == 0){
                sender.sendMessage(ChatColor.RED + "This settlement is not elligible for promotion yet.");
            }
            else if(set.getStatus() == 1){
                sender.sendMessage(ChatColor.RED + "This settlement is elligible for promotion, but has not been put on track by the Owner");
            }
            else if (set.getStatus() == 2){
                sender.sendMessage(ChatColor.RED + "This settlement is elligible for promotion (though has been denied), but has not been put back on promotion track by the Owner");               
            }
            else{
                sender.sendMessage(ChatColor.YELLOW + "CIV ERROR: Problem in determining promotion track. Status out of bounds. DOOM");             
            }
        }
        else sender.sendMessage(ChatColor.RED + "There is no settlement by the name of " + townName);
         
        return true;
    }
}
