package eu.cloudnetservice.sign.core.models;

import java.util.Collection;

public final class SearchingAnimation {
    private int animations;

    private int animationsPerSecond;

    private Collection<SignLayout> searchingLayouts;

    public SearchingAnimation(int animations, int animationsPerSecond, Collection<SignLayout> searchingLayouts) {
        this.animations = animations;
        this.animationsPerSecond = animationsPerSecond;
        this.searchingLayouts = searchingLayouts;
    }

    public Collection<SignLayout> getSearchingLayouts() {
        return searchingLayouts;
    }

    public int getAnimations() {
        return animations;
    }

    public int getAnimationsPerSecond() {
        return animationsPerSecond;
    }
}
