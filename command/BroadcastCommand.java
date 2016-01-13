// File: BroadcastCommand.java
// Purpose: To distribute a message to all online members of a settlement
 
package com.survivorserver.Civ.command;
 
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
 
import com.survivorserver.Civ.Civ;
import com.survivorserver.Civ.Settlement;
import com.survivorserver.Civ.lib.SubCommand;
 
 
public class BroadcastCommand extends SubCommand {
    public BroadcastCommand(Civ civ){
        super(civ);
    }
 
    @Override
    public String getCommand() {
        return "broadcast";
    }
 
    @Override
    public String[] getAliases() {
        return new String[] {"call","talk","tell"};
    }
 
    @Override
    public String getPermissionNode() {
        return "civ.broadcast";
    }
 
    @Override
    public String[] getHelp() {
        return new String[] {"&6broadcast &f<&lmessage&r> &7[Broadcasts a message to all settlement memebers online]"};
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
        Settlement set = civ.getSettlementsFromOwner(sender.getName());
         
        if (set != null){
            String message = ChatColor.GOLD + "[" + set.getName() + "'s Broadcast] " + ChatColor.WHITE; 
            message += args[1];
            if (args.length > 2){
                for (int i = 2; i<args.length; i++){
                    message += " " + args[i];
                }
            }
             
            Player[] onlinePlayers = civ.getServer().getOnlinePlayers();
            for (int i = 0; i<onlinePlayers.length; i++){
                for (int j=0; j<set.getMembers().size(); j++){
                    if (onlinePlayers[i].getName().equalsIgnoreCase(set.getMembers().get(j))){
                        onlinePlayers[i].sendMessage(message);
                    }
                }
            }
        }
        else {
            sender.sendMessage(ChatColor.RED + "You must be a civilization owner to broadcast a message");
        }
        return true;
    }
 
}
