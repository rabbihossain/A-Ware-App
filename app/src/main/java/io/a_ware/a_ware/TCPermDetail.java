package io.a_ware.a_ware;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

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
import java.util.Objects;

public class TCPermDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcperm_detail);

        Bundle bundle = getIntent().getExtras();
        final String permCodename = bundle.getString("permIds");
        String permName = bundle.getString("permName");

        TextView textview1 = (TextView) findViewById (R.id.permDetailName);
        TextView textview2 = (TextView) findViewById (R.id.permShortName);
        final TextView seekBarValue = (TextView) findViewById (R.id.seekBarValue);

        textview1.setText(permName);
        textview2.setText(permCodename);

        final SharedPreferences preferences = getSharedPreferences("PERMISSION_VALUES_STORAGE", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        seekBarValue.setText("" + preferences.getInt(permCodename, 0));
        final SeekBar sk=(SeekBar) findViewById(R.id.seekBar);

        sk.setProgress(preferences.getInt(permCodename, 0));

        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                seekBarValue.setText("" + progress);
                editor.putInt(permCodename, progress);
                editor.apply();
            }
        });

        TinyDB tinydb = new TinyDB(getApplicationContext());

        BarChart permChart = (BarChart) findViewById(R.id.TCPermDetailChart);

        JSONObject fullPermObj = new JSONObject();

        try {
            JSONArray fullArray = new JSONArray(tinydb.getString("TotalLog"));
            for (int i = 1; i < fullArray.length(); i++){
                JSONObject permData = fullArray.getJSONObject(i);
                if (Objects.equals(permName, permData.getString("Permission"))){
                    if(fullPermObj.has(permData.getString("Package"))){
                        fullPermObj.put(permData.getString("Package"), fullPermObj.getInt(permData.getString("Package")) + 1);
                    } else {
                        fullPermObj.put(permData.getString("Package"), 1);
                    }
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
                xLabel.add(fullPermObj.names().getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        BarDataSet set = new BarDataSet(entries, permName + " Usage by Different Apps");

        BarData data = new BarData(set);
        XAxis xAxis = permChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
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
