package com.yuandaima.peanutrobot.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.yuandaima.peanutrobot.R;
import com.yuandaima.peanutrobot.bean.MapPointConfig;
import com.yuandaima.peanutrobot.util.CenterCropCoordinateMapper;

import java.util.ArrayList;
import java.util.List;

public class MapPointOverlayView extends View {
    private static final long EDIT_ENTRY_HOLD_DURATION_MS = 3000L;

    private final Handler interactionHandler = new Handler(Looper.getMainLooper());
    private final Paint markerFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint markerStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint markerCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint labelTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path locationMarkerPath = new Path();
    private final List<MapPointConfig.Marker> markers = new ArrayList<>();
    private final float density;
    private final float touchSlop;
    private final Runnable editEntryRunnable = this::notifyEditEntryRequested;

    private float sourceImageWidth;
    private float sourceImageHeight;
    private CenterCropCoordinateMapper coordinateMapper;
    private Integer focusedRobotPointId;
    private Integer editingRobotPointId;
    private Integer draggedRobotPointId;
    private float touchDownX;
    private float touchDownY;
    private boolean touchMoved;
    private boolean editEntryTriggered;
    private boolean editMode;
    private OnMapEditEntryListener onMapEditEntryListener;
    private OnMarkerInteractionListener onMarkerInteractionListener;

    public MapPointOverlayView(Context context) {
        this(context, null);
    }

    public MapPointOverlayView(Context context, @Nullable AttributeSet attributes) {
        this(context, attributes, 0);
    }

    public MapPointOverlayView(
            Context context,
            @Nullable AttributeSet attributes,
            int defaultStyleAttribute
    ) {
        super(context, attributes, defaultStyleAttribute);
        density = getResources().getDisplayMetrics().density;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        configurePaints(context);
        setClickable(true);
    }

    private void configurePaints(Context context) {
        int primaryColor = ContextCompat.getColor(context, R.color.ui_primary);
        markerFillPaint.setStyle(Paint.Style.FILL);
        markerFillPaint.setColor(primaryColor);

        markerStrokePaint.setStyle(Paint.Style.STROKE);
        markerStrokePaint.setStrokeWidth(dp(2f));
        markerStrokePaint.setColor(Color.WHITE);

        markerCenterPaint.setStyle(Paint.Style.FILL);
        markerCenterPaint.setColor(Color.WHITE);

        labelTextPaint.setStyle(Paint.Style.FILL);
        labelTextPaint.setColor(ContextCompat.getColor(context, R.color.ui_text_primary));
        labelTextPaint.setTextSize(dp(13f));

        labelBackgroundPaint.setStyle(Paint.Style.FILL);
        labelBackgroundPaint.setColor(0xEFFFFFFF);
    }

    public void setSourceImageSize(float sourceImageWidth, float sourceImageHeight) {
        this.sourceImageWidth = sourceImageWidth;
        this.sourceImageHeight = sourceImageHeight;
        refreshCoordinateMapper();
        invalidate();
    }

    public void setMarkers(List<MapPointConfig.Marker> markers) {
        this.markers.clear();
        if (markers != null) {
            this.markers.addAll(markers);
        }
        invalidate();
    }

    public void setFocusedRobotPointId(Integer focusedRobotPointId) {
        this.focusedRobotPointId = focusedRobotPointId;
        invalidate();
    }

    public void setEditingRobotPointId(Integer editingRobotPointId) {
        this.editingRobotPointId = editingRobotPointId;
        invalidate();
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        cancelPendingEditEntry();
        draggedRobotPointId = null;
        invalidate();
    }

    public void setOnMapEditEntryListener(OnMapEditEntryListener listener) {
        onMapEditEntryListener = listener;
    }

    public void setOnMarkerInteractionListener(OnMarkerInteractionListener listener) {
        onMarkerInteractionListener = listener;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        refreshCoordinateMapper();
    }

