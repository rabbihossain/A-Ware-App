package io.a_ware.a_ware;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class logger extends AppCompatActivity {
    private class Startup extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog = null;
        private Context context = null;
        private boolean suAvailable = false;
        private String suVersion = null;
        private String suVersionInternal = null;
        private List<String> suResult = null;

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
            dialog.setTitle("Some title");
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
                        "logcat -d"
                });
            }

            // This is just so you see we had a progress dialog,
            // don't do this in production code
            try { Thread.sleep(5000); } catch(Exception e) { }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();

            // output
            StringBuilder sb = (new StringBuilder()).
                    append("Root? ").append(suAvailable ? "Yes" : "No").append((char)10).
                    append("Version: ").append(suVersion == null ? "N/A" : suVersion).append((char)10).
                    append("Version (internal): ").append(suVersionInternal == null ? "N/A" : suVersionInternal).append((char)10).
                    append((char)10);
            if (suResult != null) {
                for (String line : suResult) {
                    sb.append(line).append((char)10);
                }
            }
            ((TextView)findViewById(R.id.logView)).setText(sb.toString());
            ((TextView)findViewById(R.id.logView)).setMovementMethod(new ScrollingMovementMethod());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logger);

        // refresh button
        ((Button)findViewById(R.id.buttonLogger)).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        (new Startup()).setContext(v.getContext()).execute();
                    }
                });

        ((Button)findViewById(R.id.copyLog)).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String currenttext = ((TextView)findViewById(R.id.logView)).getText().toString();

                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        clipboard.setText(currenttext);
                    }
                });

        // Let's do some background stuff
        (new Startup()).setContext(this).execute();
    }
}
