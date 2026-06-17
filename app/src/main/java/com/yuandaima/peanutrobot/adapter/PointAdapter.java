package com.yuandaima.peanutrobot.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yuandaima.peanutrobot.R;
import com.yuandaima.peanutrobot.bean.DestModel;
import com.yuandaima.peanutrobot.interFace.OnItemClickListener;
import com.yuandaima.peanutrobot.util.TtsUntil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PointAdapter extends BaseQuickAdapter<DestModel.DataBean, BaseViewHolder> {
    private TtsUntil ttsUntil;
    private boolean crossVisibility=false;
    private final Set<Integer> selectedPointIds = new HashSet<>();

    private OnItemClickListener onItemClickListener;

    public void setCrossVisibility(boolean visibility){
        this.crossVisibility=visibility;
    }

    public void setOnClickItemListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    public void setSelectedPoints(@Nullable List<DestModel.DataBean> selectedPoints) {
        selectedPointIds.clear();
        if (selectedPoints != null) {
            for (DestModel.DataBean selectedPoint : selectedPoints) {
                if (selectedPoint != null) {
                    selectedPointIds.add(selectedPoint.getId());
                }
            }
        }
        notifyDataSetChanged();
    }

    public PointAdapter(@Nullable List<DestModel.DataBean> data) {
        super(R.layout.item_point, data);
        addChildClickViewIds(R.id.tv_point, R.id.bt_cross);
        setOnItemChildClickListener((adapter, view, position) -> {
            int id = view.getId();
            if (id == R.id.bt_cross) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(position, false);
                }
                return;
            }
            if (id==R.id.tv_point){
                DestModel.DataBean dataBean = getData().get(position);
                boolean isSelected = !isPointSelected(dataBean);
                if (dataBean != null) {
                    if (isSelected) {
                        selectedPointIds.add(dataBean.getId());
                    } else {
                        selectedPointIds.remove(dataBean.getId());
                    }
                }
                view.setSelected(isSelected);
                if (isSelected&&ttsUntil!=null){
                    ttsUntil.speech(((TextView)view).getText().toString(),false);
                }
                if (onItemClickListener!=null){
                    onItemClickListener.onClick(position,isSelected);
                }
                notifyDataSetChanged();
            }
        });
    }

    private boolean isPointSelected(DestModel.DataBean item) {
        return item != null && selectedPointIds.contains(item.getId());
    }

    public void setTtsUtil(TtsUntil ttsUntil){
        this.ttsUntil=ttsUntil;
    }

    public int getItemViewType(int position) {
        return position;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, DestModel.DataBean item) {
        helper.setText(R.id.tv_point,String.valueOf(item.getName()));
        helper.setVisible(R.id.bt_cross,crossVisibility);
        TextView pointView = helper.getView(R.id.tv_point);
        pointView.setSelected(isPointSelected(item));
    }
}
