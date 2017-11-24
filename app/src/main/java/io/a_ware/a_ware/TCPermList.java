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
    public void permGraphOpen(View view) {
        Intent graphintent = new Intent(this, PermListGraph.class);
        startActivity(graphintent);
    }
}
