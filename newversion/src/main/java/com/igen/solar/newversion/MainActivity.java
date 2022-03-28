package com.igen.solar.newversion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.igen.solar.newversion.bean.PowerData;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LineChart chart;

    private long ZeroTimestamp = 0;

    private long yMax;
    private long yMin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("折线图Demo");

        initCharts();

        String[] fileList = {"data_03_20.json", "data_03_21.json", "data_03_23.json"};
        List<List<PowerData>> dataResult = readData(fileList);

        // 时间同步
        for (int i = 0; i < dataResult.size(); i++) {
            if (dataResult.get(i) != null) {
                for (PowerData pd : dataResult.get(i)) {
                    if(i < 2) {
                        pd.setDateTime(pd.getDateTime() + (3 - i) * 86400L);
                    }else{
                        pd.setDateTime(pd.getDateTime() + (2 - i) * 86400L);
                    }
                }
            }
        }

        // 过滤空值结果
        for (int i = 0; i < dataResult.size(); i++) {
            if (dataResult.get(i) != null) {
                for (int j = dataResult.get(i).size() - 1; j >= 0; j--) {
                    if (dataResult.get(i).get(j).getGenerationPower() == null) {
                        dataResult.get(i).remove(j);
                    }
                }
            }
        }

        // 计算Y轴 最大最小值
        yMax = dataResult.get(0).get(0).getGenerationPower();
        yMin = dataResult.get(0).get(0).getGenerationPower();
        for(List<PowerData> tmp : dataResult){
            if(tmp != null){
                for(PowerData pd : tmp){
                    if(pd.getGenerationPower() > yMax){
                        yMax = pd.getGenerationPower();
                    }else if(pd.getGenerationPower() < yMin){
                        yMin = pd.getGenerationPower();
                    }
                }
            }
        }
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0.0f);
        yAxis.setAxisMaximum((float) (yMax * 1.2));

        // 计算当日零点时间戳
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 23);
        ZeroTimestamp = calendar.getTimeInMillis() / 1000;


        // add Data
        String[] colors = {"E93F37", "7AB2FF", "3BBCAD"};


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        for (int z = 0; z < dataResult.size(); z++) {
            dataSets.add(formatData(z, dataResult.get(z), colors[z]));
        }
        // create a data object with the data sets
        LineData data = new LineData(dataSets);

        // set data
        chart.setData(data);

    }

    private void initCharts() {
        {   // // Chart Style // //
            chart = findViewById(R.id.chart1);

            // background color
            chart.setBackgroundColor(Color.WHITE);

            // disable description text
            chart.getDescription().setEnabled(false);

            // enable touch gestures
            chart.setTouchEnabled(true);

            chart.setGridBackgroundColor(Color.WHITE);

            // set listeners
//            chart.setOnChartValueSelectedListener(this);
            chart.setDrawGridBackground(false);

            // create marker to display box when values are selected
//            MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//
//            // Set the marker to the chart
//            mv.setChartView(chart);
//            chart.setMarker(mv);

            // enable scaling and dragging
            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);
            chart.setDoubleTapToZoomEnabled(false);
            // chart.setScaleXEnabled(true);
            // chart.setScaleYEnabled(true);

            // force pinch zoom along both axis
//            chart.setPinchZoom(true);
        }

        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chart.getXAxis();

            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 0f, 0f);
            xAxis.setGridColor(Color.parseColor("#E5E5E8"));

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    int hour = (int) (value / 3600);
                    int leftSeconds = (int) (value % 3600);
                    StringBuilder sb = new StringBuilder();
                    if(hour < 10){
                        sb.append("0");
                    }
                    sb.append(hour + ":");
                    int minute = leftSeconds / 60;
                    int seconds = leftSeconds % 60;
                    if(minute < 10){
                        sb.append("0" + minute);
                    }else{
                        sb.append("" + minute);
                    }

                    Log.e("TAG", "value = " + value + "\n" +
                            "\thour: " + hour + "\n" +
                            "\tleftSeconds： " + leftSeconds + "\n"+
                            "\tminute: " + minute + "\n" +
                            "\tresult: " + sb.toString() + "\n\n ");

                    return sb.toString();
                }
            });
            xAxis.setAxisMinimum(0.0f);
            xAxis.setAxisMaximum(86400.0f);
            xAxis.setGranularity(1800);
            xAxis.setTextColor(Color.parseColor("#92959C"));
            xAxis.setLabelCount(8, false);
        }

        chart.setScaleYEnabled(false);
        chart.setVisibleXRangeMinimum(1800 * 8);

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 0f, 0f);
            yAxis.setGridColor(Color.parseColor("#E5E5E8"));

            // axis range
            yAxis.setAxisMaximum(200f);
            yAxis.setAxisMinimum(-50f);

            yAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return String.valueOf((int)(value / 10000));
                }
            });
            yAxis.setTextColor(Color.parseColor("#92959C"));
        }


        // draw points over time
