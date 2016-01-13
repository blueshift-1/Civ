// File: Settlement.java
// Purpose: This class defines the private members and accessors/mutators of the overall
//          civilization. Basically what it is and what can be seen about it and also what
//          can be changed by callers.
 
package com.survivorserver.Civ;
 
import java.util.ArrayList;
import java.util.List;
 
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;
import org.bukkit.entity.Player;
 
public class Settlement {
 
     
    // ******************** CONFIGURABLE OPTIONS ******************** //
     
    // Minimum sizes of the types of civilizations
    private int village = 3;
    private int town = 6;
    private int city = 12;
     
    // Minimum number of staff member for approval to that tier
    private int villageApprove = 1;
    private int townApprove = 2;
    private int cityApprove = 3;
     
    // ****************** END CONFIGURABLE OPTIONS ****************** //
     
    @Expose
    private boolean spawnMobs = true;
    @Expose
    private String name;
    @Expose
    private List<Long> claims;
    @Expose
    private String theme;
     
    // Key for status meanings
    // 0 = Not Eligible for Promotion
    // 1 = Eligible for Promotion
    // 2 = Eligible for Promotion but was denied Review
    // 3 = Under Review
    @Expose
    private int status = 0;
    @Expose
    private int threadId = 0;
    @Expose
    private int tier = 0;
    @Expose
    private String founder;
    @Expose
    private List<String> members;
    @Expose
    private List<String> approvingMods;
    @Expose
    private List<String> disapprovingMods;
    @Expose
    private String motd;
     
    public Settlement() {
        if (claims == null) {
            claims = new ArrayList<Long>();
        }
        if (members == null) {
            members = new ArrayList<String>();
        }
        if (approvingMods == null) {
            approvingMods = new ArrayList<String>();
        }
        if (disapprovingMods == null) {
            disapprovingMods = new ArrayList<String>();
        }
    }
     
    public void setName(String name) {
        this.name = name;
    }
     
    public String getName() {
        return name;
    }
     
    public void setClaims(List<Long> claims) {
        this.claims = claims;
    }
     
    public boolean addClaim(Long claimId) {
        return claims.contains(claimId) ? false : claims.add(claimId);
    }
     
