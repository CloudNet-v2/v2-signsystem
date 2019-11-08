package eu.cloudnetservice.sign.core.manager;

import eu.cloudnetservice.sign.core.models.Sign;
import eu.cloudnetservice.sign.core.models.SignLayoutConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class SignManager {
    protected static SignManager instance;

    private final Map<UUID, Sign> signMap = new HashMap<>();
    private final Runnable worker;
    private SignLayoutConfig signLayoutConfig;

    public SignManager(Runnable worker) {
        this.worker = worker;
        instance = this;
    }

    public static SignManager getInstance() {
        return instance;
    }

    public abstract void updateLayoutCall();

    public Runnable getWorker() {
        return worker;
    }

    public SignLayoutConfig getSignLayoutConfig() {
        return signLayoutConfig;
    }

    public void setSignLayoutConfig(SignLayoutConfig signLayoutConfig) {
        this.signLayoutConfig = signLayoutConfig;
    }

    public Map<UUID, Sign> getSigns() {
        return signMap;
    }
}
