package com.yuandaima.peanutrobot.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DeliveryLockScreenTextTest {
    @Test
    public void titleDependsOnWaitingState() {
        assertEquals("配送中", DeliveryLockScreenText.title(false));
        assertEquals("请取餐", DeliveryLockScreenText.title(true));
    }

    @Test
    public void travelingDetailShowsOneBasedProgressAndPointName() {
        assertEquals("正在前往 第 1/3 个点位 · A3桌",
                DeliveryLockScreenText.travelingDetail(0, 3, "A3桌"));
        assertEquals("正在前往 第 3/3 个点位",
                DeliveryLockScreenText.travelingDetail(2, 3, "  "));
    }

    @Test
    public void waitingDetailShowsOneBasedProgressAndPointName() {
        assertEquals("第 2/3 个点位 · 包间 8", DeliveryLockScreenText.waitingDetail(1, 3, " 包间 8 "));
        assertEquals("第 1/1 个点位", DeliveryLockScreenText.waitingDetail(0, 1, null));
    }

    @Test
    public void invalidProgressFallsBackToGenericText() {
        assertEquals("正在前往配送点位", DeliveryLockScreenText.travelingDetail(-1, 3, null));
        assertEquals("正在前往配送点位 · A1", DeliveryLockScreenText.travelingDetail(3, 3, "A1"));
        assertEquals("已到达配送点位", DeliveryLockScreenText.waitingDetail(0, 0, ""));
    }

    @Test
    public void bayPromptRequiresPositiveBay() {
        assertEquals("请从 2 号仓取餐", DeliveryLockScreenText.bayPrompt(2));
        assertNull(DeliveryLockScreenText.bayPrompt(0));
        assertNull(DeliveryLockScreenText.bayPrompt(-1));
    }
}
