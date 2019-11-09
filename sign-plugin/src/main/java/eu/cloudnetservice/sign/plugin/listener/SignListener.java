package eu.cloudnetservice.sign.plugin.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import eu.cloudnetservice.sign.core.manager.SignManager;
import eu.cloudnetservice.sign.core.models.Sign;
import eu.cloudnetservice.sign.plugin.adapter.SignNetworkHandlerAdapter;
import eu.cloudnetservice.sign.plugin.hook.ThreadImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener {

    private final SignManager signManager;

    public SignListener(SignManager signManager) {
        this.signManager = signManager;
    }


    @EventHandler
    public void handleInteract(PlayerInteractEvent e) {
        if ((e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && e.getClickedBlock() != null && e.getClickedBlock()
                                                                                                .getState() instanceof org.bukkit.block.Sign) {
            if (SignNetworkHandlerAdapter.containsPosition(e.getClickedBlock().getLocation())) {
                Sign sign = SignNetworkHandlerAdapter.getSignByPosition(e.getClickedBlock().getLocation());
                if (sign.getServerInfo() != null) {
                    String s = sign.getServerInfo().getServiceId().getServerId();
                    ByteArrayDataOutput output = ByteStreams.newDataOutput();
                    output.writeUTF("Connect");
                    output.writeUTF(s);
                    e.getPlayer().sendPluginMessage(((ThreadImpl) signManager.getWorker()).getSignNetworkHandlerAdapter().getPlugin(),
                        "BungeeCord",
                        output.toByteArray());
                }
            }
        }
    }
}
