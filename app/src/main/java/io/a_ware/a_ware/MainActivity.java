package io.a_ware.a_ware;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences preferences = getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        TinyDB tinydb = new TinyDB(getApplicationContext());
        JSONArray loggedArray = new JSONArray();

        if(preferences.getString("firstRun", "true") == "true"){
            editor.putString("firstRun", "false");
            JSONObject loggedObj = new JSONObject();

            try {
                loggedObj.put("Package", "init");
                loggedObj.put("Permission", "init");
                loggedObj.put("Timestamp", "init");
                loggedArray.put(loggedObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tinydb.putString("TotalLog", loggedArray.toString());

        }

        Intent ishintent = new Intent(this, TCService.class);
        startService(ishintent);
        PendingIntent pintent = PendingIntent.getService(this, 0, ishintent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pintent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pintent);


        Intent logintent = new Intent(this, loggerService.class);
        startService(logintent);
        PendingIntent pintent2 = PendingIntent.getService(this, 0, logintent, 0);
        AlarmManager alarm2 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm2.cancel(pintent2);
        alarm2.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 180000, pintent2);



    }

    public void imClicked(View view) {
        Intent intent = new Intent(this, permActivity.class);
        startActivity(intent);
    }

    public void mockClicked(View view) {
        Intent mocklocintent = new Intent(this, MockLocation.class);
        startActivity(mocklocintent);
    }

    public void rppClicked(View view) {
        Intent rppIntent = new Intent(this, RPP_Main.class);
        startActivity(rppIntent);
    }

    public void logClicked(View view) throws IOException, JSONException {
        Toast.makeText(this, "Nothing Here...", Toast.LENGTH_LONG).show();

    }





}
