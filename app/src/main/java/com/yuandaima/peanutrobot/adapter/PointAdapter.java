package com.yuandaima.peanutrobot.adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.keenon.sdk.component.navigation.common.Navigation;
import com.keenon.sdk.component.navigation.route.RouteNode;
import com.yuandaima.peanutrobot.R;
import com.yuandaima.peanutrobot.bean.DestModel;
import com.yuandaima.peanutrobot.interFace.OnItemClickListener;
import com.yuandaima.peanutrobot.util.TtsUntil;


import java.util.List;


public class PointAdapter extends BaseQuickAdapter<DestModel.DataBean, BaseViewHolder> {
    private TtsUntil ttsUntil;
    private boolean crossVisibility=false;

    private OnItemClickListener onItemClickListener;

    public void setCrossVisibility(boolean visibility){
        this.crossVisibility=visibility;
    }
    public void setOnClickItemListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }
    public PointAdapter(@Nullable List<DestModel.DataBean> data) {
        super(R.layout.item_point, data);
        addChildClickViewIds(R.id.tv_point);
        setOnItemChildClickListener((adapter, view, position) -> {
            int id = view.getId();
            if (id==R.id.tv_point){

                boolean isSelected = !view.isSelected();
                view.setSelected(isSelected);
                if (isSelected&&ttsUntil!=null){
                    ttsUntil.speech(((TextView)view).getText().toString(),false);
                }
                if (onItemClickListener!=null){
                 //   onItemClickListener.onClick(position,isSelected);
                }
            }

        });
    }

    public void setTtsUtil(TtsUntil ttsUntil){
        this.ttsUntil=ttsUntil;
    }

    public int getItemViewType(int position) {
        return position;
    }
    @Override
    protected void convert(@NonNull BaseViewHolder helper, DestModel.DataBean item) {
        int itemPosition = getItemPosition(item);
        helper.setText(R.id.tv_point,String.valueOf(item.getName()));
        helper.setVisible(R.id.bt_cross,crossVisibility);

    }
}
