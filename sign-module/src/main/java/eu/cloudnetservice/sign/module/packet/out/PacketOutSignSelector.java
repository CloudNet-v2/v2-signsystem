/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package eu.cloudnetservice.sign.module.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.cloudnetservice.sign.core.models.Sign;
import eu.cloudnetservice.sign.core.models.SignLayoutConfig;

import java.util.Map;
import java.util.UUID;

public class PacketOutSignSelector extends Packet {

    public PacketOutSignSelector(Map<UUID, Sign> signMap, SignLayoutConfig signLayoutConfig) {
        super(PacketRC.SERVER_SELECTORS + 21, new Document("signs", signMap).append("signLayoutConfig", signLayoutConfig));
    }
}
