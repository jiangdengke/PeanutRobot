package com.yuandaima.peanutrobot;

import com.keenon.sdk.component.navigation.route.RouteNode;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ScreenDepartureHandoffStateTest {

    @Test
    public void repeatedReleaseConsumesCurrentRouteOnlyOnce() {
        ScreenDepartureHandoffState handoffState = new ScreenDepartureHandoffState();
        RouteNode routeNode = createRouteNode(101, "Point A");
        long handoffGeneration = handoffState.begin(Collections.singletonList(routeNode));

        List<RouteNode> firstConsumption = handoffState.consumeRoute(handoffGeneration);
        List<RouteNode> repeatedConsumption = handoffState.consumeRoute(handoffGeneration);

        assertEquals(1, firstConsumption.size());
        assertSame(routeNode, firstConsumption.get(0));
        assertNull(repeatedConsumption);
        assertEquals(
                ScreenDepartureHandoffState.NO_GENERATION,
                handoffState.captureCurrentGeneration()
        );
    }

    @Test
    public void canceledGenerationCannotConsumeOrFailReplacementGeneration() {
        ScreenDepartureHandoffState handoffState = new ScreenDepartureHandoffState();
        long canceledGeneration = handoffState.begin(
                Collections.singletonList(createRouteNode(101, "Point A"))
        );

        assertEquals(canceledGeneration, handoffState.cancelCurrent());

        RouteNode replacementRouteNode = createRouteNode(202, "Point B");
        long replacementGeneration = handoffState.begin(
                Collections.singletonList(replacementRouteNode)
        );

        assertTrue(replacementGeneration > canceledGeneration);
        assertNull(handoffState.consumeRoute(canceledGeneration));
        assertFalse(handoffState.clearIfCurrent(canceledGeneration));
        assertEquals(replacementGeneration, handoffState.captureCurrentGeneration());

        List<RouteNode> replacementRoute = handoffState.consumeRoute(replacementGeneration);
        assertEquals(1, replacementRoute.size());
        assertSame(replacementRouteNode, replacementRoute.get(0));
    }

    @Test
    public void currentFailureClearsCurrentHandoff() {
        ScreenDepartureHandoffState handoffState = new ScreenDepartureHandoffState();
        long handoffGeneration = handoffState.begin(
                Collections.singletonList(createRouteNode(303, "Point C"))
        );

        assertTrue(handoffState.clearIfCurrent(handoffGeneration));

        assertEquals(
                ScreenDepartureHandoffState.NO_GENERATION,
                handoffState.captureCurrentGeneration()
        );
        assertNull(handoffState.consumeRoute(handoffGeneration));
        assertFalse(handoffState.clearIfCurrent(handoffGeneration));
    }

    private RouteNode createRouteNode(int pointId, String pointName) {
        RouteNode routeNode = new RouteNode();
        routeNode.setId(pointId);
        routeNode.setName(pointName);
        return routeNode;
    }
}
