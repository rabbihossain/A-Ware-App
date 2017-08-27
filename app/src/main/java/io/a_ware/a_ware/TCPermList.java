package io.a_ware.a_ware;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TCPermList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcperm_list);

        final ArrayList<String> itemname =new ArrayList<String>();
        final ArrayList<String> itemdetail = new ArrayList<String>();

        TinyDB tinydb = new TinyDB(getApplicationContext());

        String [] permNamesDetail = new String[] {"VIBRATE", "READ_CONTACTS", "WRITE_CONTACTS", "POST_NOTIFICATION", "CALL_PHONE", "WRITE_SMS", "CAMERA", "RECORD_AUDIO", "TAKE_AUDIO_FOCUS", "WAKE_LOCK", "OP_READ_PHONE_STATE", "READ_EXTERNAL_STORAGE", "WRITE_EXTERNAL_STORAGE", "TURN_ON_SCREEN", "GET_ACCOUNTS", "BOOT_COMPLETED", "TOAST_WINDOW", "COARSE_LOCATION", "FINE_LOCATION", "GPS", "WIFI_SCAN", "MONITOR_LOCATION", "MONITOR_HIGH_POWER_LOCATION", "READ_CALL_LOG", "READ_SMS", "RECEIVE_SMS", "READ_ICC_SMS", "SYSTEM_ALERT_WINDOW", "AUDIO_RING_VOLUME", "USE_FINGERPRINT", "WRITE_CALL_LOG", "ADD_VOICEMAIL", "PROCESS_OUTGOING_CALLS", "WRITE_CLIPBOARD", "READ_CALENDAR", "WRITE_CALENDAR", "NEIGHBORING_CELLS", "READ_CLIPBOARD", "AUDIO_ALARM_VOLUME", "MUTE_MICROPHONE", "AUDIO_VOICE_VOLUME", "AUDIO_MEDIA_VOLUME", "WIFI_CHANGE"};
        String [] permNamesReadable = new String[] {"VIBRATE", "READ_CONTACTS", "WRITE_CONTACTS", "POST_NOTIFICATION", "CALL_PHONE", "WRITE_SMS", "CAMERA", "RECORD_AUDIO", "TAKE_AUDIO_FOCUS", "WAKE_LOCK", "OP_READ_PHONE_STATE", "READ_EXTERNAL_STORAGE", "WRITE_EXTERNAL_STORAGE", "TURN_ON_SCREEN", "GET_ACCOUNTS", "BOOT_COMPLETED", "TOAST_WINDOW", "COARSE_LOCATION", "FINE_LOCATION", "GPS", "WIFI_SCAN", "MONITOR_LOCATION", "MONITOR_HIGH_POWER_LOCATION", "READ_CALL_LOG", "READ_SMS", "RECEIVE_SMS", "READ_ICC_SMS", "SYSTEM_ALERT_WINDOW", "AUDIO_RING_VOLUME", "USE_FINGERPRINT", "WRITE_CALL_LOG", "ADD_VOICEMAIL", "PROCESS_OUTGOING_CALLS", "WRITE_CLIPBOARD", "READ_CALENDAR", "WRITE_CALENDAR", "NEIGHBORING_CELLS", "READ_CLIPBOARD", "AUDIO_ALARM_VOLUME", "MUTE_MICROPHONE", "AUDIO_VOICE_VOLUME", "AUDIO_MEDIA_VOLUME", "WIFI_CHANGE"};

        itemname.addAll(Arrays.asList(permNamesReadable));
        itemdetail.addAll(Arrays.asList(permNamesDetail));

        ListView listView = (ListView) findViewById(R.id.TCPermListContainer);

        CustomArrayListAdapterForPerm adapter=new CustomArrayListAdapterForPerm(this, itemname, itemdetail);
        listView.setAdapter(adapter);

        BarChart permChart = (BarChart) findViewById(R.id.TCPermChart);

        JSONObject fullPermObj = new JSONObject();

        try {
            JSONArray fullArray = new JSONArray(tinydb.getString("TotalLog"));
            for (int i = 1; i < fullArray.length(); i++){
             JSONObject permData = fullArray.getJSONObject(i);
                if(fullPermObj.has(permData.getString("Permission"))){
                    fullPermObj.put(permData.getString("Permission"), fullPermObj.getInt(permData.getString("Permission")) + 1);
                } else {
                    fullPermObj.put(permData.getString("Permission"), 1);
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


        BarDataSet set = new BarDataSet(entries, "Permission Calls By Different Apps");

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                String Selecteditem1 = itemdetail.get(+position);
                String Selecteditem2 = itemname.get(+position);
                Intent intent = new Intent(TCPermList.this, TCPermDetail.class);
                intent.putExtra("permIds", Selecteditem1);
                intent.putExtra("permName", Selecteditem2);
                startActivity(intent);

            }
        });

    }
}