    public List<Long> getClaimIds() {
        return claims;
    }
 
     
    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }
     
    public int getThreadId() {
        return threadId;
    }
     
    public void setTier(int tier) {
        this.tier = tier;
    }
     
    public void nextTier (){
        if (tier < 3){
            this.tier++;
        }
    }
     
    public void dropTier (){
        if (tier > 0){
            this.tier--;
        }
    }
     
    public int getTier() {
        return tier;
    }
    public String getTierStr() {
        if (tier==0) {
            return "Unofficial Settlement";
        }
        else if (tier == 1){
            return "Village";
        }
        else if (tier == 2){
            return "Town";
        }
        else if (tier == 3){
            return "City";
        }
        return "ERROR: Tier indicator out of bounds";
    }
    public String getTierStr(int tier) {
        if (tier==0) {
            return "Unofficial Settlement";
        }
        else if (tier == 1){
            return "Village";
        }
        else if (tier == 2){
            return "Town";
        }
        else if (tier == 3){
            return "City";
        }
        return "ERROR: Tier indicator out of bounds";
    }
     
    public void setFounder(String founder) {
        this.founder = founder;                                                                                                                 
    }
     
    public String getFounder() {
        return founder;
    }
     
    public void setMembers(List<String> members) {
        this.members = members;
    }
     
    public List<String> getMembers() {
        return members;
    }
     
    public void setApprovingMods(List<String> approvingMods) {
        this.approvingMods = approvingMods;
    }
     
    public List<String> getApprovingMods() {
        return approvingMods;
    }
     
    public void setDisapprovingMods(List<String> disapprovingMods) {
        this.disapprovingMods = disapprovingMods;
    }
     
    public List<String> getDisapprovingMods() {
        return disapprovingMods;
    }
     
    public String getMotd() {
        return motd;
    }
     
    public void setMotd(String motd) {
        this.motd = motd;
    }
     
    public boolean addMember(String name) {
        return members.contains(name) ? false : members.add(name);
    }
     
    public void addApproval(String name) {
        if (!approvingMods.contains(name)){
            approvingMods.add(name);
             
            int num = approvingMods.size();
             
            if (tier == 0) {
                if (num >= villageApprove){
                    promoteProcedure();
                }
            }
            else if (tier == 1) {
                if (num >= townApprove){
                    promoteProcedure();
                }
            }
            else if (tier == 2) {
                if (num >= cityApprove){
                    promoteProcedure();
                }
            }
        }
    }
    public void addDisapproval(String name) {
        if (!disapprovingMods.contains(name)){
            disapprovingMods.add(name);
             
            int num = disapprovingMods.size();
             
            if (num > 1) {
                status = 2;
                resetApproval();
                resetDisapproval();
            }
        }
    }
    public void resetApproval (){
        approvingMods.clear();
    }
    public void resetDisapproval () {
        disapprovingMods.clear();
    }   
    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus (){
        return status;
    }
    public String getStatusStr(){
        if (status == 0){
            return "Not eligible for promotion";
        }
         
        else if (status == 1){
            return "Eligible for promotion";
        }
         
        else if (status == 2){
            return "Eligible for promotion, but was denied during last promotion attempt";
        }
         
        else if (status == 3){
            return "Under review for tier promotion";       
        }
        return "CIV ERROR: Problem in determining promotion track. Unknown town status. DOOM";
    }
    public void checkStatus(){
        int size = members.size();
         
        // For settlements that are currently unable to have promotional status
        if (status == 0) {
            if (tier==0){
                if (size >= village){
                    status = 1;
                }
            }
            else if (tier == 1){
                if (size >= town){
                    status = 1;
                }
            }
            else if (tier == 2){
                if (size >= city){
                    status = 1;
                }
            }
        }
         
        if (tier == 1) {
            if (size < village) {
                demoteProcedure();
            }           
        }
        else if (tier == 2) {
            if (size < town) {
                demoteProcedure();
            }
        }
        else if (tier == 3) {
            if (size < city) {
                demoteProcedure();
            }
        }   
    }
     
    public void demoteProcedure(){
        tier--;
        status = 0;
        resetApproval();
        resetDisapproval();
         
        Player[] onlinePlayers = Bukkit.getOnlinePlayers();
        for (int i=0; i<onlinePlayers.length; i++){
            if (onlinePlayers[i].getName().equalsIgnoreCase(founder)){
                onlinePlayers[i].sendMessage(ChatColor.GOLD + "Your town has been demoted to " + getTierStr());
            }
        }
         
        ////////////////////////////////
        //  Handle Demotion of Mayor  //
        ////////////////////////////////
         
        if (tier == 1) {
            try {
                com.survivorserver.Dasfaust.Bridge.Bridge bridge = (com.survivorserver.Dasfaust.Bridge.Bridge) Bukkit.getServer().getPluginManager().getPlugin("Bridge");
                // 20 is 'mayor'
                bridge.removeGroup(founder, 20);
                Player player = Bukkit.getServer().getPlayer(founder);
                if (player != null) {
                    bridge.syncTest(player);
                    player.sendMessage(ChatColor.GREEN + "You have been demoted from Mayor.");
                }
            } catch(Exception e) {
                Bukkit.getServer().getPluginManager().getPlugin("Civ").getLogger().severe("Bridge not found, can't set rank");
            }
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "acb " + founder + " -" + ((int) Math.pow(256, 2)));
        } else if (tier == 2) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "acb " + founder + " -" + ((int) Math.pow(256, 2)));
        }
         
        // Rechecks to see if the demote procedure should be executed another time
        checkStatus();
    }
     
    public void promoteProcedure(){
        tier++;
        status = 0;
        resetApproval();
        resetDisapproval();
         
        Player[] onlinePlayers = Bukkit.getOnlinePlayers();
        for (int i=0; i<onlinePlayers.length; i++){
            if (onlinePlayers[i].getName().equalsIgnoreCase(founder)){
                onlinePlayers[i].sendMessage(ChatColor.GOLD + "Your town has been promoted to " + getTierStr());
            }
        }
         
        /////////////////////////////////
        //  Handle Promotion of Mayor  //
        /////////////////////////////////
         
        if (tier == 2) {
            try {
                com.survivorserver.Dasfaust.Bridge.Bridge bridge = (com.survivorserver.Dasfaust.Bridge.Bridge) Bukkit.getServer().getPluginManager().getPlugin("Bridge");
                // 20 is 'mayor'
                bridge.addGroup(founder, 20);
                Player player = Bukkit.getServer().getPlayer(founder);
                if (player != null) {
                    bridge.syncTest(player);
                    player.sendMessage(ChatColor.GREEN + "You have been promoted to Mayor! You also have additional claimblocks to use.");
                }
            } catch(Exception e) {
                Bukkit.getServer().getPluginManager().getPlugin("Civ").getLogger().severe("Bridge not found, can't set rank");
            }
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "acb " + founder + " " + ((int) Math.pow(256, 2)));
        } else if (tier == 3) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "acb " + founder + " " + ((int) Math.pow(256, 2)));
            Player player = Bukkit.getServer().getPlayer(founder);
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "You now have additional claimblocks to use.");
            }
        }
         
        checkStatus();
    }
     
    public void setTheme(String theme){
        this.theme = theme;
    }
     
    public String getTheme(){
        return theme;
    }
     
    public void toggleMobs(){
        if (spawnMobs){
            spawnMobs = false;
        }
        else {
            spawnMobs = true;
        }
    }
     
    public void setSpawnMobs(boolean spawnMobs){
        this.spawnMobs = spawnMobs;
    }
     
    public boolean getSpawnMobs(){
        return spawnMobs;
    }
}
