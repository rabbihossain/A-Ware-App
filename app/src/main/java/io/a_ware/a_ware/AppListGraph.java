package io.a_ware.a_ware;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AppListGraph extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list_graph);

        TinyDB tinydb = new TinyDB(getApplicationContext());

        BarChart permChart = (BarChart) findViewById(R.id.TCAppChart);

        JSONObject fullPermObj = new JSONObject();

        PackageManager packageManager= getApplicationContext().getPackageManager();

        try {
            JSONArray fullArray = new JSONArray(tinydb.getString("TotalLog"));
            for (int i = 1; i < fullArray.length(); i++){
                JSONObject permData = fullArray.getJSONObject(i);
                if(fullPermObj.has(permData.getString("Package"))){
                    fullPermObj.put(permData.getString("Package"), fullPermObj.getInt(permData.getString("Package")) + 1);
                } else {
                    fullPermObj.put(permData.getString("Package"), 1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<BarEntry> entries = new ArrayList<>();
        final ArrayList<String> xLabel = new ArrayList<>();

        for (int i = 0; i < fullPermObj.names().length(); i++){
            try {
                entries.add(new BarEntry(i, fullPermObj.getInt(fullPermObj.names().getString(i))));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                xLabel.add((String)packageManager.getApplicationLabel(packageManager.getApplicationInfo(fullPermObj.names().getString(i), PackageManager.GET_META_DATA)));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (PackageManager.NameNotFoundException e) {
                try {
                    xLabel.add(fullPermObj.names().getString(i));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }


        BarDataSet set = new BarDataSet(entries, "Each App Permission Calls");

        BarData data = new BarData(set);
        XAxis xAxis = permChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(90);
        xAxis.setTextSize(8f);
        xAxis.setGranularity(1f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(fullPermObj.names().length());
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel.get((int)value);
            }
        });
        permChart.setData(data);
        permChart.setVisibleXRangeMaximum(5);
        permChart.setFitBars(true); // make the x-axis fit exactly all bars
        permChart.invalidate();
    }
}
