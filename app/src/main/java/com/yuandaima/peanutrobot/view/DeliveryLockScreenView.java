package com.yuandaima.peanutrobot.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.yuandaima.peanutrobot.R;
import com.yuandaima.peanutrobot.util.DeliveryLockScreenText;

import java.util.ArrayList;
import java.util.List;

/**
 * 屏幕送餐期间叠在锁屏壁纸底部的配送提示。
 *
 * <p>上方透明露出壁纸；底部约 30% 是渐变遮罩，显示卡通字体的“配送中...”和流动进度条。
 * 到点等待时切换为“请取餐...”和取餐仓号。
 * 本 View 不消费触摸事件，锁屏的隐藏解锁手势仍由父布局处理。
 */
public class DeliveryLockScreenView extends FrameLayout {
    private static final String TAG = "DeliveryLockScreenView";
    private static final int MAX_DOTS = 3;
    private static final long DOT_INTERVAL_MS = 500L;
    private static final long PROGRESS_SWEEP_DURATION_MS = 1600L;
    private static final long BAY_PULSE_DURATION_MS = 700L;
    private static final float TITLE_STROKE_DP = 7f;
    private static final float CONTENT_MAX_FRACTION = 0.9f;
    private static final float MIN_CONTENT_SCALE = 0.3f;

    private final Handler dotHandler = new Handler(Looper.getMainLooper());
    private final List<Animator> runningAnimators = new ArrayList<>();
    private final Runnable dotRunnable = new Runnable() {
        @Override
        public void run() {
            visibleDotCount = visibleDotCount % MAX_DOTS + 1;
            renderTitle();
            dotHandler.postDelayed(this, DOT_INTERVAL_MS);
        }
    };

    private View panel;
    private LinearLayout contentLayout;
    private TextView titleOutlineView;
    private TextView titleView;
    private TextView bayView;
    private TextView detailView;
    private View progressTrack;
    private View progressBar;

    private boolean waiting;
    private boolean animationsRunning;
    private int visibleDotCount = MAX_DOTS;

    public DeliveryLockScreenView(Context context) {
        this(context, null);
    }

