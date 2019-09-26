package eu.cloudnetservice.sign.plugin.manager;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.MapWrapper;
import eu.cloudnetservice.sign.core.manager.SignManager;
import eu.cloudnetservice.sign.plugin.adapter.SignNetworkHandlerAdapter;
import eu.cloudnetservice.sign.plugin.hook.ThreadImpl;
import eu.cloudnetservice.sign.plugin.listener.SignListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitSignManager extends SignManager {

	public BukkitSignManager(JavaPlugin plugin) {
		super(new ThreadImpl(new SignNetworkHandlerAdapter(plugin)));
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
		CloudAPI.getInstance().getNetworkHandlerProvider().registerHandler(((ThreadImpl)getWorker()).getSignNetworkHandlerAdapter());
		getWorker().setDaemon(true);
		getWorker().start();

		Bukkit.getPluginManager().registerEvents(new SignListener(this),plugin);
	}

	@Override
	public void updateLayoutCall() {

	}
}
