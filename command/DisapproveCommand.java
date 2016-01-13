package com.survivorserver.Civ.command;
 
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
 
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
 
import com.survivorserver.Civ.Civ;
import com.survivorserver.Civ.Settlement;
import com.survivorserver.Civ.lib.SubCommand;
 
import java.io.IOException;
 
public class DisapproveCommand extends SubCommand{
    public DisapproveCommand(Civ civ){
        super(civ);
    }
 
    @Override
    public String getCommand() {
        return "disapprove";
    }
 
    @Override
    public String[] getAliases() {
        return null;
    }
 
    @Override
    public String getPermissionNode() {
        return "civ.disapprove";
    }
 
    @Override
    public String[] getHelp() {
        return new String[] {"&6disapprove &f(&osettlement-name&r)&7 [Disapproves a settlement for tier promotion]"};
    }
 
    @Override
    public boolean allowConsoleSender() {
        return false;
    }
 
    @Override
    public int getMinimumArgs() {
        return 0;
    }
 
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 1){
            Player player = (Player) sender;
            GriefPrevention gp = civ.getGp();
            Claim claim = gp.dataStore.getClaimAt(player.getLocation(), true, null);
            if (claim != null) {
                Settlement set = civ.getSettlementFromClaim(claim.getID());
                if (set !=null){
                    if(set.getStatus()==3){
                        set.addDisapproval(sender.getName());
                        sender.sendMessage(ChatColor.GOLD + "You have disapproved the promotion of "+set.getName());
                        try {
                            civ.saveTowns();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        sender.sendMessage(ChatColor.RED+"The settlement must be under review to do this");
                    }
                }
                else{
                    sender.sendMessage(ChatColor.RED+"You must be standing in the claim of a settlement to do this");
                }
            }
            else{
                sender.sendMessage(ChatColor.RED+"You must be standing in the claim to do this");
            }
        }
        else{
            String townName = args[1];
            if (args.length > 2) {
                for (int i = 2; i < args.length; i++) {
                    townName += " " + args[i];
                }
            }
            Settlement set =civ.getSettlementFromName(townName);
            if (set !=null){
                if(set.getStatus()==3){
                    set.addDisapproval(sender.getName());
                    sender.sendMessage(ChatColor.GOLD + "You have disapproved the promotion of "+set.getName());
                    try {
                        civ.saveTowns();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED+"The settlement must be under review to do this");
                }
            }
            else{
                sender.sendMessage(ChatColor.RED+"You must enter a valid settlement name to do this");
            }
        }
             
        return true;
    }
}
