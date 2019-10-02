package eu.cloudnetservice.sign.core.manager;

import eu.cloudnetservice.sign.core.models.Sign;
import eu.cloudnetservice.sign.core.models.SignLayoutConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class SignManager {
	protected static SignManager instance;

	private final Map<UUID, Sign> signMap = new HashMap<>();
	private SignLayoutConfig signLayoutConfig;
	private final Thread worker;

	public SignManager(Thread worker) {
		this.worker = worker;
		instance = this;
	}

	public abstract void updateLayoutCall();

	public void setSignLayoutConfig(SignLayoutConfig signLayoutConfig) {
		this.signLayoutConfig = signLayoutConfig;
	}

	public Thread getWorker() {
		return worker;
	}

	public SignLayoutConfig getSignLayoutConfig() {
		return signLayoutConfig;
	}

	public Map<UUID, Sign> getSigns() {
		return signMap;
	}

	public static SignManager getInstance() {
		return instance;
	}
}
