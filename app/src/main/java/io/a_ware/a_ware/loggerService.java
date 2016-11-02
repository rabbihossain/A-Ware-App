package io.a_ware.a_ware;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

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


        suAvailable = Shell.SU.available();

        if(suAvailable){
            suResult = Shell.SU.run(new String[] {
                    "logcat -d  | grep 'ActivityManager' "
            });
        }

        try {
            File myFile = new File(Environment.getExternalStorageDirectory().getPath()+"/AwareLogRaw.txt");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            final PackageManager pm = getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            if (suResult != null) {
                for (String line : suResult) {
                    for (ApplicationInfo packageInfo : packages){
                        if (line.toLowerCase().contains(packageInfo.packageName)){
                            myOutWriter.append(line);
                            myOutWriter.append(System.getProperty("line.separator"));
                        }
                    }

                }
            }

            myOutWriter.close();
            fOut.close();



        } catch (Exception e) {
            e.printStackTrace();
        }
        Shell.SU.run(new String[] {
                "logcat -c"
        });
        return Service.START_STICKY;
    }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

}
