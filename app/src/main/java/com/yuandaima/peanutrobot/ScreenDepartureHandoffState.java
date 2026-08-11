package com.yuandaima.peanutrobot;

import com.keenon.sdk.component.navigation.route.RouteNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns the route and generation for one charger-to-navigation handoff.
 * Mutations are serialized on the main thread; callback threads only capture
 * the published generation before posting their work to the main thread.
 */
final class ScreenDepartureHandoffState {
    static final long NO_GENERATION = 0L;

    private long nextGeneration = NO_GENERATION;
    private volatile long currentGeneration = NO_GENERATION;
    private List<RouteNode> currentRouteSnapshot;

    long begin(List<RouteNode> routeSnapshot) {
        nextGeneration++;
        currentRouteSnapshot = new ArrayList<>(routeSnapshot);
        currentGeneration = nextGeneration;
        return currentGeneration;
    }

    long captureCurrentGeneration() {
        return currentGeneration;
    }

    List<RouteNode> consumeRoute(long generation) {
        if (!isCurrent(generation)) {
            return null;
        }
        List<RouteNode> routeSnapshot = currentRouteSnapshot;
        clearCurrent();
        return routeSnapshot;
    }

    boolean clearIfCurrent(long generation) {
        if (!isCurrent(generation)) {
            return false;
        }
        clearCurrent();
        return true;
    }

    long cancelCurrent() {
        long canceledGeneration = currentGeneration;
        if (canceledGeneration != NO_GENERATION) {
            clearCurrent();
        }
        return canceledGeneration;
    }

    private boolean isCurrent(long generation) {
        return generation != NO_GENERATION
                && generation == currentGeneration
                && currentRouteSnapshot != null;
    }

    private void clearCurrent() {
        currentRouteSnapshot = null;
        currentGeneration = NO_GENERATION;
    }
}
