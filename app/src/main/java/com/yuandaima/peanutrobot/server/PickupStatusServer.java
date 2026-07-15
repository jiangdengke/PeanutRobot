package com.yuandaima.peanutrobot.server;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class PickupStatusServer extends NanoHTTPD {
    private static final String TAG = "PickupStatusServer";
    private static final String PICKUP_STATUS_PATH = "/pickup_status";

    private final PickupStatusCallback pickupStatusCallback;

    public PickupStatusServer(int port, PickupStatusCallback pickupStatusCallback) {
        super(port);
        this.pickupStatusCallback = pickupStatusCallback;
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (!PICKUP_STATUS_PATH.equals(session.getUri())) {
            return createJsonResponse(
                    Response.Status.NOT_FOUND,
                    "not_found",
                    "Path not found"
            );
        }

        if (!Method.POST.equals(session.getMethod())) {
            Response response = createJsonResponse(
                    Response.Status.METHOD_NOT_ALLOWED,
                    "method_not_allowed",
                    "Only POST is supported"
            );
            response.addHeader("Allow", "POST");
            return response;
        }

        String contentType = session.getHeaders().get("content-type");
        if (contentType == null
                || !"application/json".equals(contentType
                .toLowerCase(Locale.US)
                .split(";", 2)[0]
                .trim())) {
            return createJsonResponse(
                    Response.Status.UNSUPPORTED_MEDIA_TYPE,
                    "unsupported_media_type",
                    "Content-Type must be application/json"
            );
        }

        String requestBody;
        try {
            Map<String, String> parsedBody = new HashMap<>();
            session.parseBody(parsedBody);
            requestBody = parsedBody.get("postData");
        } catch (IOException | ResponseException exception) {
            Log.w(TAG, "Failed to read pickup request body", exception);
            return createJsonResponse(
                    Response.Status.BAD_REQUEST,
                    "invalid_body",
                    "Request body could not be read"
            );
        }

        if (requestBody == null || requestBody.trim().isEmpty()) {
            return createJsonResponse(
                    Response.Status.BAD_REQUEST,
                    "invalid_body",
                    "Request body is required"
            );
        }

        try {
            JSONObject requestJson = new JSONObject(requestBody);
            if (!requestJson.has("pickup_status")) {
                return createJsonResponse(
                        Response.Status.BAD_REQUEST,
                        "missing_pickup_status",
                        "pickup_status is required"
                );
            }

            Object pickupStatusValue = requestJson.get("pickup_status");
            if (!(pickupStatusValue instanceof Boolean)) {
                return createJsonResponse(
                        Response.Status.BAD_REQUEST,
                        "invalid_pickup_status",
                        "pickup_status must be a boolean"
                );
            }

            if (!((Boolean) pickupStatusValue)) {
                return createJsonResponse(
                        Response.Status.BAD_REQUEST,
                        "pickup_not_completed",
                        "pickup_status must be true"
                );
            }
        } catch (JSONException exception) {
            return createJsonResponse(
                    Response.Status.BAD_REQUEST,
                    "invalid_json",
                    "Request body must be valid JSON"
            );
        }

        if (pickupStatusCallback == null || !pickupStatusCallback.onPickupCompleted()) {
            return createJsonResponse(
                    Response.Status.CONFLICT,
                    "no_active_wait",
                    "No active arrival wait"
            );
        }

        return createJsonResponse(
                Response.Status.OK,
                "accepted",
                "Pickup completion accepted"
        );
    }

    private Response createJsonResponse(Response.Status status, String result, String message) {
        JSONObject responseJson = new JSONObject();
        try {
            responseJson.put("status", result);
            responseJson.put("message", message);
        } catch (JSONException exception) {
            Log.e(TAG, "Failed to create pickup response", exception);
        }
        return newFixedLengthResponse(status, "application/json; charset=utf-8", responseJson.toString());
    }

    public interface PickupStatusCallback {
        boolean onPickupCompleted();
    }
}
