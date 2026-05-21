package com.yuandaima.peanutrobot.presentation;

import android.app.Presentation;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.viewpager2.widget.ViewPager2;

import com.yuandaima.peanutrobot.MainActivity;
import com.yuandaima.peanutrobot.adapter.BannerAdapter;
import com.yuandaima.peanutrobot.adapter.MediaAdapter;
import com.yuandaima.peanutrobot.bean.BannerModel;
import com.yuandaima.peanutrobot.bean.MediaModel;
import com.yuandaima.peanutrobot.bean.ScreenModel;
import com.yuandaima.peanutrobot.databinding.ActivityMainBinding;
import com.yuandaima.peanutrobot.databinding.PresentaionViewBinding;
import com.yuandaima.peanutrobot.manager.VideoCacheManager;
import com.zhpan.bannerview.constants.IndicatorGravity;
import com.zhpan.indicator.enums.IndicatorStyle;

import java.util.ArrayList;
import java.util.List;

import okio.ByteString;

public class PresentationCoucou extends Presentation {
    String videoUrl = "http://vjs.zencdn.net/v/oceans.mp4";
    private ExoPlayer player;

    private PresentaionViewBinding mBinding;
    private MediaAdapter mediaAdapter;
    private List<MediaModel> mediaModelList = new ArrayList<>();
    String videoPath =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/peanut/media/ad/吧台.mp4";

    private ByteString bytes;
    private Context outerContext;
    private ScreenModel screenModel;
    private MainActivity.MediaType mediaType;
    private  BannerModel bannerModelList;
    public PresentationCoucou(Context outerContext, Display display, ScreenModel screenModel, BannerModel bannerModelList, MainActivity.MediaType mediaType) {
        super(outerContext, display);
        Log.d("presentation","PresentationCoucou");
        this.outerContext = outerContext;
        this.screenModel = screenModel;
        this.mediaType = mediaType;
        this.bannerModelList=bannerModelList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = PresentaionViewBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (mediaType== MainActivity.MediaType.VIDEO){
            Log.d("presentation","initPlay");
            initPlay();
        }else {
            Log.d("presentation","showImageList");
            showImageList();
        }





    }

    private void initPlay() {
        mBinding.playerView.setVisibility(View.VISIBLE);
        mBinding.bannerView.setVisibility(View.GONE);

        player = new ExoPlayer.Builder(outerContext).build();
        //循环模式
        player.setRepeatMode(Player.REPEAT_MODE_ONE);

        mBinding.playerView.setPlayer(player);
        Log.d("bannerData","video==="+screenModel.getUrl());
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(screenModel.getUrl()));

        player.setMediaItem(mediaItem);

        player.prepare();
    }
    //播放新视频
    public void playNewVideo(String videoUrl) {
        boolean wasPlaying = player.isPlaying();
        long currentPosition = player.getCurrentPosition();

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));

        player.setMediaItem(mediaItem);
        player.prepare();
        if (wasPlaying) {
            player.play();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("presentation","onStart");
        if (player != null) {
            player.play();
        }
        if (mBinding.bannerView != null) {
            mBinding.bannerView.startLoop();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        // 当界面不可见时，暂停播放以节省资源
        if (player != null) {
            player.pause();
            player.release();
            player = null;
        }
    }


    private void showImageList() {
        mBinding.playerView.setVisibility(View.GONE);
        mBinding.bannerView.setVisibility(View.VISIBLE);
        mBinding.bannerView
                .setOrientation(ViewPager2.ORIENTATION_VERTICAL)
                .setAdapter(new BannerAdapter())
                .setIndicatorStyle(IndicatorStyle.CIRCLE)
                .setIndicatorSliderColor(Color.GRAY, Color.WHITE)
                .setIndicatorGravity(IndicatorGravity.CENTER)
                .setInterval(bannerModelList.getTime_sleep())
                .setCanLoop(true)
                .setAutoPlay(true)
                .setOnPageClickListener((view, position) -> {

                }).create(bannerModelList.getImageUrl());
//        mBinding.playerView.setVisibility(View.GONE);
//        mBinding.rvMedia.setVisibility(View.VISIBLE);
//        mediaAdapter=new MediaAdapter(mediaModelList, getContext());
//        mBinding.rvMedia.setAdapter(mediaAdapter);

    }
}
