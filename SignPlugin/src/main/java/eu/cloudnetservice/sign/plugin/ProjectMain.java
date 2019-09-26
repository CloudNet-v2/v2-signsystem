package eu.cloudnetservice.sign.plugin;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.config.CloudConfigLoader;
import de.dytanic.cloudnet.api.config.ConfigTypeLoader;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.sign.core.manager.SignManager;
import eu.cloudnetservice.sign.core.packets.PacketInSignSelector;
import eu.cloudnetservice.sign.plugin.hook.ServerCancelTask;
import eu.cloudnetservice.sign.plugin.manager.BukkitSignManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Paths;

public final class ProjectMain extends JavaPlugin {

	private BukkitSignManager signManager;

	@Override
	public void onLoad() {
		System.out.println("Load cloud and connection config");
		CloudAPI cloudAPI = new CloudAPI(new CloudConfigLoader(Paths.get("CLOUD/connection.json"),
				Paths.get("CLOUD/config.json"),
				ConfigTypeLoader.INTERNAL), new ServerCancelTask(this));
		System.out.println("Initiate bukkit sign manager");

		this.signManager = new BukkitSignManager(this);
		System.out.println("Register sign system packets");
		cloudAPI.getNetworkConnection().getPacketManager().registerHandler(PacketRC.SERVER_SELECTORS + 21, PacketInSignSelector.class);
		System.out.println("Register logger");
		cloudAPI.setLogger(getLogger());
	}

	@Override
	public void onEnable() {
		CloudAPI.getInstance().bootstrap();
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
