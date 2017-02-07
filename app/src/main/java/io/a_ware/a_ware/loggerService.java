package io.a_ware.a_ware;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TimeUtils;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.Duration;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by rabbi on 11/2/16.
 */

public class loggerService extends Service {

    public boolean suAvailable = false;
    public List<String> suResult = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
    }
    @Override
    public int onStartCommand(Intent intent, int startId, int flags) {

        HandlerThread handlerThread = new HandlerThread("HandlerThreadName");
        // Starts the background thread
        handlerThread.start();
        // Create a handler attached to the HandlerThread's Looper
        Handler mHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // Process received messages here!
            }
        };

        // Execute the specified code on the worker thread
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                suAvailable = Shell.SU.available();

                if(suAvailable){

                }
                //final String [] permList= new String[]{"COARSE_LOCATION", "FINE_LOCATION", "GPS", "VIBRATE", "READ_CALL_LOG", "READ_CALENDAR", "WIFI_SCAN", "POST_NOTIFICATION", "NEIGHBORING_CELLS", "READ_SMS", "READ_CLIPBOARD", "TAKE_AUDIO_FOCUS", "WAKE_LOCK", "MONITOR_LOCATION", "MONITOR_HIGH_POWER_LOCATION", "TOAST_WINDOW", "READ_EXTERNAL_STORAGE", "WRITE_EXTERNAL_STORAGE", "GET_ACCOUNTS", "RUN_IN_BACKGROUND", "BOOT_COMPLETED", "READ_CONTACTS"};
                TinyDB tinydb = new TinyDB(getApplicationContext());
                final String totalLog = tinydb.getString("TotalLog");
                Log.d("totalLog", totalLog);
                JSONArray loggedArray = null;
                try {
                    loggedArray = new JSONArray(totalLog);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final ArrayList<String> appList = tinydb.getListString("AwareAppList");
                for (String appName : appList){
                    Log.d("nowApp", appName);
                    suResult = Shell.SU.run("appops get " +  appName);
                    for(String line : suResult) {
                        if (line.contains("ago")){


                        String permName = line.substring(0, line.indexOf(':'));
                        String timeString = line.substring(line.indexOf("+"), line.indexOf("ago"));
                        Date DateString = new Date(System.currentTimeMillis() - getMilliSecFromString(timeString.substring(0, timeString.length() - 1)));
                        JSONObject loggedObj = new JSONObject();

                        try {
                            loggedObj.put("Package", appName);
                            loggedObj.put("Permission", permName);
                            loggedObj.put("Timestamp", DateString);
                            if (loggedArray != null && !(loggedArray.toString().contains(loggedObj.toString()))) {
                                loggedArray.put(loggedObj);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        }

                    }
                }
                if (loggedArray != null) {
                    tinydb.putString("TotalLog", loggedArray.toString());
                }
                try {
                    File myFile = new File(Environment.getExternalStorageDirectory().getPath()+"/AwareLog.json");
                    myFile.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(myFile);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.write(tinydb.getString("TotalLog"));
                    myOutWriter.close();
                    fOut.close();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });




        return Service.START_STICKY;
    }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
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
