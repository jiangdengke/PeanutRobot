package com.yuandaima.peanutrobot.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CenterCropCoordinateMapperTest {
    private static final float FLOAT_TOLERANCE = 0.0001f;

    @Test
    public void wideViewportClampsTopAndBottomToVisibleImageRegion() {
        CenterCropCoordinateMapper coordinateMapper = new CenterCropCoordinateMapper(
                1202f,
                1026f,
                800f,
                400f
        );

        CenterCropCoordinateMapper.Coordinate topEdge = coordinateMapper.toNormalized(400f, 0f);
        CenterCropCoordinateMapper.Coordinate aboveView = coordinateMapper.toNormalized(400f, -200f);
        CenterCropCoordinateMapper.Coordinate bottomEdge = coordinateMapper.toNormalized(400f, 400f);
        CenterCropCoordinateMapper.Coordinate belowView = coordinateMapper.toNormalized(400f, 600f);

        assertTrue(topEdge.getY() > 0f);
        assertTrue(bottomEdge.getY() < 1f);
        assertEquals(topEdge.getY(), aboveView.getY(), FLOAT_TOLERANCE);
        assertEquals(bottomEdge.getY(), belowView.getY(), FLOAT_TOLERANCE);
        assertTrue(coordinateMapper.getTranslationY() < 0f);
    }

    @Test
    public void tallViewportClampsLeftAndRightToVisibleImageRegion() {
        CenterCropCoordinateMapper coordinateMapper = new CenterCropCoordinateMapper(
                1202f,
                1026f,
                400f,
                800f
        );

        CenterCropCoordinateMapper.Coordinate leftEdge = coordinateMapper.toNormalized(0f, 400f);
        CenterCropCoordinateMapper.Coordinate leftOfView = coordinateMapper.toNormalized(-200f, 400f);
        CenterCropCoordinateMapper.Coordinate rightEdge = coordinateMapper.toNormalized(400f, 400f);
        CenterCropCoordinateMapper.Coordinate rightOfView = coordinateMapper.toNormalized(600f, 400f);

        assertTrue(leftEdge.getX() > 0f);
        assertTrue(rightEdge.getX() < 1f);
        assertEquals(leftEdge.getX(), leftOfView.getX(), FLOAT_TOLERANCE);
        assertEquals(rightEdge.getX(), rightOfView.getX(), FLOAT_TOLERANCE);
        assertTrue(coordinateMapper.getTranslationX() < 0f);
    }

    @Test
    public void nonCenterVisiblePointRoundTripsThroughViewCoordinates() {
        CenterCropCoordinateMapper coordinateMapper = new CenterCropCoordinateMapper(
                1202f,
                1026f,
                800f,
                400f
        );

        CenterCropCoordinateMapper.Coordinate viewCoordinate = coordinateMapper.toView(0.35f, 0.62f);
        CenterCropCoordinateMapper.Coordinate normalizedCoordinate = coordinateMapper.toNormalized(
                viewCoordinate.getX(),
                viewCoordinate.getY()
        );

        assertEquals(0.35f, normalizedCoordinate.getX(), FLOAT_TOLERANCE);
        assertEquals(0.62f, normalizedCoordinate.getY(), FLOAT_TOLERANCE);
    }
}
