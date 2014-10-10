package com.fortysevendeg.translace_bubble.ui.components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.fortysevendeg.translace_bubble.R;

public class BubbleView extends FrameLayout {

    private ImageView bubble;

    private ImageView loading;

    private RotateAnimation anim;

    private int widthScreen;

    private int heightScreen;

    private int heightCloseZone;

    public BubbleView(Context context) {
        super(context);
        initLayout();
    }

    public BubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout();
    }

    private void initLayout() {
        bubble = new ImageView(getContext());
        bubble.setImageResource(R.drawable.bubble);
        addView(bubble);
        loading = new ImageView(getContext());
        loading.setImageResource(R.drawable.bubble_loading);
        loading.setVisibility(INVISIBLE);
        addView(loading);

        heightCloseZone = (int) getContext().getResources().getDimension(R.dimen.height_close_zone);

        anim = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(1000);
    }

    public void init(int heightScreen, int widthScreen) {
        this.widthScreen = widthScreen;
        this.heightScreen = heightScreen;
    }

    private int left() {
        return 0;
    }

    private int right() {
        return widthScreen;
    }

    public void stopAnimation() {
        loading.clearAnimation();
        loading.setVisibility(INVISIBLE);
    }

    public void show(WindowManager.LayoutParams params, WindowManager windowManager) {
        if (getVisibility() != VISIBLE) {
            windowManager.updateViewLayout(this, params);
            setVisibility(VISIBLE);
        }
        loading.setVisibility(VISIBLE);
        loading.startAnimation(anim);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void drop(final WindowManager.LayoutParams params, final WindowManager windowManager) {
        if (params.y > heightScreen - heightCloseZone) {
            hide();
            params.x = 0;
            params.y = (int) getResources().getDimension(R.dimen.bubble_start_pos_y);
        } else {
            int x = params.x;
            int to = x < widthScreen / 2 ? left() : right();
            ValueAnimator animator = ValueAnimator.ofFloat(x, to);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float pos = (Float) animation.getAnimatedValue();
                    params.x = (int) pos;
                    windowManager.updateViewLayout(BubbleView.this, params);
                }
            });
            animator.start();
        }
    }

}
