package eu.cloudnetservice.sign.core.packets;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.utility.document.Document;

public final class PacketOutAddSign extends Packet {
	public PacketOutAddSign(Sign sign) {
		super(PacketRC.SERVER_SELECTORS + 21, new Document("sign", sign));
	}
}
