package com.igen.solar.newversion.render;

import android.graphics.Canvas;
import android.graphics.Color;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class IGENBarChartRenderer extends BarChartRenderer {

    public IGENBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        BarData barData = mChart.getBarData();

        for (Highlight high : indices) {
            IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            BarEntry e = set.getEntryForXValue(high.getX(), high.getY());

            if (!isInBoundsX(e, set))
                continue;

            Transformer trans = mChart.getTransformer(set.getAxisDependency());

            float left = (float) Math.floor(high.getX());
            float right = (float) Math.ceil(high.getX());
            float top = 0.0f;
            float bottom = 0.0f;

            mBarRect.set(left, top, right, bottom);

            trans.rectToPixelPhase(mBarRect, mAnimator.getPhaseY());
            mBarRect.top = mViewPortHandler.contentTop();

            setHighlightDrawPos(high, mBarRect);
            mHighlightPaint.setColor(Color.CYAN);

            c.drawRect(mBarRect, mHighlightPaint);
        }
    }
}
