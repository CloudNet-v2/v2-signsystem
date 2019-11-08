package eu.cloudnetservice.sign.core.packets;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.cloudnetservice.sign.core.manager.SignManager;
import eu.cloudnetservice.sign.core.models.Sign;
import eu.cloudnetservice.sign.core.models.SignLayoutConfig;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class PacketInSignSelector extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        Map<UUID, Sign> signMap = data.getObject("signs", TypeToken.getParameterized(Map.class, UUID.class, Sign.class).getType());
        SignLayoutConfig signLayoutConfig = data.getObject("signLayoutConfig", TypeToken.get(SignLayoutConfig.class).getType());

        Map<UUID, Sign> values = signMap.entrySet().stream().filter(uuidSignEntry -> uuidSignEntry.getValue().getPosition().getGroup().equals(
            CloudAPI.getInstance().getGroup())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (SignManager.getInstance() != null) {
            SignManager.getInstance().updateLayoutCall();
            SignManager.getInstance().setSignLayoutConfig(signLayoutConfig);
            Collection<UUID> collection = new HashSet<>();
            for (Sign sign : SignManager.getInstance().getSigns().values()) {
                if (!values.containsKey(sign.getUniqueId())) {
                    collection.add(sign.getUniqueId());
                }
            }
            for (UUID x : collection) {
                SignManager.getInstance().getSigns().remove(x);
            }
            for (Sign sign : values.values()) {
                if (!SignManager.getInstance().getSigns().containsKey(sign.getUniqueId())) {
                    SignManager.getInstance().getSigns().put(sign.getUniqueId(), sign);
                }
            }
        }
    }
}
