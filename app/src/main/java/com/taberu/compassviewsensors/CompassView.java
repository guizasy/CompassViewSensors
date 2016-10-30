package com.taberu.compassviewsensors;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Guilherme on 30/10/2016.
 */

public class CompassView extends View {
    private float bearing;
    private Paint markerPaint;
    private Paint textPaint;
    private Paint circlePaint;
    private String nothStr;
    private String southStr;
    private String eastStr;
    private String westStr;
    private int textHeight;

    public CompassView(Context context) {
        super(context);
        initCompassView();
    }

    public CompassView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initCompassView();
    }

    private int measure(int Spec) {
        int retVal;

        int specMode = MeasureSpec.getMode(Spec);
        int specSize = MeasureSpec.getSize(Spec);

        if (specMode == MeasureSpec.UNSPECIFIED)
            retVal = 200;
        else
            retVal = specSize;

        return retVal;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        // ocupar o espaço disponível obedecendo restrições view pai
        int measuredWidth = measure(widthSpec);
        int measuredHeight = measure(heightSpec);
        int diam = Math.min(measuredWidth, measuredHeight);
        setMeasuredDimension(diam, diam);
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    protected void initCompassView() {
        Resources r = this.getResources();

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(r.getColor(R.color.colorCompassBackground, null));
        circlePaint.setStrokeWidth(5);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        nothStr = r.getString(R.string.cardinalN);
        southStr = r.getString(R.string.cardinalS);
        eastStr = r.getString(R.string.cardinalE);
        westStr = r.getString(R.string.cardinalW);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(r.getColor(R.color.colorCompassText, null));
        textPaint.setTextSize(30);
        textHeight = (int) textPaint.measureText("yY");

        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setStrokeWidth(5);
        markerPaint.setColor(r.getColor(R.color.colorCompassMarker, null));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int px = measuredWidth / 2;
        int py = measuredHeight / 2;
        int radius = Math.min(px, py);

        canvas.drawCircle(px, py, radius, circlePaint);

        canvas.save();
        canvas.rotate(-bearing, px, py);

        int textWidth = (int) textPaint.measureText("W");
        int cardinalX = px - textWidth / 2;
        int cardinalY = py - radius + textHeight;

        for (int i = 0; i < 24; i++) {
            canvas.drawLine(px, py - radius, px, py - radius + 20, markerPaint);
            canvas.save();
            canvas.translate(0, textHeight);

            if (i % 6 == 0) {
                String dirStr = "";

                switch (i) {
                    case (0): {
                        dirStr = nothStr;
                        int arrowY = 2 * textHeight;
                        canvas.drawLine(px, arrowY, px - 10, 3 * textHeight, markerPaint);
                        canvas.drawLine(px, arrowY, px + 10, 3 * textHeight, markerPaint);
                        break;
                    }
                    case (6): {
                        dirStr = eastStr;
                        break;
                    }
                    case (12): {
                        dirStr = southStr;
                        break;
                    }
                    case (18): {
                        dirStr = westStr;
                        break;
                    }

                }
                canvas.drawText(dirStr, cardinalX, cardinalY, textPaint);
            } else if (i % 3 == 0) {
                String angle = String.valueOf(i * 15);
                float angleTextWidth = textPaint.measureText(angle);

                int angleTextX = (int) (px - angleTextWidth / 2);
                int angleTextY = py - radius + textHeight;
                canvas.drawText(angle, angleTextX, angleTextY, textPaint);
            }

            canvas.restore();
            canvas.rotate(15, px, py);
        }
        canvas.restore();
    }
}
