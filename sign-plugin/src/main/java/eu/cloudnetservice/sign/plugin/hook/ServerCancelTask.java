package eu.cloudnetservice.sign.plugin.hook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServerCancelTask implements Runnable {

    private final JavaPlugin plugin;

    public ServerCancelTask(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void run() {
        Bukkit.getServer().getPluginManager().disablePlugin(this.plugin);
        Bukkit.shutdown();
    }
}