    private void refreshCoordinateMapper() {
        if (sourceImageWidth <= 0f || sourceImageHeight <= 0f || getWidth() <= 0 || getHeight() <= 0) {
            coordinateMapper = null;
            return;
        }
        coordinateMapper = new CenterCropCoordinateMapper(
                sourceImageWidth,
                sourceImageHeight,
                getWidth(),
                getHeight()
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (coordinateMapper == null) {
            return;
        }

        for (MapPointConfig.Marker marker : markers) {
            if (marker == null || !marker.isEnabled() || isFocusedMarker(marker)) {
                continue;
            }
            drawMarker(canvas, marker, false);
        }
        for (MapPointConfig.Marker marker : markers) {
            if (marker != null && marker.isEnabled() && isFocusedMarker(marker)) {
                drawMarker(canvas, marker, true);
            }
        }
    }

    private boolean isFocusedMarker(MapPointConfig.Marker marker) {
        Integer highlightedRobotPointId = editMode ? editingRobotPointId : focusedRobotPointId;
        return highlightedRobotPointId != null
                && marker.getRobotPointId() == highlightedRobotPointId;
    }

    private void drawMarker(Canvas canvas, MapPointConfig.Marker marker, boolean focused) {
        CenterCropCoordinateMapper.Coordinate viewCoordinate = coordinateMapper.toView(
                marker.getNormalizedX(),
                marker.getNormalizedY()
        );
        float markerX = viewCoordinate.getX();
        float markerY = viewCoordinate.getY();

        if (focused) {
            drawLocationMarker(canvas, markerX, markerY);
        } else {
            float markerRadius = dp(editMode ? 7f : 5f);
            canvas.drawCircle(markerX, markerY, markerRadius, markerFillPaint);
            canvas.drawCircle(markerX, markerY, markerRadius, markerStrokePaint);
        }

        if (editMode || focused) {
            float preferredLabelBottom = focused ? markerY - dp(26f) : markerY - dp(10f);
            float fallbackLabelTop = markerY + dp(focused ? 6f : 10f);
            drawMarkerLabel(
                    canvas,
                    marker.getDisplayName(),
                    markerX,
                    preferredLabelBottom,
                    fallbackLabelTop
            );
        }
    }

    private void drawLocationMarker(Canvas canvas, float markerX, float markerY) {
        float markerRadius = dp(12f);
        float markerCenterY = markerY - markerRadius;
        locationMarkerPath.reset();
        locationMarkerPath.moveTo(markerX, markerY);
        locationMarkerPath.lineTo(markerX - markerRadius * 0.72f, markerCenterY + markerRadius * 0.4f);
        locationMarkerPath.lineTo(markerX + markerRadius * 0.72f, markerCenterY + markerRadius * 0.4f);
        locationMarkerPath.close();
        canvas.drawPath(locationMarkerPath, markerFillPaint);
        canvas.drawCircle(markerX, markerCenterY, markerRadius, markerFillPaint);
        canvas.drawCircle(markerX, markerCenterY, markerRadius, markerStrokePaint);
        canvas.drawCircle(markerX, markerCenterY, markerRadius * 0.36f, markerCenterPaint);
    }

    private void drawMarkerLabel(
            Canvas canvas,
            String displayName,
            float markerX,
            float preferredLabelBottom,
            float fallbackLabelTop
    ) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return;
        }
        String normalizedDisplayName = displayName.trim();
        float horizontalPadding = dp(7f);
        float verticalPadding = dp(4f);
        float viewEdgeMargin = dp(2f);
        float maximumLabelWidth = getWidth() - viewEdgeMargin * 2f;
        float maximumTextWidth = maximumLabelWidth - horizontalPadding * 2f;
        if (maximumTextWidth <= 0f) {
            return;
        }
        String visibleDisplayName = TextUtils.ellipsize(
                normalizedDisplayName,
                labelTextPaint,
                maximumTextWidth,
                TextUtils.TruncateAt.END
        ).toString();
        Paint.FontMetrics fontMetrics = labelTextPaint.getFontMetrics();
        float textWidth = labelTextPaint.measureText(visibleDisplayName);
        float labelWidth = textWidth + horizontalPadding * 2f;
        float labelHeight = fontMetrics.bottom - fontMetrics.top + verticalPadding * 2f;
        float labelLeft = markerX - labelWidth / 2f;
        labelLeft = Math.max(
                viewEdgeMargin,
                Math.min(getWidth() - labelWidth - viewEdgeMargin, labelLeft)
        );
        float preferredLabelTop = preferredLabelBottom - labelHeight;
        float labelTop = preferredLabelTop >= viewEdgeMargin
                ? preferredLabelTop
                : fallbackLabelTop;
        labelTop = Math.max(
                viewEdgeMargin,
                Math.min(getHeight() - labelHeight - viewEdgeMargin, labelTop)
        );
        float labelBottom = labelTop + labelHeight;
        RectF labelBounds = new RectF(labelLeft, labelTop, labelLeft + labelWidth, labelBottom);
        canvas.drawRoundRect(labelBounds, dp(8f), dp(8f), labelBackgroundPaint);
        float textBaseline = labelTop + verticalPadding - fontMetrics.top;
        canvas.drawText(
                visibleDisplayName,
                labelLeft + horizontalPadding,
                textBaseline,
                labelTextPaint
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (coordinateMapper == null) {
            return true;
        }
        return editMode ? handleEditTouch(event) : handleNormalTouch(event);
    }

