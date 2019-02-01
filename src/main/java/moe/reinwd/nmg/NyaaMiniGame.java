package moe.reinwd.nmg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class NyaaMiniGame extends JavaPlugin {

    public static NyaaMiniGame plugin;
    public static Logger logger;
    CommandHandler handler;
    private boolean boatGameEnabled = false;
    private boolean isElytraEnabled = true;

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();
        if (Bukkit.class.getPackage().getImplementationVersion().startsWith("git-Bukkit-")){
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "======================================");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "NyaaMiniGame plugin require Spigot API, Please make sure you are using Spigot.");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "======================================");
        }
        try {
            Bukkit.spigot();
        }catch (NoSuchMethodError e){
            getCommand("nmg").setExecutor((sender, command, label, args) -> {
                sender.sendMessage(ChatColor.RED + "======================================");
                sender.sendMessage(ChatColor.RED + "NyaaMiniGame plugin require Spigot API, Please make sure you are using Spigot.");
                sender.sendMessage(ChatColor.RED + "======================================");
                return true;
            });
        }
        handler = new CommandHandler(plugin);
        getCommand("nmg").setExecutor(handler);
        getCommand("nmg").setExecutor(handler);
        EventListener listener = new EventListener(this);
        getServer().getPluginManager().registerEvents(listener , this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
//        getCommand("nmg").setExecutor(null);
//        getCommand("nmg").setTabCompleter(nutll);
//        this.getServer().getScheduler().cancelAllTasks();
    }

    public boolean getBoatGameEnabled() {
        return boatGameEnabled;
    }

    public void toggleBoatGame() {
        boatGameEnabled = !boatGameEnabled;
    }

    public boolean toggleElytra() {
        this.isElytraEnabled = !isElytraEnabled;
        return isElytraEnabled;
    }

    public boolean isElytraEnabled() {
        return isElytraEnabled;
    }
}
