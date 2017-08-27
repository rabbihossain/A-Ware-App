package io.a_ware.a_ware;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

public class TCAppList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcapp_list);

        final ArrayList<String> itemname =new ArrayList<String>();
        final ArrayList<String> itemdetail = new ArrayList<String>();
        ArrayList<Drawable> imgid =new ArrayList<Drawable>();

        ListView listView = (ListView) findViewById(R.id.TCAppListContainer);
        TinyDB tinydb = new TinyDB(getApplicationContext());

        CustomArrayListAdapter adapter=new CustomArrayListAdapter(this, itemname, itemdetail, imgid);
        listView.setAdapter(adapter);

        final PackageManager pm = getPackageManager();

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {

            if(pm.getLaunchIntentForPackage(packageInfo.packageName)!= null &&

                    !pm.getLaunchIntentForPackage(packageInfo.packageName).equals(""))


            {
                try {
                    ApplicationInfo app = this.getPackageManager().getApplicationInfo(packageInfo.packageName, 0);
                    Drawable icon = pm.getApplicationIcon(app);
                    String name = (String) pm.getApplicationLabel(app);
                    itemname.add(name);
                    imgid.add(icon);

                } catch (PackageManager.NameNotFoundException e) {
                    Log.d("fuck", "no name found for this package");
                }

                itemdetail.add(packageInfo.packageName);
            }
        }
        /**
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                String Selecteditem = itemdetail.get(+position);

                Intent intent = new Intent(TCAppList.this, TCAppDetail.class);
                intent.putExtra("pkgname", Selecteditem);
                startActivity(intent);

            }
        });
        */

        BarChart permChart = (BarChart) findViewById(R.id.TCAppChart);

        JSONObject fullPermObj = new JSONObject();

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
                xLabel.add(fullPermObj.names().getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        BarDataSet set = new BarDataSet(entries, "Each App Permission Calls");

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                String Selecteditem = itemdetail.get(+position);

                Intent intent = new Intent(TCAppList.this, logger.class);
                intent.putExtra("pkgname", Selecteditem);
                startActivity(intent);
            }
        });

        /**
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parentView, View childView, int position, long id) {
                String Selecteditem = itemdetail.get(+position);

                Intent intent = new Intent(TCAppList.this, logger.class);
                intent.putExtra("pkgname", Selecteditem);
                startActivity(intent);
                return true;
            }
        });
         */
    }
}
