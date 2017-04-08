package com.example.andrea.lyrics.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.andrea.lyrics.R;

/**
 * Created by andrea on 26/03/17.
 */

public class Animations {

    private static final int DURATION = 200;

    public static void open(Context ctx, final ViewGroup v) {
        Animation anim = AnimationUtils.loadAnimation(ctx, R.anim.fade_in);
        anim.setDuration(DURATION);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        v.startAnimation(anim);
    }

    public static void close(Context ctx, final ViewGroup v) {
        Animation anim = AnimationUtils.loadAnimation(ctx, R.anim.fade_out);
        anim.setDuration(DURATION);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        v.startAnimation(anim);
    }

}
