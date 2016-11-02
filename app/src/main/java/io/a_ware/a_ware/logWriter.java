package io.a_ware.a_ware;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
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
import java.util.List;

/**
 * Created by rabbi on 11/3/16.
 */

public class logWriter extends Service {

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

    }
    @Override
    public int onStartCommand(Intent intent, int startId, int flags) {



        FileInputStream is = null;
        BufferedReader reader;
        final File file = new File(Environment.getExternalStorageDirectory().getPath()+"/AwareLogRaw.txt");

        final String [] loggingKeywords = new String[] {"start","camera","location","gps","bluetooth","kill", "sms", "contact", "call", "wifi", "data"};
        final String [] loggingKeywordsExplained = new String[] {"App Launched", "Camera", "Geolocation", "Geolocation", "Bluetooth", "App Closed", "SMS", "Contacts", "Phone Call", "Wifi Access", "Data Service"};

        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        final JSONArray loggedArray = new JSONArray();
        if (file.exists()) {
            try {
                is = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(line != null){

                for (ApplicationInfo packageInfo : packages) {
                    String logline = line.toLowerCase();
                    if(pm.getLaunchIntentForPackage(packageInfo.packageName)!= null &&

                            !pm.getLaunchIntentForPackage(packageInfo.packageName).equals("") && logline.contains(packageInfo.packageName))


                    {
                        try {
                            ApplicationInfo app = this.getPackageManager().getApplicationInfo(packageInfo.packageName, 0);
                            String appName = (String) pm.getApplicationLabel(app);

                            for (int i = 0; i < loggingKeywords.length; i++) {
                                if(logline.contains(loggingKeywords[i])){
                                    JSONObject loggedObj = new JSONObject();
                                    loggedObj.put("AppName", appName);
                                    loggedObj.put("PackageName", packageInfo.packageName);
                                    loggedObj.put("PermName", loggingKeywordsExplained[i]);
                                    loggedObj.put("Timestamp",line.substring(0,20));

                                    loggedArray.put(loggedObj);
                                }
                            }

                        } catch (PackageManager.NameNotFoundException e) {
                            Log.d("fuck", "no name found for this package");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }

                //Log.d("StackOverflow", line);
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //Log.d("WhileFinish", loggedArray.toString());

            try {
                File myFile = new File(Environment.getExternalStorageDirectory().getPath()+"/AwareLog.json");
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.write(loggedArray.toString());
                myOutWriter.close();
                fOut.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return Service.START_STICKY;
    }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

}
