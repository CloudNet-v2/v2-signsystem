package eu.cloudnetservice.sign.module;

import de.dytanic.cloudnet.event.IEventListener;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnetcore.api.CoreModule;
import de.dytanic.cloudnetcore.api.event.network.ChannelInitEvent;
import de.dytanic.cloudnetcore.api.event.network.UpdateAllEvent;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import eu.cloudnetservice.sign.module.config.ConfigSignLayout;
import eu.cloudnetservice.sign.module.database.SignDatabase;
import eu.cloudnetservice.sign.module.packet.in.PacketInAddSign;
import eu.cloudnetservice.sign.module.packet.in.PacketInRemoveSign;
import eu.cloudnetservice.sign.module.packet.out.PacketOutSignSelector;

public class SignModule extends CoreModule implements IEventListener<UpdateAllEvent> {
    private static SignModule instance;
    private ConfigSignLayout configSignLayout;
    private SignDatabase signDatabase;

    public static SignModule getInstance() {
        return instance;
    }

    public ConfigSignLayout getConfigSignLayout() {
        return configSignLayout;
    }

    public SignDatabase getSignDatabase() {
        return signDatabase;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onBootstrap() {
        configSignLayout = new ConfigSignLayout();
        configSignLayout.loadLayout();
        signDatabase = new SignDatabase(getCloud().getDatabaseManager().getDatabase("cloud_internal_cfg"));

        if (getCloud().getPacketManager().buildHandlers(PacketRC.SERVER_SELECTORS + 21).size() == 0) {
            getCloud().getPacketManager().registerHandler(PacketRC.SERVER_SELECTORS + 21, PacketInAddSign.class);
        }
        if (getCloud().getPacketManager().buildHandlers(PacketRC.SERVER_SELECTORS + 22).size() == 0) {
            getCloud().getPacketManager().registerHandler(PacketRC.SERVER_SELECTORS + 22, PacketInRemoveSign.class);
        }

        getCloud().getEventManager().registerListener(this, this);
        getCloud().getEventManager().registerListener(this, new ListenerImpl());
    }

    @Override
    public void onCall(UpdateAllEvent event) {
        if (event.isOnlineCloudNetworkUpdate()) {
            event.getNetworkManager().sendToLobbys(new PacketOutSignSelector(signDatabase.loadAll(), configSignLayout.loadLayout()));
        }
    }

    private class ListenerImpl implements IEventListener<ChannelInitEvent> {

        @Override
        public void onCall(ChannelInitEvent event) {
            if (event.getINetworkComponent() instanceof Wrapper) {
                return;
            }

            if (event.getINetworkComponent() instanceof MinecraftServer && (((MinecraftServer) event.getINetworkComponent()).getGroupMode()
                                                                                                                            .equals(
                                                                                                                                ServerGroupMode.LOBBY) || ((MinecraftServer) event
                .getINetworkComponent()).getGroupMode().equals(ServerGroupMode.STATIC_LOBBY))) {
                event.getINetworkComponent().sendPacket(new PacketOutSignSelector(signDatabase.loadAll(), configSignLayout.loadLayout()));
            }
        }
    }
}
