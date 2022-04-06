package com.igen.solar.newversion.view;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.igen.solar.newversion.render.IGENLineChartRenderer;
import com.igen.solar.newversion.render.Time24HXAxisRenderer;

public class IGenLineChart extends LineChart {
    public IGenLineChart(Context context) {
        super(context);
    }

    public IGenLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IGenLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new IGENLineChartRenderer(this, mAnimator, mViewPortHandler);
        mXAxisRenderer = new Time24HXAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer);
    }
}
