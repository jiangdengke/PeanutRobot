package com.yuandaima.peanutrobot.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.yuandaima.peanutrobot.R;
import com.yuandaima.peanutrobot.bean.DestModel;
import com.yuandaima.peanutrobot.bean.MediaModel;
import com.yuandaima.peanutrobot.interFace.OnItemClickListener;
import com.yuandaima.peanutrobot.util.TtsUntil;

import java.util.List;


public class MediaAdapter extends BaseMultiItemQuickAdapter<MediaModel, BaseViewHolder> {

    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_VIDEO = 2;
    private Context mContext;
    public MediaAdapter(@Nullable List<MediaModel> data, Context context) {
        super(data);
        addItemType(TYPE_IMAGE, R.layout.item_img);
        addItemType(TYPE_VIDEO, R.layout.item_media);
        this.mContext=context;

    }


//    public int getItemViewType(int position) {
//        return position;
//    }
    @Override
    protected void convert(@NonNull BaseViewHolder helper, MediaModel item) {
       Log.d("MediaAdapteraaa","i="+getItemPosition(item)+"type="+getItemViewType(getItemPosition(item)));
        switch (getItemViewType(getItemPosition(item))) {
            case TYPE_IMAGE:
                bindImageData(helper, item);
                break;
            case TYPE_VIDEO:
                bindVideoData(helper, item);
                break;
        }
    }
    private void bindVideoData(BaseViewHolder helper, MediaModel item) {

        // 绑定视频数据
        VideoView videoView = helper.getView(R.id.media_view);
//        videoView.setVideoURI(Uri.parse(item.getVideoUrl()));
//        Glide.with(mContext)
//                .load(item.getCoverUrl())
//                .into(coverView);
//
//        helper.setText(R.id.tv_duration, item.getDuration());
//
//        // 添加点击事件
//        helper.addOnClickListener(R.id.btn_play);
    }
    private void bindImageData(BaseViewHolder helper, MediaModel item) {
        Log.d("asdada","getImageUrl=="+item.getImageUrl());
        // 绑定图片数据
        ImageView imageView = helper.getView(R.id.iv_view);
        Glide.with(imageView.getContext().getApplicationContext())
                .load(R.mipmap.wifi)
                .into(imageView);

//        helper.setText(R.id.tv_title, item.getTitle());
    }
}

