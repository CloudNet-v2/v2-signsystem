package eu.cloudnetservice.sign.plugin;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.sign.core.packets.PacketInSignSelector;
import eu.cloudnetservice.sign.plugin.commands.SignsCommand;
import eu.cloudnetservice.sign.plugin.manager.BukkitSignManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SignPlugin extends JavaPlugin {

    private BukkitSignManager signManager;

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        getLogger().config("Initiate bukkit sign manager");
        this.signManager = new BukkitSignManager(this);
        getLogger().config("Register sign system packets");
        CloudAPI.getInstance().getNetworkConnection().getPacketManager().registerHandler(PacketRC.SERVER_SELECTORS + 21,
            PacketInSignSelector.class);
        SignsCommand command = new SignsCommand();

        getCommand("signs").setExecutor(command);
        getCommand("signs").setPermission("cloudnet.command.signs");
        getCommand("signs").setTabCompleter(command);
    }
}
