package com.yuandaima.peanutrobot.util;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuandaima.peanutrobot.R;
import com.yuandaima.peanutrobot.interFace.RequestSuccess;

import java.util.HashMap;
import java.util.Map;

public class ScrollAndSelectHelper {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Map<Integer, Boolean> selectStates = new HashMap<>();
    private  boolean isScroll = false;
    public ScrollAndSelectHelper(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        this.recyclerView = recyclerView;
        this.adapter = adapter;
    }

    public void smoothScrollToPositionAndSelect(int position, boolean isSelect, RequestSuccess success) {
        // 保存选择状态
        selectStates.put(position, isSelect);
        isScroll=false;
        // 设置一次性滚动监听
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            private  boolean isTargetScrolling = false;
            private int targetPosition = position;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                isScroll=true;
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {

                    isTargetScrolling = true;
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE && isTargetScrolling) {

                    // 滚动完成
                    isTargetScrolling = false;
                    recyclerView.removeOnScrollListener(this);

                    // 执行选择操作
                    executeSelection(targetPosition);
                }
            }


            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        };
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isScroll){
                    success.success();
                }
            }
        },50);

        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.smoothScrollToPosition(position);
    }

    private void executeSelection(int position) {
        // 获取保存的选择状态
        Boolean isSelect = selectStates.get(position);
        if (isSelect == null) return;

        // 更新视图
        View view = findViewByPosition(position);
        if (view != null) {
            TextView tvPoint = view.findViewById(R.id.tv_point);
            if (tvPoint != null) {
                tvPoint.setSelected(isSelect);
            }
        }

        // 刷新对应项
        if (adapter != null) {
            adapter.notifyItemChanged(position);
        }

        // 清理状态
        selectStates.remove(position);
    }

    private View findViewByPosition(int position) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null) {
            return layoutManager.findViewByPosition(position);
        }
        return null;
    }
}