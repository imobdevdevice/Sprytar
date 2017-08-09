package com.novp.sprytar.poi;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.novp.sprytar.data.model.PoiFile;
import com.novp.sprytar.databinding.PoiPlayerViewBinding;

import java.util.ArrayList;
import java.util.List;

class ImagePagerAdapter extends PagerAdapter {
    List<PoiFile> files;

    private Context context;

    private Callback callback;


    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public ImagePagerAdapter(Context context) {
        this.context = context;
        files = new ArrayList<PoiFile>();
    }

    @Override
    public int getCount() {
        return files.size();
    }

    public void setImages(List<PoiFile> images) {
        this.files = images;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        PoiFile poiFile = files.get(position);

        if (!poiFile.getFileType().equals(PoiFile.IMAGE)) {

            PoiPlayerViewBinding binding = PoiPlayerViewBinding.inflate(LayoutInflater.from(context),
                    null, false);
            binding.playerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

//            SurfaceView view = new SurfaceView(context);
//            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
//                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
            callback.updatePoiControls(poiFile, binding);

            ((ViewPager) container).addView(binding.playerView, 0);
            return binding.playerView;
        } else {

            SimpleDraweeView view = new SimpleDraweeView(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            view.setImageURI(Uri.parse(poiFile.getFilePath()));

            ((ViewPager) container).addView(view, 0);
            return view;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (!files.get(position).getFileType().equals(PoiFile.IMAGE)) {
            ((ViewPager) container).removeView((SurfaceView) object);
        } else {
            ((ViewPager) container).removeView((SimpleDraweeView) object);
        }
    }

    public interface Callback {
        void updatePoiControls(PoiFile poiFile, PoiPlayerViewBinding binding);
    }
}
