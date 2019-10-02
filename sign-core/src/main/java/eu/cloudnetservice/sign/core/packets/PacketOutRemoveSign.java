package eu.cloudnetservice.sign.core.packets;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.cloudnetservice.sign.core.models.Sign;

public final class PacketOutRemoveSign extends Packet {
	public PacketOutRemoveSign(Sign sign) {
		super(PacketRC.SERVER_SELECTORS + 22, new Document("sign", sign));
	}
}
