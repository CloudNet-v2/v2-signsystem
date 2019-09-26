package eu.cloudnetservice.sign.plugin;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.sign.core.manager.SignManager;
import eu.cloudnetservice.sign.core.packets.PacketInSignSelector;
import eu.cloudnetservice.sign.plugin.commands.SignsCommand;
import eu.cloudnetservice.sign.plugin.manager.BukkitSignManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ProjectMain extends JavaPlugin {

	private BukkitSignManager signManager;

	@Override
	public void onEnable() {
		System.out.println("Initiate bukkit sign manager");
		this.signManager = new BukkitSignManager(this);
		System.out.println("Register sign system packets");
		CloudAPI.getInstance().getNetworkConnection().getPacketManager().registerHandler(PacketRC.SERVER_SELECTORS + 21, PacketInSignSelector.class);
		SignsCommand command = new SignsCommand();

		getCommand("signs").setExecutor(command);
		getCommand("signs").setPermission("cloudnet.command.signs");
		getCommand("signs").setTabCompleter(command);
	}

	@Override
	public void onDisable() {
		if (CloudAPI.getInstance() != null) {
			CloudAPI.getInstance().shutdown();
		}
		getServer().getMessenger().unregisterOutgoingPluginChannel(this);
		if (SignManager.getInstance() != null && SignManager.getInstance().getWorker() != null) {
			SignManager.getInstance().getWorker().stop();
		}
	}
}