//        chart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // draw legend entries as lines
        l.setForm(Legend.LegendForm.SQUARE);
        l.setEnabled(false);
    }

    private List<List<PowerData>> readData(String[] fileNameList) {
        List<List<PowerData>> result = new ArrayList<>();
        for (int i = 0; i < fileNameList.length; i++) {
            StringBuilder newStringBuilder = new StringBuilder();
            InputStream inputStream = null;
            try {
                inputStream = getResources().getAssets().open(fileNameList[i]);
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String jsonLine;
                while ((jsonLine = reader.readLine()) != null) {
                    newStringBuilder.append(jsonLine);
                }
                reader.close();
                isr.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!newStringBuilder.toString().isEmpty()) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                List<PowerData> tmp = gson.fromJson(newStringBuilder.toString(), new TypeToken<List<PowerData>>() {
                }.getType());
                result.add(tmp);
            } else {
                result.add(null);
            }
        }

        return result;
    }

    private ILineDataSet formatData(int index, List<PowerData> datas, String rgb) {

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < datas.size(); i++) {
            values.add(new Entry(datas.get(i).getDateTime() - ZeroTimestamp,
                    datas.get(i).getGenerationPower(),
                    getResources().getDrawable(R.drawable.star)));
        }

        LineDataSet set1;


        // create a dataset and give it a type
        set1 = new LineDataSet(values, "DataSetDataSetDataSetDataSet" + (index + 1));

        set1.setDrawIcons(false);

        // draw dashed line
//            set1.enableDashedLine(10f, 5f, 0f);
        set1.disableDashedLine();

        // black lines and points
        set1.setColor(Color.parseColor("#" + rgb));
        set1.setCircleColor(Color.BLACK);
        set1.setHighLightColor(Color.GREEN);


        // line thickness and point size
        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);

        // draw points as solid circles
        set1.setDrawCircleHole(false);
        set1.setDrawCircles(false);
        set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        // customize legend entry
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);

        // text size of values
        set1.setValueTextSize(9f);
        set1.setDrawValues(false);

        // draw selection line as dashed
        set1.enableDashedHighlightLine(10f, 5f, 0f);

        // set the filled area
        set1.setDrawFilled(true);
        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart.getAxisLeft().getAxisMinimum();
            }
        });

        // set color of filled area
        if (Utils.getSDKInt() >= 18) {
            // drawables only supported on api level 18 and above
            GradientDrawable drawable1 = new GradientDrawable();
            drawable1.setShape(GradientDrawable.RECTANGLE);
            int[] colors = {Color.parseColor("#99" + rgb), Color.parseColor("#99" + rgb), Color.parseColor("#33" + rgb)};
            drawable1.setColors(colors);
            drawable1.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            set1.setFillDrawable(drawable1);
        } else {
            set1.setFillColor(Color.BLACK);
        }

        return set1;

    }
}