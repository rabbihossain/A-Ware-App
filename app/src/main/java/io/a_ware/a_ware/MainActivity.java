package io.a_ware.a_ware;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
            editor.apply();
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

        final ArrayList<String> appNames = new ArrayList<String>();

        final PackageManager pm = getPackageManager();

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {

            if(pm.getLaunchIntentForPackage(packageInfo.packageName)!= null &&

                    !pm.getLaunchIntentForPackage(packageInfo.packageName).equals(""))


            {
                appNames.add(packageInfo.packageName);
            }
        }
        tinydb.putListString("AwareAppList", appNames);
        Log.d("AppList", tinydb.getListString("AwareAppList").toString());

        //Intent ishintent = new Intent(this, TCService.class);
        //startService(ishintent);
        //PendingIntent pintent = PendingIntent.getService(this, 0, ishintent, 0);
        //AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //alarm.cancel(pintent);
        //alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pintent);


        Intent logintent = new Intent(this, loggerService.class);
        startService(logintent);
        PendingIntent pintent2 = PendingIntent.getService(this, 0, logintent, 0);
        AlarmManager alarm2 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm2.cancel(pintent2);
        alarm2.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 250000, pintent2);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("reqperm","Permission is granted");
            } else {

                Log.v("reqperm","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("reqperm","Permission is granted");
        }

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
        Toast.makeText(this, "Wait a bit...", Toast.LENGTH_LONG).show();
        TinyDB tinydb = new TinyDB(getApplicationContext());

        try {
            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            File myFile = new File(Environment.getExternalStorageDirectory().getPath()+"/AwareLog-" + date + ".json");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.write(tinydb.getString("TotalLog"));
            myOutWriter.close();
            fOut.close();
            Toast.makeText(this, "Log file generated. Filename - AwareLog-" + date + ".json", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
