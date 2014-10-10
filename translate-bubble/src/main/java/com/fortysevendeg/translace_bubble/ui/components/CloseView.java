package com.fortysevendeg.translace_bubble.ui.components;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import com.fortysevendeg.translace_bubble.R;

public class CloseView extends View {

    private Paint paintBackground;

    private int width;

    private int height;

    private int middleWidth;

    private int middleHeight;

    private float[] positionsColors = new float[]{0f, 1f};

    private int[] colors = new int[]{Color.parseColor("#99000000"), Color.TRANSPARENT};

    private int radius;

    private int sizeAcross;

    private Paint paintCircle;

    public CloseView(Context context) {
        super(context);
        init(context);
    }

    public CloseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CloseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paintBackground = new Paint();

        radius = (int) context.getResources().getDimension(R.dimen.radius_close);
        sizeAcross = radius / 3;
        int stroke = (int) context.getResources().getDimension(R.dimen.stroke_close);

        paintCircle = new Paint();
        paintCircle.setColor(Color.WHITE);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setStrokeWidth(stroke);
        paintCircle.setAntiAlias(true);

    }

    private void load() {
        if (width == 0) {
            width = getWidth();
            height = getHeight();
            middleWidth = width / 2;
            middleHeight = height / 2;
            paintBackground.setShader(new LinearGradient(width / 2, height, width / 2, 0, colors, positionsColors, Shader.TileMode.CLAMP));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        load();
        canvas.drawRect(0, 0, width, height, paintBackground);
        canvas.drawCircle(middleWidth, middleHeight, radius, paintCircle);
        canvas.drawLine(middleWidth - sizeAcross, middleHeight - sizeAcross, middleWidth + sizeAcross, middleHeight + sizeAcross, paintCircle);
        canvas.drawLine(middleWidth + sizeAcross, middleHeight - sizeAcross, middleWidth - sizeAcross, middleHeight + sizeAcross, paintCircle);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }

}