    private boolean handleNormalTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touchDownX = event.getX();
                touchDownY = event.getY();
                touchMoved = false;
                editEntryTriggered = false;
                interactionHandler.postDelayed(editEntryRunnable, EDIT_ENTRY_HOLD_DURATION_MS);
                return true;
            case MotionEvent.ACTION_MOVE:
                if (hasMovedBeyondTouchSlop(event.getX(), event.getY())) {
                    touchMoved = true;
                    cancelPendingEditEntry();
                }
                return true;
            case MotionEvent.ACTION_UP:
                cancelPendingEditEntry();
                if (!touchMoved && !editEntryTriggered) {
                    performClick();
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                cancelPendingEditEntry();
                return true;
            default:
                return true;
        }
    }

    private boolean handleEditTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touchDownX = event.getX();
                touchDownY = event.getY();
                touchMoved = false;
                draggedRobotPointId = findMarkerAt(event.getX(), event.getY());
                if (draggedRobotPointId != null && onMarkerInteractionListener != null) {
                    onMarkerInteractionListener.onMarkerSelected(draggedRobotPointId);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (hasMovedBeyondTouchSlop(event.getX(), event.getY())) {
                    touchMoved = true;
                }
                if (draggedRobotPointId != null) {
                    updateMarkerPosition(draggedRobotPointId, event.getX(), event.getY());
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (draggedRobotPointId == null && !touchMoved && editingRobotPointId != null) {
                    notifyMarkerPositionChanged(editingRobotPointId, event.getX(), event.getY());
                    performClick();
                }
                draggedRobotPointId = null;
                return true;
            case MotionEvent.ACTION_CANCEL:
                draggedRobotPointId = null;
                return true;
            default:
                return true;
        }
    }

    private Integer findMarkerAt(float touchX, float touchY) {
        float hitRadius = dp(24f);
        for (int markerIndex = markers.size() - 1; markerIndex >= 0; markerIndex--) {
            MapPointConfig.Marker marker = markers.get(markerIndex);
            if (marker == null || !marker.isEnabled()) {
                continue;
            }
            CenterCropCoordinateMapper.Coordinate markerCoordinate = coordinateMapper.toView(
                    marker.getNormalizedX(),
                    marker.getNormalizedY()
            );
            float horizontalDistance = touchX - markerCoordinate.getX();
            float verticalDistance = touchY - markerCoordinate.getY();
            if (horizontalDistance * horizontalDistance + verticalDistance * verticalDistance
                    <= hitRadius * hitRadius) {
                return marker.getRobotPointId();
            }
        }
        return null;
    }

    private void updateMarkerPosition(int robotPointId, float viewX, float viewY) {
        notifyMarkerPositionChanged(robotPointId, viewX, viewY);
    }

    private void notifyMarkerPositionChanged(int robotPointId, float viewX, float viewY) {
        CenterCropCoordinateMapper.Coordinate normalizedCoordinate = coordinateMapper.toNormalized(
                viewX,
                viewY
        );
        for (MapPointConfig.Marker marker : markers) {
            if (marker != null && marker.getRobotPointId() == robotPointId) {
                marker.setNormalizedX(normalizedCoordinate.getX());
                marker.setNormalizedY(normalizedCoordinate.getY());
                break;
            }
        }
        invalidate();
        if (onMarkerInteractionListener != null) {
            onMarkerInteractionListener.onMarkerPositionChanged(
                    robotPointId,
                    normalizedCoordinate.getX(),
                    normalizedCoordinate.getY()
            );
        }
    }

    private boolean hasMovedBeyondTouchSlop(float currentX, float currentY) {
        float horizontalDistance = currentX - touchDownX;
        float verticalDistance = currentY - touchDownY;
        return horizontalDistance * horizontalDistance + verticalDistance * verticalDistance
                > touchSlop * touchSlop;
    }

    private void notifyEditEntryRequested() {
        editEntryTriggered = true;
        if (onMapEditEntryListener != null) {
            onMapEditEntryListener.onMapEditEntryRequested();
        }
    }

    private void cancelPendingEditEntry() {
        interactionHandler.removeCallbacks(editEntryRunnable);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelPendingEditEntry();
        super.onDetachedFromWindow();
    }

    private float dp(float value) {
        return value * density;
    }

    public interface OnMapEditEntryListener {
        void onMapEditEntryRequested();
    }

    public interface OnMarkerInteractionListener {
        void onMarkerSelected(int robotPointId);

        void onMarkerPositionChanged(int robotPointId, float normalizedX, float normalizedY);
    }
}
