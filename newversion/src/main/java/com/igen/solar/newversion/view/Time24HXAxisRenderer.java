package com.igen.solar.newversion.view;


import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * 定制X轴，显示24小时的时间，同时缩放是，时间轴显示会根据时段等分
 */
public class Time24HXAxisRenderer extends XAxisRenderer {
    public Time24HXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(24 * 60 * 60);
    }

    @Override
    protected void computeAxisValues(float min, float max) {
//        super.computeAxisValues(min, max);
        computeAxisValuesFor24Hours(min, max);

        computeSize();
    }

    private void computeAxisValuesFor24Hours(float min, float max) {
        // 由于是以秒为单位的时间戳进行处理，
        int xMin = (int) min;
        int xMax = (int) max;

        int labelCount = mAxis.getLabelCount();
        double range = Math.abs(xMax - xMin);

        if (labelCount == 0 || range <= 0 || Double.isInfinite(range)) {
            mAxis.mEntries = new float[]{};
            mAxis.mCenteredEntries = new float[]{};
            mAxis.mEntryCount = 0;
            return;
        }

        TimeIntervalType timeIntervalType = calcSuitableTimeIntervalType(range);
        double interval = timeIntervalType.timeInterval;

        // If granularity is enabled, then do not allow the interval to go below specified granularity.
        // This is used to avoid repeated values when rounding values for display.
        if (mAxis.isGranularityEnabled())
            interval = interval < mAxis.getGranularity() ? mAxis.getGranularity() : interval;

        // Normalize interval
//        double intervalMagnitude = Utils.roundToNextSignificant(Math.pow(10, (int) Math.log10(interval)));
//        int intervalSigDigit = (int) (interval / intervalMagnitude);
//        if (intervalSigDigit > 5) {
//            // Use one order of magnitude higher, to avoid intervals like 0.9 or 90
//            // if it's 0.0 after floor(), we use the old value
//            interval = Math.floor(10.0 * intervalMagnitude) == 0.0
//                    ? interval
//                    : Math.floor(10.0 * intervalMagnitude);
//
//        }

        int n = mAxis.isCenterAxisLabelsEnabled() ? 1 : 0;

        double first;
        if ((xMin % interval) == 0) {
            first = xMin;
        } else {
            first = ((int) (xMin / interval) + 1) * interval;
        }

        double tmpStart = first;
        while (tmpStart < xMax) {
            tmpStart += interval;
            n++;
        }
        // 绘制边界数据
        if (tmpStart == xMax) {
            n++;
        }
        mAxis.mEntryCount = n;
        if (mAxis.mEntries.length < n) {
            // Ensure stops contains at least numStops elements.
            mAxis.mEntries = new float[n];
        }

        for (int i = 0; i < n; ++i) {
            mAxis.mEntries[i] = (float) (first + i * interval);
        }

        // set decimals
        if (interval < 1) {
            mAxis.mDecimals = (int) Math.ceil(-Math.log10(interval));
        } else {
            mAxis.mDecimals = 0;
        }

        if (mAxis.isCenterAxisLabelsEnabled()) {

            if (mAxis.mCenteredEntries.length < n) {
                mAxis.mCenteredEntries = new float[n];
            }

            float offset = (float) interval / 2f;

            for (int i = 0; i < n; i++) {
                mAxis.mCenteredEntries[i] = mAxis.mEntries[i] + offset;
            }
        }
    }

    /**
     * 计算最合适的时间间隔类型
     *
     * @return TimeIntervalType
     */
    private TimeIntervalType calcSuitableTimeIntervalType(double range) {
        int[] countArray = new int[TimeIntervalType.values().length];
        int minCountDistance = Integer.MAX_VALUE;
        int minIndex = 0;
        for (int i = 0; i < countArray.length; i++) {
            int tmpLabelCount = (int) (range / TimeIntervalType.values()[i].timeInterval);
            int tmpDistance = Math.abs(tmpLabelCount - mAxis.getLabelCount());
            if (tmpDistance < minCountDistance) {
                minCountDistance = tmpDistance;
                minIndex = i;
            }
        }
        return TimeIntervalType.values()[minIndex];
    }

    private enum TimeIntervalType {
        THREE_HOURS(3 * 60 * 60),
        TWO_HOURS(2 * 60 * 60),
        ONE_HOUR(60 * 60),
        THREE_QUARTERS(45 * 60),
        HALF_HOUR(30 * 60),
        ONE_QUARTERS(15 * 60);

        // 事件间隔，以 秒 为单位
        private final long timeInterval;

        TimeIntervalType(long timeInterval) {
            this.timeInterval = timeInterval;
        }
    }
}
