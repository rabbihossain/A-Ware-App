package io.a_ware.a_ware;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import eu.chainfire.libsuperuser.Shell;

import static android.R.attr.value;

public class logger extends AppCompatActivity {

    final ArrayList<String> itemname =new ArrayList<String>();
    final ArrayList<String> itemdetail = new ArrayList<String>();
    final Activity activity = this;
    final String [] loggingKeywords = new String[] {"start","camera","location","gps","bluetooth","kill", "sms", "contact", "call", "wifi", "data"};
    final String [] loggingKeywordsExplained = new String[] {"App Launched", "Camera", "Geolocation", "Geolocation", "Bluetooth", "App Closed", "SMS", "Contacts", "Phone Call", "Wifi Access", "Data Service"};


    private class Startup extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog = null;
        private Context context = null;
        private boolean suAvailable = false;
        private String suVersion = null;
        private String suVersionInternal = null;
        private List<String> suResult = null;



        Bundle bundle = getIntent().getExtras();
        String appPackageName = bundle.getString("pkgname");

        public Startup setContext(Context context) {
            this.context = context;
            return this;
        }

        @Override
        protected void onPreExecute() {
            // We're creating a progress dialog here because we want the user to wait.
            // If in your app your user can just continue on with clicking other things,
            // don't do the dialog thing.

            dialog = new ProgressDialog(context);
            dialog.setTitle("Loading Contents");
            dialog.setMessage("Doing something interesting ...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Let's do some SU stuff
            suAvailable = Shell.SU.available();
            if (suAvailable) {
                suVersion = Shell.SU.version(false);
                suVersionInternal = Shell.SU.version(true);
                suResult = Shell.SU.run(new String[] {
                        "appops get " + appPackageName
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();

            // output
            StringBuilder sb = new StringBuilder();
            if (suResult != null) {
                for (String line : suResult) {
                    sb.append(line).append((char)10);
                    sb.append(line).append(System.getProperty("line.separator"));
                    Log.d("logcatmsg", line);

                    if(line.toLowerCase().contains("time")){
                        itemname.add(line.substring(0, line.indexOf(':')));

                        String timeString = line.substring(line.indexOf("+"), line.indexOf("ago"));
                        Date DateString = new Date(System.currentTimeMillis() - getMilliSecFromString(timeString.substring(0, timeString.length() - 1)));

                        itemdetail.add("Last Usage: " + String.valueOf(DateString));

                    }

                }
            }
            ListView listView = (ListView) findViewById(R.id.loggerList);

            CustomArrayListAdapterForPerm adapter=new CustomArrayListAdapterForPerm(activity, itemname, itemdetail);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logger);

       (new Startup()).setContext(this).execute();

        Bundle bundle = getIntent().getExtras();
        String appPackageName = bundle.getString("pkgname");

        TinyDB tinydb = new TinyDB(getApplicationContext());

        BarChart permChart = (BarChart) findViewById(R.id.TCAppDetailChart);

        JSONObject fullPermObj = new JSONObject();

        try {
            JSONArray fullArray = new JSONArray(tinydb.getString("TotalLog"));
            for (int i = 1; i < fullArray.length(); i++){
                JSONObject permData = fullArray.getJSONObject(i);
                if (Objects.equals(appPackageName, permData.getString("Package"))){
                    if(fullPermObj.has(permData.getString("Permission"))){
                        fullPermObj.put(permData.getString("Permission"), fullPermObj.getInt(permData.getString("Permission")) + 1);
                    } else {
                        fullPermObj.put(permData.getString("Permission"), 1);
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


        BarDataSet set = new BarDataSet(entries, "Permission Calls By " + appPackageName);

        BarData data = new BarData(set);
        XAxis xAxis = permChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setLabelRotationAngle(90);
        xAxis.setTextSize(5f);
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

    private long getMilliSecFromString(String value) {

        String[] tokens = value.split("\\+|d|h|m|s|ms");
        Collections.reverse(Arrays.asList(tokens));
        TimeModel timeModel = null;
        if (tokens.length == 5) {
            timeModel = new TimeModel.TimeModelBuilder()
                    .setMilliSecond(tokens[0])
                    .setSecond(tokens[1])
                    .setMinute(tokens[2])
                    .setHour(tokens[3])
                    .setDay(tokens[4])
                    .build();
        } else if (tokens.length == 4) {
            timeModel = new TimeModel.TimeModelBuilder()
                    .setMilliSecond(tokens[0])
                    .setSecond(tokens[1])
                    .setMinute(tokens[2])
                    .setHour(tokens[3])
                    .build();
        } else if (tokens.length == 3) {
            timeModel = new TimeModel.TimeModelBuilder()
                    .setMilliSecond(tokens[0])
                    .setSecond(tokens[1])
                    .setMinute(tokens[2])
                    .build();
        } else if (tokens.length == 2) {
            timeModel = new TimeModel.TimeModelBuilder()
                    .setMilliSecond(tokens[0])
                    .setSecond(tokens[1])
                    .build();
        } else {
            timeModel = new TimeModel.TimeModelBuilder()
                    .setMilliSecond(tokens[0])
                    .build();
        }


        return timeModel.getTotalTimeInMillisecond();
    }
}
