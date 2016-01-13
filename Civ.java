package com.survivorserver.Civ;
 
import java.io.BufferedReader;
import static java.lang.System.out;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
 
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;
 
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
 
import com.survivorserver.Civ.command.CivCommand;
 
public class Civ extends JavaPlugin implements Listener {
     
    private List<Settlement> towns;
    private File townsFile;
    private Gson gson;
    private GriefPrevention gp;
     
    public void onEnable() {
        towns = new ArrayList<Settlement>();
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        File dataFolder = getDataFolder();
        if (dataFolder.exists()) {
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                out.print("I'm trying to be caught - onenable");
                e.printStackTrace();
            }
        }
        townsFile = new File(dataFolder.getAbsolutePath() + File.separator + "towns.json");
        try {
            if (!townsFile.exists()) {
                townsFile.createNewFile();
                towns = new ArrayList<Settlement>();
                 
                saveTowns();
            } else {
                loadTowns();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        gp = GriefPrevention.instance;
        getCommand("civ").setExecutor(new CivCommand(this));
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new MobListener(this), this);
    }
     
    public void onDisable() {
        try {
            saveTowns();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
     
    public void saveTowns() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(townsFile));
        writer.write(gson.toJson(towns));
        writer.close();
    }
     
 
    public void loadTowns() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(townsFile));
        String t = "";
        String c;
        while ((c = reader.readLine()) != null) {
            t += c;
        }
        towns = gson.fromJson(t, new TypeToken<List<Settlement>>(){}.getType());
        for (int i = 0; i < towns.size(); i++) {
            if (towns.get(i) == null) {
                towns.remove(i);
            }
        }
        reader.close();
    }
     
    public GriefPrevention getGp() {
        return gp;
    }
     
    public boolean isNameTaken(String name) {
        for (Settlement town : towns) {
            if (town.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
     
    public Settlement getSettlementsFromOwner(String player) {
        for (Settlement set : towns) {
            if (set.getFounder().equals(player)) {
                return set;
            }
        }
        return null;
    }
     
    public void addSettlement(Settlement set) {
        towns.add(set);
    }
     
    public void removeSettlement(Settlement set){
        towns.remove(set);
    }
     
    public Settlement getSettlementFromClaim(Long id) {
        for (Settlement town : towns) {
            if (town.getClaimIds().contains(id)) {
                return town;
            }
        }
        return null;
    }
     
    public Settlement getSettlementFromName(String name) {
        for (Settlement town : towns) {
            if (town.getName().equalsIgnoreCase(name)) {
                return town;
            }
        }
        return null;
    }
     
    public String getTier(int tier) {
        switch(tier) {
            case 1:
                return "Village";
            case 2:
                return "Town";
            case 3:
                return "City";
            default:
                return "Settlement";
        }
    }
     
    public List<Settlement> getSettlements(){
        return towns;
    }
     
    public List<Settlement> getOfficialSettlements() {
        List<Settlement> set = new ArrayList<Settlement>();
        for (Settlement town : towns) {
            if (town.getTier() > 0) {
                set.add(town);
            }
        }
        return set;
    }
     
     
    public Settlement getSettlementWithMember(String name) {
        for (Settlement set : towns) {
            List <String> members = set.getMembers();
            for (int i=0;i<members.size();i++){
                if(name.equalsIgnoreCase(members.get(i))){
                    return set;
                }
            }
        }
        return null;
    }
     
    @EventHandler
    public void claimDeleteEvent(ClaimDeletedEvent event) {
        Long claim = event.getClaim().getID();
        Settlement set = getSettlementFromClaim(claim);
        if (set != null) {
            set.getClaimIds().remove(claim);
        }
    }
}
