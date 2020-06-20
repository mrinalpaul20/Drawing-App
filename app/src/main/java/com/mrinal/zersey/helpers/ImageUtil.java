package com.mrinal.zersey.helpers;

import android.text.TextUtils;
import android.widget.ImageView;

import com.mrinal.zersey.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ImageUtil {

    public static void loadImage(final ImageView imageView, final String url) {
        if (!TextUtils.isEmpty(url)) {
            Picasso.get().load(url).centerInside()
                    .placeholder(R.drawable.loading_icon)
                    .resize(512, 512)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(url).centerInside()
                                    .resize(512, 512)
                                    .placeholder(R.drawable.loading_icon).error(R.drawable.loading_failed)
                                    .into(imageView);
                        }
                    });
        } else
            imageView.setImageResource(R.drawable.loading_failed);
    }

}