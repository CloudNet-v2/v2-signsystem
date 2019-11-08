package eu.cloudnetservice.sign.plugin.event;

import de.dytanic.cloudnet.bridge.event.bukkit.BukkitCloudEvent;
import eu.cloudnetservice.sign.core.models.SignLayoutConfig;
import org.bukkit.event.HandlerList;

public class BukkitUpdateSignLayoutsEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private SignLayoutConfig signLayoutConfig;

    public BukkitUpdateSignLayoutsEvent(SignLayoutConfig signLayoutConfig) {
        this.signLayoutConfig = signLayoutConfig;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public SignLayoutConfig getSignLayoutConfig() {
        return signLayoutConfig;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
