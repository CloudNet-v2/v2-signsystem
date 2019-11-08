package eu.cloudnetservice.sign.core.models;

import de.dytanic.cloudnet.lib.server.info.ServerInfo;

import java.util.Objects;
import java.util.UUID;

public final class Sign {
    private final UUID uniqueId;
    private final String targetGroup;
    private final Position position;

    private volatile ServerInfo serverInfo;

    public Sign(String targetGroup, Position signPosition) {
        this.uniqueId = UUID.randomUUID();
        this.targetGroup = targetGroup;
        this.position = signPosition;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUniqueId(), getTargetGroup(), getPosition(), getServerInfo());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sign sign = (Sign) o;
        return Objects.equals(getUniqueId(), sign.getUniqueId()) &&
               Objects.equals(getTargetGroup(), sign.getTargetGroup()) &&
               Objects.equals(getPosition(), sign.getPosition()) &&
               Objects.equals(getServerInfo(), sign.getServerInfo());
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getTargetGroup() {
        return targetGroup;
    }

    public Position getPosition() {
        return position;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }
}
