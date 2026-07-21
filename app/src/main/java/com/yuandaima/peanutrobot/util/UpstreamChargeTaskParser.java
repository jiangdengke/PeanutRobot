package com.yuandaima.peanutrobot.util;

import com.google.gson.Gson;
import com.yuandaima.peanutrobot.bean.ChargeModel;

import java.util.List;

public final class UpstreamChargeTaskParser {
    private static final Gson GSON = new Gson();

    private UpstreamChargeTaskParser() {
    }

    public static Integer parsePositivePileId(String requestBody) {
        try {
            ChargeModel chargeModel = GSON.fromJson(requestBody, ChargeModel.class);
            if (chargeModel == null) {
                return null;
            }

            List<ChargeModel.DataBean> chargeTasks = chargeModel.getData();
            if (chargeTasks == null || chargeTasks.isEmpty() || chargeTasks.get(0) == null) {
                return null;
            }

            String pileIdText = chargeTasks.get(0).getId();
            if (pileIdText == null || pileIdText.trim().isEmpty()) {
                return null;
            }

            int pileId = Integer.parseInt(pileIdText.trim());
            return pileId > 0 ? pileId : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
