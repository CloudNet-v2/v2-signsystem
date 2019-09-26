/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package eu.cloudnetservice.sign.module.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import eu.cloudnetservice.sign.core.models.Sign;
import eu.cloudnetservice.sign.module.ProjectMain;

/**
 * Created by Tareko on 22.08.2017.
 */
public class PacketInAddSign extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        Sign sign = data.getObject("sign", TypeToken.get(Sign.class).getType());
        ProjectMain.getInstance().getSignDatabase().appendSign(sign);

        CloudNet.getInstance().getNetworkManager().reload();
        CloudNet.getInstance().getNetworkManager().updateAll();
    }
}
