package com.survivorserver.Civ.lib;
 
import org.bukkit.command.CommandSender;
 
import com.survivorserver.Civ.Civ;
 
public abstract class SubCommand {
 
    protected Civ civ;
     
    public SubCommand(Civ civ) {
        this.civ = civ;
    }
     
    public abstract String getCommand();
     
    public abstract String[] getAliases();
     
    public abstract String getPermissionNode();
     
    public abstract String[] getHelp();
     
    public abstract boolean allowConsoleSender();
     
    public abstract int getMinimumArgs();
     
    public abstract boolean onCommand(CommandSender sender, String[] args);
}
