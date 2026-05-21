package com.yuandaima.peanutrobot.adapter;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yuandaima.peanutrobot.R;
import com.yuandaima.peanutrobot.bean.BannerModel;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;
public class BannerAdapter extends BaseBannerAdapter<String> {

    @Override
    protected void bindData(BaseViewHolder<String> holder,
                            String data, int position, int pageSize) {
        ImageView imageView = holder.findViewById(R.id.banner_image);
        Log.d("bannerData","img==="+data);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(imageView.getContext())
                .load(data)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d("bannerData","onLoadFailed"+ e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.circle_cross)
                .centerCrop()
                .into(imageView);


    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_banner;
    }
}