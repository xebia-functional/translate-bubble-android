package com.fortysevendeg.translace_bubble.ui.components;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.fortysevendeg.translace_bubble.R;

public class ContentView extends FrameLayout implements
        GestureDetector.OnGestureListener {

    public interface GestureListener {
        void onUp();
        void onDown();
        void onPrevious();
        void onNext();
    }

    private static final float SENSIBILITY = 80;

    private TextView original;

    private TextView translate;

    private TextView collapse;

    private TextView close;

    private ImageView options;

    private LinearLayout info;

    private LinearLayout buttons;

    private boolean showingInfo = true;

    private GestureDetectorCompat detector;

    private GestureListener gestureListener;

    public ContentView(Context context) {
        super(context);
        init();
    }

    public ContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        ViewGroup view = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.content_view, null);
        original = (TextView) view.findViewById(R.id.original);
        translate = (TextView) view.findViewById(R.id.translate);
        collapse = (TextView) view.findViewById(R.id.collapse);
        close = (TextView) view.findViewById(R.id.close);
        info = (LinearLayout) view.findViewById(R.id.info_layout);
        buttons = (LinearLayout) view.findViewById(R.id.buttons_layout);
        options = (ImageView) view.findViewById(R.id.options);
        options.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showingInfo) {
                    showButtons();
                } else {
                    showInfo();
                }
            }
        });
        showInfo();
        addView(view, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        detector = new GestureDetectorCompat(getContext(), this);
    }

    public void setGestureListener(GestureListener gestureListener) {
        this.gestureListener = gestureListener;
    }

    public void setTexts(String textOriginal, String textTranslate) {
        original.setText(textOriginal);
        translate.setText(textTranslate);
    }

    public void setListeners(OnClickListener collapseClickListener, OnClickListener closeClickListener) {
        collapse.setOnClickListener(collapseClickListener);
        close.setOnClickListener(closeClickListener);
    }

    private void showInfo() {
        showingInfo = true;
        info.setVisibility(VISIBLE);
        buttons.setVisibility(GONE);
        options.setImageResource(R.drawable.icon_options_light);
    }

    private void showButtons() {
        showingInfo = false;
        info.setVisibility(GONE);
        buttons.setVisibility(VISIBLE);
        options.setImageResource(R.drawable.icon_options_selected_light);
    }

    public void show() {
        showInfo();
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void collapse(final WindowManager.LayoutParams params, final WindowManager windowManager) {
        ValueAnimator animator = ValueAnimator.ofFloat(params.y, params.y + 100);
        animator.setDuration(100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float pos = (Float) animation.getAnimatedValue();
                params.alpha = 1 - (pos / 100);
                params.y = (int) pos;
                windowManager.updateViewLayout(ContentView.this, params);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hide();
                setAlpha(1);
                params.y = 0;
                params.alpha = 1;
                windowManager.updateViewLayout(ContentView.this, params);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (gestureListener != null) {
            if ((e1.getY() - e2.getY()) > SENSIBILITY) {
                gestureListener.onUp();
            } else if ((e2.getY() - e1.getY()) > SENSIBILITY) {
                gestureListener.onDown();
            } else if ((e1.getX() - e2.getX()) > SENSIBILITY) {
                gestureListener.onNext();
            } else if ((e2.getX() - e1.getX()) > SENSIBILITY) {
                gestureListener.onPrevious();
            }
        }
        return true;
    }
}
