package io.a_ware.a_ware;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Objects;

import eu.chainfire.libsuperuser.Shell;

public class CustomArrayListAdapterForAppDetail extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> itemname;
    private final ArrayList<String> itemdetail;
    private final String appName;
    private final ArrayList<String> allowdeny;

    public CustomArrayListAdapterForAppDetail(Activity context, ArrayList<String> itemname, ArrayList<String> itemdetail, String appName, ArrayList<String> allowdeny) {
        super(context, R.layout.list_view_app_detail, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.itemdetail=itemdetail;
        this.appName=appName;
        this.allowdeny=allowdeny;

    }



    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_view_app_detail, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.Itemname);
        TextView extratxt = (TextView) rowView.findViewById(R.id.Itemdetail);

        txtTitle.setText(itemname.get(position));
        extratxt.setText(itemdetail.get(position));

        final ToggleButton tB = (ToggleButton) rowView.findViewById(R.id.permBlocker);

        if (Objects.equals(allowdeny.get(position), "allow")){
            tB.setChecked(false);
        } else {
            tB.setChecked(true);
        }

        tB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(tB.isChecked()){
                    Shell.SU.run("appops set " + appName + " " + itemname.get(position) + " deny");
                }
                else {
                    Shell.SU.run("appops set " + appName + " " + itemname.get(position) + " allow");
                }
            }
        });

        return rowView;

    };


}