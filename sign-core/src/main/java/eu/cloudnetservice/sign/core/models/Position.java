package eu.cloudnetservice.sign.core.models;

import java.util.Objects;

public final class Position {

    private final double x;
    private final double y;
    private final double z;
    private final String world;
    private final String group;

    public Position(double x, double y, double z, String world, String group) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.group = group;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getZ(), getWorld(), getGroup());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Position position = (Position) o;
        return Double.compare(position.getX(), getX()) == 0 &&
               Double.compare(position.getY(), getY()) == 0 &&
               Double.compare(position.getZ(), getZ()) == 0 &&
               Objects.equals(getWorld(), position.getWorld()) &&
               Objects.equals(getGroup(), position.getGroup());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public String getGroup() {
        return group;
    }
}
