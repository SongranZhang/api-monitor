package com.linkedkeeper;

import java.io.Serializable;

/**
 * Class used to track the number of active monitors (including global/primary/this).
 * It allows you to see how many monitors are concurrently running at any given time.
 */
public final class ActivityStats implements Serializable {

    final Counter thisActive;

    public ActivityStats(Counter thisActive) {
        this.thisActive = thisActive;
    }

}
