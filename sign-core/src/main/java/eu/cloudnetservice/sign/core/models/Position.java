package eu.cloudnetservice.sign.core.models;

import java.util.Objects;

public final class Position {

	private final double x,y,z;
	private final String world,group;

	public Position(double x, double y, double z, String world, String group) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.group = group;
	}

	public String getGroup() {
		return group;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Position position = (Position) o;
		return Double.compare(position.getX(), getX()) == 0 &&
				Double.compare(position.getY(), getY()) == 0 &&
				Double.compare(position.getZ(), getZ()) == 0 &&
				getWorld().equals(position.getWorld()) &&
				getGroup().equals(position.getGroup());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getX(), getY(), getZ(), getWorld(), getGroup());
	}
}