    public DeliveryLockScreenView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeliveryLockScreenView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_delivery_lock_screen, this, true);
        setClipChildren(false);
        panel = findViewById(R.id.fl_delivery_screen_panel);
        contentLayout = findViewById(R.id.ll_delivery_screen_content);
        titleOutlineView = findViewById(R.id.tv_delivery_screen_title_outline);
        titleView = findViewById(R.id.tv_delivery_screen_title);
        bayView = findViewById(R.id.tv_delivery_screen_bay);
        detailView = findViewById(R.id.tv_delivery_screen_detail);
        progressTrack = findViewById(R.id.fl_delivery_screen_track);
        progressBar = findViewById(R.id.v_delivery_screen_progress);
        progressTrack.setClipToOutline(true);
        applyCartoonFont();
        applyTitleOutline();
        fixTitleWidth();
        renderTitle();
    }

    /**
     * 显示前往点位状态。
     */
    public void showTraveling(String detail) {
        show(false, detail, null);
    }

    /**
     * 显示到点等待取餐状态；bayPrompt 为空时不显示仓号。
     */
    public void showWaiting(String detail, @Nullable String bayPrompt) {
        show(true, detail, bayPrompt);
    }

    /**
     * 隐藏提示并停止所有动画，锁屏壁纸保持不变。
     */
    public void hide() {
        stopAnimations();
        setVisibility(GONE);
    }

    /**
     * 停止动画但保持可见状态，用于 Activity 暂停；再次 show 时恢复。
     */
    public void stopAnimations() {
        dotHandler.removeCallbacks(dotRunnable);
        for (Animator animator : runningAnimators) {
            animator.cancel();
        }
        runningAnimators.clear();
        animationsRunning = false;
        resetAnimatedProperties();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnimations();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 内容按自然尺寸测量，超过底部提示区域时整体等比缩小，避免小屏或高密度屏上被截断或换行。
        // 缩放按两种状态中较高的一种计算，“配送中”和“请取餐”切换时标题大小保持一致。
        // 先单独测两块可切换内容，再测整体内容，让整体测量结果最终生效。
        int bayBlock = measureBlockHeight(bayView);
        int trackBlock = measureBlockHeight(progressTrack);
        int currentBlock = waiting ? bayBlock : trackBlock;
        int unspecified = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        contentLayout.measure(unspecified, unspecified);
        int tallestContentHeight = contentLayout.getMeasuredHeight()
                - currentBlock
                + Math.max(bayBlock, trackBlock);
        float availableWidth = panel.getMeasuredWidth() * CONTENT_MAX_FRACTION;
        float availableHeight = panel.getMeasuredHeight() * CONTENT_MAX_FRACTION;
        float scale = 1f;
        if (contentLayout.getMeasuredWidth() > 0 && tallestContentHeight > 0) {
            scale = Math.min(scale, availableWidth / contentLayout.getMeasuredWidth());
            scale = Math.min(scale, availableHeight / tallestContentHeight);
        }
        scale = Math.max(scale, MIN_CONTENT_SCALE);
        contentLayout.setScaleX(scale);
        contentLayout.setScaleY(scale);
    }

    private void show(boolean waitingState, String detail, @Nullable String bayPrompt) {
        boolean stateChanged = waitingState != waiting;
        waiting = waitingState;
        detailView.setText(detail);
        bayView.setText(bayPrompt);
        bayView.setVisibility(waitingState && !TextUtils.isEmpty(bayPrompt) ? VISIBLE : GONE);
        progressTrack.setVisibility(waitingState ? GONE : VISIBLE);
        setVisibility(VISIBLE);
        if (stateChanged || !animationsRunning) {
            stopAnimations();
            startAnimations();
        }
    }

    private void applyCartoonFont() {
        Typeface cartoon;
        try {
            cartoon = ResourcesCompat.getFont(getContext(), R.font.zcool_kuaile);
        } catch (Resources.NotFoundException exception) {
            Log.w(TAG, "卡通字体加载失败，使用系统粗体", exception);
            cartoon = null;
        }
        Typeface titleTypeface = cartoon != null ? cartoon : Typeface.DEFAULT_BOLD;
        titleOutlineView.setTypeface(titleTypeface);
        titleView.setTypeface(titleTypeface);
        bayView.setTypeface(titleTypeface);
        detailView.setTypeface(cartoon != null ? cartoon : Typeface.DEFAULT);
    }

    /**
     * 底层标题只画描边，叠在白色填充标题下面形成卡通描边字。
     */
    private void applyTitleOutline() {
        Paint outlinePaint = titleOutlineView.getPaint();
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeJoin(Paint.Join.ROUND);
        outlinePaint.setStrokeCap(Paint.Cap.ROUND);
        outlinePaint.setStrokeWidth(dp(TITLE_STROKE_DP));
    }

    private void startAnimations() {
        animationsRunning = true;
        visibleDotCount = 0;
        dotHandler.post(dotRunnable);

        if (waiting) {
            ObjectAnimator bayPulse = ObjectAnimator.ofPropertyValuesHolder(
                    bayView,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.08f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.08f),
                    PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0.85f)
            );
            bayPulse.setDuration(BAY_PULSE_DURATION_MS);
            bayPulse.setRepeatCount(ValueAnimator.INFINITE);
            bayPulse.setRepeatMode(ValueAnimator.REVERSE);
            bayPulse.setInterpolator(new AccelerateDecelerateInterpolator());
            startAnimator(bayPulse);
        } else {
            // 亮条从轨道左侧外扫到右侧外，按当前尺寸计算，屏幕尺寸变化后仍然正确。
            ValueAnimator progressSweep = ValueAnimator.ofFloat(0f, 1f);
            progressSweep.setDuration(PROGRESS_SWEEP_DURATION_MS);
            progressSweep.setRepeatCount(ValueAnimator.INFINITE);
            progressSweep.setInterpolator(new LinearInterpolator());
            progressSweep.addUpdateListener(animation -> {
                float barWidth = progressBar.getWidth();
                float travel = progressTrack.getWidth() + barWidth;
                progressBar.setTranslationX(-barWidth + travel * animation.getAnimatedFraction());
            });
            startAnimator(progressSweep);
        }
    }

    private void startAnimator(Animator animator) {
        runningAnimators.add(animator);
        animator.start();
    }

    private void resetAnimatedProperties() {
        bayView.setScaleX(1f);
        bayView.setScaleY(1f);
        bayView.setAlpha(1f);
        progressBar.setTranslationX(-progressBar.getWidth());
    }

    private void renderTitle() {
        StringBuilder title = new StringBuilder(DeliveryLockScreenText.title(waiting));
        for (int index = 0; index < visibleDotCount; index++) {
            title.append('.');
        }
        titleOutlineView.setText(title);
        titleView.setText(title);
    }

    /**
     * 两层标题宽度都固定为带满 3 个点时的宽度并左对齐，点数变化时文字不左右抖动，
     * 描边层和填充层的字形也始终对齐。
     */
    private void fixTitleWidth() {
        float widest = 0f;
        for (boolean waitingState : new boolean[]{false, true}) {
            StringBuilder fullTitle = new StringBuilder(DeliveryLockScreenText.title(waitingState));
            for (int index = 0; index < MAX_DOTS; index++) {
                fullTitle.append('.');
            }
            widest = Math.max(widest, Layout.getDesiredWidth(fullTitle, titleView.getPaint()));
        }
        int minWidth = (int) Math.ceil(widest + dp(TITLE_STROKE_DP))
                + titleView.getPaddingLeft()
                + titleView.getPaddingRight();
        for (TextView layer : new TextView[]{titleOutlineView, titleView}) {
            layer.setMinWidth(minWidth);
            layer.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        }
    }

    /**
     * 按子 View 自身的布局参数（固定尺寸或 wrap_content）测量，返回含上下外边距的高度。
     */
    private static int measureBlockHeight(View child) {
        MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
        int unspecified = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        child.measure(
                getChildMeasureSpec(unspecified, 0, params.width),
                getChildMeasureSpec(unspecified, 0, params.height)
        );
        return child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
