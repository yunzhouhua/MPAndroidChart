package com.igen.solar.newversion.render;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.Log;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

public class IGENLineChartRenderer extends LineChartRenderer {

    /**
     * path that is used for drawing highlight-lines (drawLines(...) cannot be used because of dashes)
     */
    private Path mCustomHighlightPath = new Path();

    private PathEffect mDefaultCustomHighlightPathEffect = new DashPathEffect(new float[]{
            5f, 5f
    }, 0f);
    private Paint mDotPaint;

    private float DotRadius = 6f;

    public IGENLineChartRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);

        mDotPaint = new Paint();
        mDotPaint.setAntiAlias(true);
        mDotPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        if (indices == null || indices.length == 0) {
            return;
        }

        LineData lineData = mChart.getLineData();
        float targetX = (float) indices[0].getX();
        Log.e("MPAndroid", "hightlight X = " + targetX);
        List<Entry> entryList = new ArrayList<>();
        List<Integer> colorList = new ArrayList<>();
        float maxY = 0.0f;
        for (ILineDataSet set : lineData.getDataSets()) {
            Entry entry = set.getEntryForXValue(targetX, Float.NaN);
            if ((int) entry.getX() == (int) targetX) {
                entryList.add(entry);
                colorList.add(set.getColor());
                maxY = Math.max(maxY, entry.getY());
            }
        }

        // TODO 是否要考虑轴
        MPPointD pix = mChart.getTransformer(YAxis.AxisDependency.LEFT).getPixelForValues(targetX, maxY * mAnimator
                .getPhaseY());
        drawHighlightLine(c, (float) pix.x, (float) pix.y);

        // 绘制高亮点
        for (int i = 0; i < entryList.size(); i++) {
            MPPointD tmpPix = mChart.getTransformer(YAxis.AxisDependency.LEFT).getPixelForValues(targetX,
                    (float) ((entryList.get(i).getY() - DotRadius) * mAnimator.getPhaseY()));
            mDotPaint.setColor(colorList.get(i));

            c.drawCircle((float) tmpPix.x, (float) tmpPix.y, DotRadius, mDotPaint);

            mDotPaint.setColor(Color.WHITE);
            c.drawCircle((float) tmpPix.x, (float) tmpPix.y, DotRadius - 3, mDotPaint);
        }

    }

    private void drawHighlightLine(Canvas c, float x, float y) {
        // set color and stroke-width
        mHighlightPaint.setColor(Color.RED);
        mHighlightPaint.setStrokeWidth(3);

        // draw highlighted lines (if enabled)
        mHighlightPaint.setPathEffect(mDefaultCustomHighlightPathEffect);

        // draw vertical highlight lines

        // create vertical path
        mCustomHighlightPath.reset();
        mCustomHighlightPath.moveTo(x, y);
        mCustomHighlightPath.lineTo(x, mViewPortHandler.contentBottom());

        c.drawPath(mCustomHighlightPath, mHighlightPaint);

    }
}
