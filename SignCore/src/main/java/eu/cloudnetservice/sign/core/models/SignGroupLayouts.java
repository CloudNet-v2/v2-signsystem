package eu.cloudnetservice.sign.core.models;

import java.util.Collection;

public final class SignGroupLayouts {

	private String name;

	private Collection<SignLayout> layouts;

	public SignGroupLayouts(String name, Collection<SignLayout> layouts) {
		this.name = name;
		this.layouts = layouts;
	}
	public String getName() {
		return name;
	}

	public Collection<SignLayout> getLayouts() {
		return layouts;
	}

}
